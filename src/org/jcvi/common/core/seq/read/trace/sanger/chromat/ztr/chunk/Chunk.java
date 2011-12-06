/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.chunk;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.IOUtil.ReadResults;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.TraceEncoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChannelGroup;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRUtil;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data.DataFactory;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data.RawData;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

/**
 * The Chunk is the basic unit of the ZTR Structure.
 * Each Chunk consists of a type, some metadata and then the data.
 * @author dkatzel
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public enum Chunk {
    
    
    /**
     * The <code>BASE</code> Chunk contains the actual base calls
     * for this Chromatogram.
     * @author dkatzel
     */
    BASE{
        @Override
        protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
                throws TraceDecoderException {
            //first byte is padding
            final int numberOfBases = unEncodedData.length -1;
            ByteBuffer buf = ByteBuffer.allocate(numberOfBases);
            buf.put(unEncodedData, 1, numberOfBases);
            builder.basecalls(new String(buf.array(),IOUtil.UTF_8));

        }

        @Override
        protected String parseData(byte[] unEncodedData,
                ChromatogramFileVisitor visitor,String ignored) throws TraceDecoderException {
          //first byte is padding
            final int numberOfBases = unEncodedData.length -1;
            ByteBuffer buf = ByteBuffer.allocate(numberOfBases);
            buf.put(unEncodedData, 1, numberOfBases);
            
            final String basecalls = new String(buf.array(),IOUtil.UTF_8);
            visitor.visitBasecalls(basecalls);
            return basecalls;
            
        }

		/* (non-Javadoc)
		 * @see org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk#encodeChunk(org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram)
		 */
		@Override
		public byte[] encodeChunk(Chromatogram ztrChromatogram)
				throws TraceEncoderException {
			String basecalls = Nucleotides.asString(ztrChromatogram.getBasecalls().asList());
			
			ByteBuffer buffer = ByteBuffer.allocate(basecalls.length()+1);
			buffer.put(PADDING_BYTE);
			for(int i=0; i< basecalls.length(); i++){
				buffer.put((byte)basecalls.charAt(i));
			}
			return buffer.array();
		}
        
        
    },
    /**
     * The <code>POSITIONS</code> Chunk contains the positions of the
     * bases (peaks)stored as ints.
     * @author dkatzel
     *
     *
     */
    POSITIONS{
        @Override
        protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
                throws org.jcvi.common.core.seq.read.trace.TraceDecoderException {
            final int numberOfBases = (unEncodedData.length -1)/4;
            ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
            ByteBuffer input = ByteBuffer.wrap(unEncodedData);
            //skip padding
            input.position(4);
            while(input.hasRemaining()){
                peaks.put((short) input.getInt());
            }
            builder.peaks(peaks.array());

        }

        @Override
        protected String parseData(byte[] unEncodedData,
                ChromatogramFileVisitor visitor,String basecalls) throws TraceDecoderException {
            final int numberOfBases = (unEncodedData.length -1)/4;
            ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
            ByteBuffer input = ByteBuffer.wrap(unEncodedData);
            //skip padding
            input.position(4);
            while(input.hasRemaining()){
                peaks.put((short) input.getInt());
            }
            visitor.visitPeaks(peaks.array());
            
            return basecalls;
            
        }

		/* (non-Javadoc)
		 * @see org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk#encodeChunk(org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram)
		 */
		@Override
		public byte[] encodeChunk(Chromatogram ztrChromatogram)
				throws TraceEncoderException {
			short[] peaks =ShortSymbol.toArray(ztrChromatogram.getPeaks().getData().asList());
			ByteBuffer buffer = ByteBuffer.allocate(peaks.length*4+4);
			//raw byte + 3 pads
			buffer.putInt(0);
			for(int i=0; i< peaks.length; i++){
				buffer.putInt(peaks[i]);
			}
			return buffer.array();
		}
        
    },
    /**
     * <code>CLIP</code> contains the suggested quality clip points (0- based).
     * @author dkatzel
     *
     *
     */
    CLIP{
        @Override
        protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
                throws TraceDecoderException {
            if(unEncodedData.length !=9){
                throw new TraceDecoderException("Invalid DefaultClip size, num of bytes = " +unEncodedData.length );
            }
            ByteBuffer buf = ByteBuffer.wrap(unEncodedData);
            buf.position(1); //skip padding
            builder.clip(Range.buildRange(buf.getInt(), buf.getInt()));
        }

        @Override
        protected String parseData(byte[] unEncodedData,
                ChromatogramFileVisitor visitor,String basecalls) throws TraceDecoderException {
            if(unEncodedData.length !=9){
                throw new TraceDecoderException("Invalid DefaultClip size, num of bytes = " +unEncodedData.length );
            }
            ByteBuffer buf = ByteBuffer.wrap(unEncodedData);
            buf.position(1); //skip padding
            Range clipRange = Range.buildRange(buf.getInt(), buf.getInt());
            if(visitor instanceof ZTRChromatogramFileVisitor){
                ((ZTRChromatogramFileVisitor)visitor).visitClipRange(clipRange);
            }
            
            return basecalls;
        }

		/* (non-Javadoc)
		 * @see org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk#encodeChunk(org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram)
		 */
		@Override
		public byte[] encodeChunk(Chromatogram ztrChromatogram)
				throws TraceEncoderException {
		    Range clip=null;
		    if(ztrChromatogram instanceof ZTRChromatogram){
		        clip =((ZTRChromatogram)ztrChromatogram).getClip();
		    }
			
			if(clip ==null){
				//store as 0,0?
				clip = Range.buildRange(0, 0);
			}
			ByteBuffer buffer= ByteBuffer.allocate(9);
			buffer.put(PADDING_BYTE);
			buffer.putInt((int)clip.getStart());
			buffer.putInt((int)clip.getEnd());
			return buffer.array();
		}
        
        
    },
    /**
     * The <code>Confidences</code> Chunk is an implemention of
     * ZTR 1.2 format for {@code CNF4Chunk} which encodes the quality values for
     * all 4 channels.  The format of the data is:
     *  the confidence of the called
     * base followed by the confidences of the uncalled bases.
     * So for a sequence AGT we would store confidences 
     * A1 G2 T3 C1 G1 T1 A2 C2 T2 A3 C3 G3. Any call that is not A, C, G or T
     * is stored as a T.
     *
     * @author dkatzel
     *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
     *
     *
     */
    CONFIDENCES{
    	
    	EnumSet<Nucleotide> notA = EnumSet.of(
				Nucleotide.Cytosine, Nucleotide.Guanine, Nucleotide.Thymine);
		EnumSet<Nucleotide> notC = EnumSet.of(
				Nucleotide.Adenine, Nucleotide.Guanine, Nucleotide.Thymine);
		EnumSet<Nucleotide> notG = EnumSet.of(
				Nucleotide.Adenine, Nucleotide.Cytosine, Nucleotide.Thymine);
		EnumSet<Nucleotide> notACorG = EnumSet.of(
				Nucleotide.Adenine, Nucleotide.Cytosine, Nucleotide.Guanine);
		Map<Nucleotide, Set<Nucleotide>> otherChannelMap = new EnumMap<Nucleotide, Set<Nucleotide>>(Nucleotide.class);
		{
			otherChannelMap.put(Nucleotide.Adenine, notA);
			otherChannelMap.put(Nucleotide.Cytosine, notC);
			otherChannelMap.put(Nucleotide.Guanine, notG);
		}
		
		private Set<Nucleotide> getOtherChannelsThan(Nucleotide channel){
			
			if(otherChannelMap.containsKey(channel)){
				return otherChannelMap.get(channel);
			}
			return notACorG;
			
		}
		
        @Override
        protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
                throws TraceDecoderException {
            String basecalls = builder.basecalls();
            int numberOfBases = basecalls.length();
               
            ByteBuffer aConfidence = ByteBuffer.allocate(numberOfBases);
            ByteBuffer cConfidence = ByteBuffer.allocate(numberOfBases);
            ByteBuffer gConfidence = ByteBuffer.allocate(numberOfBases);
            ByteBuffer tConfidence = ByteBuffer.allocate(numberOfBases);
               
            ByteBuffer calledConfidence = ByteBuffer.wrap(unEncodedData);       
            ByteBuffer unCalledConfidence = calledConfidence.slice();
            //skip padding
            calledConfidence.position(1);
            unCalledConfidence.position(1+numberOfBases);
            populateConfidenceBuffers(basecalls, aConfidence, cConfidence,
                    gConfidence, tConfidence, calledConfidence, unCalledConfidence);
               
            builder.aConfidence(aConfidence.array());
            builder.cConfidence(cConfidence.array());
            builder.gConfidence(gConfidence.array());
            builder.tConfidence(tConfidence.array());

        }

        private void populateConfidenceBuffers(String basecalls,
                ByteBuffer aConfidence, ByteBuffer cConfidence,
                ByteBuffer gConfidence, ByteBuffer tConfidence,
                ByteBuffer calledConfidence, ByteBuffer unCalledConfidence) {
            for (int i = 0; i < basecalls.length(); i++) {
                char currentChar = basecalls.charAt(i);
                populateConfidenceBuffers(currentChar, aConfidence, cConfidence,
                        gConfidence, tConfidence, calledConfidence,
                        unCalledConfidence);
            }
        }

        private void populateConfidenceBuffers(char currentChar,
                ByteBuffer aConfidence, ByteBuffer cConfidence,
                ByteBuffer gConfidence, ByteBuffer tConfidence,
                ByteBuffer calledConfidence, ByteBuffer unCalledConfidence) {
            if(matchesCharacterIgnoringCase(currentChar, 'A')){
                setConfidences(calledConfidence, unCalledConfidence, 
                        aConfidence, Arrays.asList(cConfidence, gConfidence, tConfidence));
            }
            else  if(matchesCharacterIgnoringCase(currentChar, 'C')){
               setConfidences(calledConfidence, unCalledConfidence, 
                       cConfidence, Arrays.asList(aConfidence, gConfidence, tConfidence));
            }
            else  if(matchesCharacterIgnoringCase(currentChar, 'G')){
                   setConfidences(calledConfidence, unCalledConfidence, 
                           gConfidence, Arrays.asList(aConfidence, cConfidence, tConfidence));
            }
            //anything is is considered a "T"
            else{
                   setConfidences(calledConfidence, unCalledConfidence, 
                           tConfidence, Arrays.asList(aConfidence, cConfidence, gConfidence));
            }
        }
        
        private boolean matchesCharacterIgnoringCase(char c, char charToMatch){
            return Character.toLowerCase(c) == charToMatch ||
                Character.toUpperCase(c) == charToMatch;
        }

        private void setConfidences(ByteBuffer calledConfidence, ByteBuffer unCalledConfidence,
                ByteBuffer calledconfidenceChannel, List<ByteBuffer> uncalledConfidenceChannels){
            calledconfidenceChannel.put(calledConfidence.get());
            for(ByteBuffer uncalledBuf : uncalledConfidenceChannels){
                uncalledBuf.put(unCalledConfidence.get());
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected String parseData(byte[] unEncodedData,
                ChromatogramFileVisitor visitor,String basecalls)throws TraceDecoderException {
           
            int numberOfBases = basecalls.length();
            ByteBuffer aConfidence = ByteBuffer.allocate(numberOfBases);
            ByteBuffer cConfidence = ByteBuffer.allocate(numberOfBases);
            ByteBuffer gConfidence = ByteBuffer.allocate(numberOfBases);
            ByteBuffer tConfidence = ByteBuffer.allocate(numberOfBases);
               
            ByteBuffer calledConfidence = ByteBuffer.wrap(unEncodedData);       
            ByteBuffer unCalledConfidence = calledConfidence.slice();
            //skip padding
            calledConfidence.position(1);
            unCalledConfidence.position(1+numberOfBases);
            populateConfidenceBuffers(basecalls, aConfidence, cConfidence,
                    gConfidence, tConfidence, calledConfidence, unCalledConfidence);
            
            visitor.visitAConfidence(aConfidence.array());
            visitor.visitCConfidence(cConfidence.array());
            visitor.visitGConfidence(gConfidence.array());
            visitor.visitTConfidence(tConfidence.array());
            return basecalls;
            
        }

		/* (non-Javadoc)
		 * @see org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk#encodeChunk(org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram)
		 */
		@Override
		public byte[] encodeChunk(Chromatogram ztrChromatogram)
				throws TraceEncoderException {
			
			
			
			
			ChannelGroup channelGroup =ztrChromatogram.getChannelGroup();
			List<Nucleotide> basecalls =ztrChromatogram.getBasecalls().asList();
			ByteBuffer calledBaseConfidences = ByteBuffer.allocate(basecalls.size());
			ByteBuffer otherConfidences = ByteBuffer.allocate(calledBaseConfidences.capacity()*3);
			for(int i=0; i< basecalls.size();i++){
				Nucleotide base = basecalls.get(i);
				calledBaseConfidences.put(channelGroup.getChannel(base).getConfidence().getData()[i]);
				
				for(Nucleotide other: getOtherChannelsThan(base)){
					otherConfidences.put(channelGroup.getChannel(other).getConfidence().getData()[i]);
				}
			}
			ByteBuffer result = ByteBuffer.allocate(basecalls.size()*4+1);
			result.put(PADDING_BYTE);
			result.put(calledBaseConfidences.array());
			result.put(otherConfidences.array());
			return result.array();
		}
        
        
    },
    /**
    * <code>SMP4</code> is the chromatogram scan points for all 4 channels
    * of trace samples.
    * The order of channels is A,C,G,T.  It is assumed that all channels are the
    * same length.
    * @author dkatzel
    */
    SMP4{
        @Override
        public void parseData(byte[] unEncodedData,ZTRChromatogramBuilder builder) throws TraceDecoderException {
        //read first 2 byte is padded bytes?
            
            ShortBuffer buf = ByteBuffer.wrap(unEncodedData).asShortBuffer();
            //skip padding
            buf.position(1);
            int length = buf.capacity()-1;
            int numberOfPositions = length/4;
            ShortBuffer aPositions = ShortBuffer.allocate(numberOfPositions);
            ShortBuffer cPositions = ShortBuffer.allocate(numberOfPositions);
            ShortBuffer gPositions = ShortBuffer.allocate(numberOfPositions);
            ShortBuffer tPositions = ShortBuffer.allocate(numberOfPositions);
            
            populatePositionData(buf, aPositions);
            populatePositionData(buf, cPositions);
            populatePositionData(buf, gPositions);
            populatePositionData(buf, tPositions);
            
              builder.aPositions(aPositions.array());
              builder.cPositions(cPositions.array());
              builder.gPositions(gPositions.array());
              builder.tPositions(tPositions.array());
              
        }


        private void populatePositionData(ShortBuffer buf, ShortBuffer aPositions) {
            for(int i=0; i< aPositions.capacity(); i++){
                aPositions.put(buf.get());
            }
        }


        /**
        * {@inheritDoc}
        */
        @Override
        protected String parseData(byte[] unEncodedData,
                ChromatogramFileVisitor visitor, String basecalls) throws TraceDecoderException {
       //read first 2 byte is padded bytes?
            
            ShortBuffer buf = ByteBuffer.wrap(unEncodedData).asShortBuffer();
            //skip padding
            buf.position(1);
            int length = buf.capacity()-1;
            int numberOfPositions = length/4;
            ShortBuffer aPositions = ShortBuffer.allocate(numberOfPositions);
            ShortBuffer cPositions = ShortBuffer.allocate(numberOfPositions);
            ShortBuffer gPositions = ShortBuffer.allocate(numberOfPositions);
            ShortBuffer tPositions = ShortBuffer.allocate(numberOfPositions);
            
            populatePositionData(buf, aPositions);
            populatePositionData(buf, cPositions);
            populatePositionData(buf, gPositions);
            populatePositionData(buf, tPositions);
            
            visitor.visitAPositions(aPositions.array());
            visitor.visitCPositions(cPositions.array());
            visitor.visitGPositions(gPositions.array());
            visitor.visitTPositions(tPositions.array());
            
            return basecalls;
        }


		/* (non-Javadoc)
		 * @see org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk#encodeChunk(org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram)
		 */
		@Override
		public byte[] encodeChunk(Chromatogram ztrChromatogram)
				throws TraceEncoderException {
			int numTracePositions = ztrChromatogram.getNumberOfTracePositions();
			ChannelGroup channelGroup = ztrChromatogram.getChannelGroup();
			short[] aConfidence= channelGroup.getAChannel().getPositions().array();
			short[] cConfidence= channelGroup.getCChannel().getPositions().array();
			short[] gConfidence= channelGroup.getGChannel().getPositions().array();
			short[] tConfidence= channelGroup.getTChannel().getPositions().array();
			
			ByteBuffer result = ByteBuffer.allocate(8 *numTracePositions+2);
			//first 2 bytes are padding
			result.putShort(PADDING_BYTE);
				for(int i=0; i< numTracePositions; i++){
					result.putShort(aConfidence[i]);
				}
				for(int i=0; i< numTracePositions; i++){
					result.putShort(cConfidence[i]);
				}
				for(int i=0; i< numTracePositions; i++){
					result.putShort(gConfidence[i]);
				}
				for(int i=0; i< numTracePositions; i++){
					result.putShort(tConfidence[i]);
				}
			return result.array();
		}
        
        
    },
    /**
     * Implementation of the ZTR TEXT Chunk.
     * Any information decoded from this chunk will be set as Trace Properties
     * which can be obtained via {@link Trace#getProperties()}.
     * @author dkatzel
     * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
     *
     *
     */
    COMMENTS{
        /**
         * 
        * {@inheritDoc}
         */
        @Override
        protected void parseData(byte[] decodedData, ZTRChromatogramBuilder builder)
                throws TraceDecoderException {
            InputStream in = new ByteArrayInputStream(decodedData);
            builder.properties(parseText(in));
        }

        protected Map<String,String> parseText(InputStream in)
                throws TraceDecoderException {
            Scanner scanner=null;
            //linked hash preserves insertion order
            Map<String,String> textProps = new LinkedHashMap<String, String>();
            try{
                //skip first byte
                in.read();
                scanner = new Scanner(in).useDelimiter("\0+");
                 
                while(scanner.hasNext()){
                    final String key = scanner.next();                
                    final String value = scanner.next();
                    textProps.put(key, value);

                }
                return textProps;
            }
            catch(IOException e){
                throw new TraceDecoderException("error reading text data", e);
            }
            finally{
                if(scanner !=null){
                    scanner.close();
                }
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected String parseData(byte[] decodedData,
                ChromatogramFileVisitor visitor,String basecalls) throws TraceDecoderException {
            InputStream in = new ByteArrayInputStream(decodedData);
            final Map<String,String> comments = parseText(in);
            visitor.visitComments(comments);
            return basecalls;
            
        }

		/* (non-Javadoc)
		 * @see org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk#encodeChunk(org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram)
		 */
		@Override
		public byte[] encodeChunk(Chromatogram ztrChromatogram)
				throws TraceEncoderException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(PADDING_BYTE);
			for(Entry<String, String> entry : ztrChromatogram.getComments().entrySet()){
				try {
					out.write(entry.getKey().getBytes("UTF-8"));
					out.write(PADDING_BYTE);
					out.write(entry.getValue().getBytes("UTF-8"));
					out.write(PADDING_BYTE);
				} catch (UnsupportedEncodingException e) {
					throw new TraceEncoderException("could not convert comment into UTF-8",e);
				} catch (IOException e) {
					throw new TraceEncoderException(String.format("error writing comment key='%s' value ='%s'", entry.getKey(), entry.getValue()),e);
				}
			}
			out.write(PADDING_BYTE);
			byte[] ret= out.toByteArray();
			IOUtil.closeAndIgnoreErrors(out);
			return ret;
		}
        
        
    }
    ;

    
    /**
     * map of all supported chunk implementations mapped by header.
     */
    private static final Map<ChunkType, Chunk> CHUNK_MAP;
    
    private static final byte PADDING_BYTE = (byte)0;
    /**
     * populate chunk_map.
     */
    static{
        Map<ChunkType, Chunk> map = new HashMap<ChunkType, Chunk>();
        map.put(ChunkType.SAMPLES, Chunk.SMP4);
        map.put(ChunkType.BASECALLS, Chunk.BASE);
        map.put(ChunkType.POSITIONS, Chunk.POSITIONS);
        map.put(ChunkType.CONFIDENCE, Chunk.CONFIDENCES);
        map.put(ChunkType.COMMENTS, Chunk.COMMENTS);
        map.put(ChunkType.CLIP, Chunk.CLIP);
        CHUNK_MAP = Collections.unmodifiableMap(map);
    }
    /**
     * get {@link Chunk} by chunk header name.
     * @param chunkHeader the name of the chunk as seen in its header.
     * @return a non-null {@link Chunk}.
     * @throws ChunkException if chunk name not supported.
     */
    public static Chunk getChunk(String chunkHeader) throws ChunkException{
        return CHUNK_MAP.get(ChunkType.getChunkFor(chunkHeader));
    }
    
    public void parseChunk(ZTRChromatogramBuilder builder, InputStream inputStream) throws TraceDecoderException{
        if(inputStream ==null){
            throw new TraceDecoderException("inputStream can not be null");
        }
        if(builder ==null){
            throw new TraceDecoderException("chromoStruct can not be null");
        }
        readMetaData(inputStream);
        readData(builder,inputStream);
    }
    public String parseChunk(InputStream inputStream, ChromatogramFileVisitor visitor,String basecalls) throws TraceDecoderException{
        if(inputStream ==null){
            throw new TraceDecoderException("inputStream can not be null");
        }
        readMetaData(inputStream);
        return readData(inputStream,visitor,basecalls);
    }
    /**
     * Read the meta data portion of the chunk
     * Some implementations of chunk may not have
     * metaData.
     *
     */
    protected void readMetaData(InputStream inputStream)throws TraceDecoderException{

       try{
        long length = readLength(inputStream);

        //skip over meta data here
        IOUtil.blockingSkip(inputStream, length);
       }
        catch(IOException ioEx){
            throw new TraceDecoderException("error reading chunk meta data",ioEx);
        }
    }

    /**
     * @return
     * @throws IOException
     * @throws TraceDecoderException
     * @throws NumberFormatException
     */
    protected int readLength(InputStream inputStream) throws TraceDecoderException{
       try{

        //get metaDataLength
        byte[] lengthArray = readLengthFromInputStream(inputStream);
        long length = ZTRUtil.readInt(lengthArray);
        if(length <0 || length > Integer.MAX_VALUE){
            //uhh... try other endian
            length = ZTRUtil.readInt(IOUtil.switchEndian(lengthArray));
        }

        return  (int)length;
       }
       catch(Exception e){
           throw new TraceDecoderException("error reading chunk length", e);
       }

    }

    private byte[] readLengthFromInputStream(InputStream inputStream)
            throws IOException, TraceDecoderException {
        byte[] lengthArray = new byte[4];
        ReadResults results = IOUtil.safeBlockingRead(inputStream, lengthArray);
        if(results.getNumberOfBytesRead()<4){
            String message ="invalid metaData length record only has " +results.getNumberOfBytesRead() + " bytes";
            throw new TraceDecoderException(message);
        }
        return lengthArray;
    }


    /**
     * Read the data portion of the chunk.
     * This method calls parseData to interpret
     * the data and returns the result.
     */
    private void readData(ZTRChromatogramBuilder builder,InputStream inputStream)throws TraceDecoderException{
        int length = readLength(inputStream);

        //the data may be encoded
        //call dataFactory to get the data implementation

        parseData(decodeChunk(inputStream,length), builder);
    }
    /**
     * Read the data portion of the chunk.
     * This method calls parseData to interpret
     * the data and returns the result.
     */
    private String readData(InputStream inputStream,ChromatogramFileVisitor visitor, String basecalls)throws TraceDecoderException{
        int length = readLength(inputStream);

        //the data may be encoded
        //call dataFactory to get the data implementation

        return parseData(decodeChunk(inputStream,length), visitor, basecalls);
    }
    /**
     * Performs the actual conversion from the data stored in the chunk
     * into the appropriate format and SETS the data to the given
     * {@link ZTRChromatogramBuilder} object.
     * @param unEncodedData the actual bytes to parse
     * (including any formating bytes).
     * @param builder the {@link ZTRChromatogramBuilder} to set the data to.
     * @throws TraceDecoderException if there are any problems parsing the data.
     */
    protected abstract void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder) throws TraceDecoderException;
    protected abstract String parseData(byte[] unEncodedData, ChromatogramFileVisitor visitor, String basecalls) throws TraceDecoderException;

    public abstract byte[] encodeChunk(Chromatogram ztrChromatogram) throws TraceEncoderException;

    protected byte[] decodeChunk(InputStream inputStream, int datalength) throws TraceDecoderException{
        try{
           
           //first we have to read the data encoding
//          data can be encoded, get data type.
            // the data can be encoded multiple times
            //ex: runlength encoded, then zipped
            //we will know when we are done, when the first byte is 0.
            boolean stillEncoded = true;
            byte[] data = readData(inputStream,datalength);
            while(stillEncoded){
                final Data dataImplementation = DataFactory.getDataImplementation(data);
                if(dataImplementation instanceof RawData){
                    stillEncoded = false;
                }            
                else{
                    //not done yet
                    data = dataImplementation.parseData(data);
                }
            }
            return data;
        }
        catch(IOException e){
            throw new TraceDecoderException("error decoding chunk",e);
        }
    }

    private byte[] readData(InputStream inputStream, int datalength) throws IOException,
            TraceDecoderException {
        byte[] data = new byte[datalength];
        ReadResults results = IOUtil.safeBlockingRead(inputStream, data);
        if(results.getNumberOfBytesRead()< datalength){
            String message = "invalid datalength field, length specified was " + datalength + " only " + results.getNumberOfBytesRead();
            throw new TraceDecoderException(message);
        }      
        return data;
    }
}
