/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sff;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * 454 includes an optional index at the 
 * end of their {@literal .sff} files that contains
 * and encoded file offset for each record
 * in the file.  The actual format is not 
 * specified in the 454 manual or file specification
 * so it had to be reverse engineered.
 * <p/>
 * This class supports two index encodings.
 * <ol>
 * <li>An XML manifest and sorted index that are generated
 * by 454 programs by default.</li>
 * <li>Just a sorted index without the XML manifest 
 * that can be generated by sfffile using the "-nmft" option.</li>
 * </ol>
 * This class does not work on ion torrent sff files
 * since as of Spring 2012, they don't include an index.
 * @author dkatzel
 *
 */
final class Indexed454SffFileDataStore implements FlowgramDataStore{
	
	private final File sffFile;
	private final SffCommonHeader commonHeader;
	/**
	 * It appears that 454 will
	 * only make an index if the file size <4GB
	 * so we can use unsigned ints to save memory
	 * in the index.
	 */
	private final Map<String, Integer> map;
	private boolean isClosed=false;
	private final DataStoreFilter filter;
	/**
	 * Try to create a {@link FlowgramDataStore} by only parsing
	 * the 454 index at the end of the sff file.
	 * If there is no index or it is encoded
	 * in an unknown format, then this method will
	 * return null.
	 * @param sffFile
	 * @return an {@link FlowgramDataStore} if successfully
	 * parsed; or {@code null} if the index can't
	 * be parsed.
	 * @throws IOException if there is a problem reading the file.
	 */
	public static FlowgramDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		ManifestCreatorVisitor visitor = new ManifestCreatorVisitor(sffFile, filter);
		SffFileParser.parse(sffFile, visitor);
		//there is a valid sff formatted manifest inside the sff file
		if(visitor.isUseableManifest()){
			return new Indexed454SffFileDataStore(visitor);
		}
		//no manifest delegate to iterating thru
		return null;
		
	}
	
	
	private Indexed454SffFileDataStore(ManifestCreatorVisitor visitor){
		this.map = visitor.map;
		this.sffFile = visitor.sffFile;
		this.commonHeader = visitor.commonHeader;
		this.filter = visitor.filter;
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		throwErrorIfClosed();
		try {
			//use large sffFileDataStore 
			//to parse ids in order in file
			return LargeSffFileDataStore.create(sffFile,filter).idIterator();
		} catch (IOException e) {
			throw new IllegalStateException("sff file has been deleted",e);
		}
	}

	@Override
	public Flowgram get(String id) throws DataStoreException {
		throwErrorIfClosed();
		Long offset = getOffsetFor(id);
		if(offset ==null){
			return null;
		}
		
		SffFileVisitorDataStoreBuilder builder = DefaultSffFileDataStore.createVisitorBuilder();
		builder.visitFile();
		InputStream in=null;
		try {
			in = new FileInputStream(sffFile);
			IOUtil.blockingSkip(in, offset);
			DataInputStream dataIn = new DataInputStream(in);
			 SffReadHeader readHeader = DefaultSffReadHeaderDecoder.INSTANCE.decodeReadHeader(dataIn);
			 final int numberOfBases = readHeader.getNumberOfBases();
             SffReadData readData = DefaultSffReadDataDecoder.INSTANCE.decode(dataIn,
                             commonHeader.getNumberOfFlowsPerRead(),
                             numberOfBases);
             builder.visitReadHeader(readHeader);
             builder.visitReadData(readData);
             return builder.build().get(id);
		} catch (IOException e) {
			throw new DataStoreException("error trying to get flowgram "+ id,e);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	/**
	 * Get the offset into the sff file from the manifest.
	 * @param id the read id to get.
	 * @return Returns offset into sff file which contains the start
	 * of the given record or null if there is no read
	 * with that name in the manifest.
	 */
	private Long getOffsetFor(String id){
		Integer offset= map.get(id);
		if(offset ==null){
			return null;
		}
		return IOUtil.toUnsignedInt(offset.intValue());
	}
	@Override
	public boolean contains(String id) throws DataStoreException {
		throwErrorIfClosed();
		return map.containsKey(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		throwErrorIfClosed();
		return map.size();
	}

	@Override
	public synchronized boolean isClosed(){
		return isClosed;
	}

	@Override
	public StreamingIterator<Flowgram> iterator() throws DataStoreException {
		throwErrorIfClosed();
		try {
			return LargeSffFileDataStore.create(sffFile,filter).iterator();
		} catch (IOException e) {
			throw new DataStoreException("sff file has been deleted",e);
		}
	}

	@Override
	public synchronized  void close() throws IOException {
		isClosed =true;
		map.clear();
		
	}
	
	private synchronized void throwErrorIfClosed(){
		if(isClosed){
			throw new IllegalStateException("closed");
		}
	}

	private static final class ManifestCreatorVisitor implements SffFileVisitor{
		/**
		 * 255 ^ 3 = {@value}.
		 */
		private static final int POW_3 = 16581375;
		/**
		 * 255 ^ 2 = {@value}.
		 */
		private static final int POW_2 = 65025;
		/**
		 * 255 ^ 1 = {@value}.
		 */
		private static final int POW_1 = 255;
		
		private final File sffFile;
		private SffCommonHeader commonHeader;
		private Map<String, Integer> map;
		private boolean useableManifest=false;
		
		private final DataStoreFilter filter;
		
		private ManifestCreatorVisitor(File sffFile, DataStoreFilter filter) {
			this.sffFile = sffFile;
			this.filter = filter;
			
		}
		@Override
		public void visitFile() {
			//no-op
		}

		@Override
		public void visitEndOfFile() {
			//no-op
		}

		public boolean isUseableManifest() {
			return useableManifest;
		}
		

		@Override
		public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
			this.commonHeader = commonHeader;
			BigInteger offsetToIndex =commonHeader.getIndexOffset();
			if(offsetToIndex.longValue() !=0L){
				tryToParseManifest(commonHeader, offsetToIndex);
			}
			//stop parsing file
			return CommonHeaderReturnCode.STOP_PARSING;
		}

		private void tryToParseManifest(SffCommonHeader commonHeader,
				BigInteger offsetToIndex) {
			int indexLength =(int)commonHeader.getIndexLength();
			InputStream in=null;
			try {
				in = IOUtil.createInputStreamFromFile(sffFile, offsetToIndex.intValue(),indexLength);
				
			    //pseudocode:
				//skip xml manifest if present
				//read bytes until byte value is 0
				//anything before the 0 is the read name
				//next 4 bytes? is offset into file using base 255
				//offset also appears to be in little endian
				//another null byte separator before next entry

				byte[] magicNumber =new byte[4];
				IOUtil.blockingRead(in, magicNumber);
				if(Arrays.equals(magicNumber, ".mft".getBytes(IOUtil.UTF_8))){
					//includes xml plus sorted index
					byte[] version = new byte[4];
					IOUtil.blockingRead(in, version);
					String versionString = new String(version, IOUtil.UTF_8);
					if(!"1.00".equals(versionString)){
						throw new IOException("unsupported xml manifest version : " + versionString);
					}
					long xmlLength =IOUtil.readUnsignedInt(in);
					//skip 4 bytes for the datalength
					IOUtil.blockingSkip(in, 4 + xmlLength);
					//System.out.println("sorted manifest\n" + IOUtil.toString(in, IOUtil.UTF_8_NAME).replaceAll("\0", "\\0") + "\n<end of sorted>");
					populateOffsetMap(in); 
					useableManifest=true;
				}
				//this kind of index is created
				//by sfffile if you say "no manifest"
				if(Arrays.equals(magicNumber, ".srt".getBytes(IOUtil.UTF_8))){
					//includes xml plus sorted index
					byte[] version = new byte[4];
					IOUtil.blockingRead(in, version);
					String versionString = new String(version, IOUtil.UTF_8);
					if(!"1.00".equals(versionString)){
						throw new IOException("unsupported sorted manifest version : " + versionString);
					}
					
					
					//System.out.println("sorted manifest\n" + IOUtil.toString(in, IOUtil.UTF_8_NAME).replaceAll("\0", "\\0") + "\n<end of sorted>");
					populateOffsetMap(in); 
					useableManifest=true;
				}
			
			} catch (IOException e) {
				throw new RuntimeException("error parsing manifest", e);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}

		private long convertFromBase255(byte[] values){
			return IOUtil.toUnsignedByte(values[0]) 
					+ POW_1	* IOUtil.toUnsignedByte(values[1]) 
					+ POW_2	* IOUtil.toUnsignedByte(values[2])
					+ POW_3	* IOUtil.toUnsignedByte(values[3]);
		}
		private void populateOffsetMap(InputStream in) throws IOException {
			int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(commonHeader.getNumberOfReads());
			map = new HashMap<String, Integer>(mapSize);
			for(long i =0; i< commonHeader.getNumberOfReads(); i++){
				String id = parseNextId(in);
				if(id ==null){
					throw new IOException(
							String.format("incomplete index in sff file; missing %d reads",commonHeader.getNumberOfReads()-i));
				}
				byte[] index = new byte[4];
				IOUtil.blockingRead(in, index);
				//only include id in index if we care about it.
				if(filter.accept(id)){
					index =IOUtil.switchEndian(index);
					long offset =convertFromBase255(index);
					//signed int to save space
					map.put(id,IOUtil.toSignedInt(offset));
				}
				//skip the read data since we only care about names
				//at the moment.  The read data will be
				//reparsed during a call to DataStore#get()
				in.read();
			}
		}

		private String parseNextId(InputStream in) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte nextByte =(byte)in.read();
			if(nextByte==-1){
				return null;
			}
			do{
				out.write(nextByte);
				nextByte =(byte)in.read();
			}while(nextByte !=0);
			return new String(out.toByteArray(), IOUtil.UTF_8);
		}

		@Override
		public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
	
			return ReadHeaderReturnCode.STOP_PARSING;
		}

		@Override
		public ReadDataReturnCode visitReadData(SffReadData readData) {
			return ReadDataReturnCode.STOP_PARSING;
		}
		
	}
}
