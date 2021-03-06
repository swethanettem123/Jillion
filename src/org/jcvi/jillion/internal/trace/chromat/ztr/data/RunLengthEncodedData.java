/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 30, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.internal.core.util.RunLength;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;



/**
 * <code>RunLengthEncodedData</code> replaces 
 * a series of N identical bytes of value V with the guard byte G followed
 * by N and V. Non-series are stored as normal unless it the value
 * happens to be the guard byte, which is stored G 0.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public enum RunLengthEncodedData implements Data {
	
	
    /**
     * Singleton instance of ZTR RunLengthEncodedData.
     */
    INSTANCE;
    private static final byte GUARD_ESCAPE = 0;
	private static final int MIN_RUN_LENGTH = 4;
	private static final int MAX_RUN_LENGTH = 255;
	/**
     * IO_Lib uses 150 as the guard byte 
     * when encoding Run Length chunks
     * so that's what we will use as default.
     */
    public static final byte DEFAULT_GUARD = (byte)150;
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data) throws IOException {
       //read uncompressed length
        int uncompressedLength = computeUncompressedLength(data);
        ByteBuffer in = ByteBuffer.wrap(data);
        in.position(5);
       //read guard
        byte guard = in.get();
        ByteBuffer out = ByteBuffer.allocate(uncompressedLength);
        parse(in, guard, out);
        return out.array();
    }

    private void parse(ByteBuffer in, byte guard, ByteBuffer out) {
        List<RunLength<Byte>> runLengthList = parseIntoRunLength(in, guard);        
        write(runLengthList,out);
    }

    private List<RunLength<Byte>> parseIntoRunLength(ByteBuffer in, byte guard) {
        List<RunLength<Byte>> runLengthList = new ArrayList<RunLength<Byte>>();
        while(in.hasRemaining()){
            byte value = in.get();
            if( value == guard){          
                int count = getCount(in);
                if(count ==0){
                	//count is 0 so guard byte must be actual value.
                    runLengthList.add(new RunLength<Byte>(Byte.valueOf(guard),1));
                }
                else{
                    byte repValue = in.get();                   
                    runLengthList.add(new RunLength<Byte>(Byte.valueOf(repValue),count));
                }
            }else{
            	//not guard, just output token
                runLengthList.add(new RunLength<Byte>(Byte.valueOf(value),1));
            }
        }
        return runLengthList;
    }

    private void write(List<RunLength<Byte>> runLengthList, ByteBuffer out) {
        for(RunLength<Byte> runLength : runLengthList){
            putConsecutiveValues(out, runLength.getLength(), runLength.getValue().byteValue());
        }
        
    }

    private void putConsecutiveValues(ByteBuffer out, int count, byte repValue) {
        byte[] consecutiveValues = new byte[count];
        Arrays.fill(consecutiveValues, repValue);
        out.put(consecutiveValues);
    }

    private int computeUncompressedLength(byte[] data) {
        byte uncompressedLengthArray[] = new byte[4];
        /* dkatzel :
         * uncompressed length is wrong endian in IO_Lib source code
         * compared to the 1.2 ZTR spec but I am going to use IO_Lib version 
         * because it seems to work.
         */
        for(int i=1; i< 5; i++){
            uncompressedLengthArray[4-i] = data[i];
        }        
        return (int)ZTRUtil.readInt(uncompressedLengthArray);
    }

    private int getCount(ByteBuffer in) {
        return IOUtil.toUnsignedByte(in.get());
    }
    /**
     * Same as {@link #encodeData(byte[], byte) encodeData(data, DEFAULT_GUARD)}
     */
	@Override
	public byte[] encodeData(byte[] data) throws IOException {
		return encodeData(data, DEFAULT_GUARD);
	}
	/**
	 * Encodes the given data as run length encoded data.
	 * @param data the data to encode.
	 * @param guard the guard byte used to specify run length blocks.
	 * @return a byte array containing the given input data but as
	 * run length encoded.
	 * @throws IOException if there is a problem
	 * encoding the data.
	 */
	@Override
	public byte[] encodeData(byte[] data, byte guard) throws IOException {
		ByteBuffer encodedBuffer = ByteBuffer.allocate(2*data.length+6);
		encodedBuffer.put(DataHeader.RUN_LENGTH_ENCODED);
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(data.length);
        encodedBuffer.put(IOUtil.switchEndian(lengthBuffer.array()));
        encodedBuffer.put(guard);      
        runLengthEncode(data, guard,encodedBuffer);
		encodedBuffer.flip();
        return Arrays.copyOfRange(encodedBuffer.array(), 0, encodedBuffer.limit());
	}
	
	private void runLengthEncode(byte[] dataToEncode, byte guard, ByteBuffer out){
		int i=0;
		while(i<dataToEncode.length){
			int runLength = getNextRunLength(dataToEncode, i);
			if (runLengthIsTooSmall(runLength)) {
				encodeUnrunLenghEncodedBlock(dataToEncode, guard, out, i, runLength); 
			}else{			
				encodeRunLengthEncodedBlock(dataToEncode, guard, out, i, runLength); 
			}
			i+=runLength;
		}
	}

	private void encodeRunLengthEncodedBlock(byte[] dataToEncode, byte guard,
			ByteBuffer out, int i, int runLength) {
		out.put( guard);
		out.put((byte)runLength);
		out.put( dataToEncode[i]);
	}

	private void encodeUnrunLenghEncodedBlock(byte[] dataToEncode, byte guard,
			ByteBuffer out, int i, int runLength) {
		for (int j =0; j<runLength; j++) {
			//if  our next byte is the same as 
			//guard, escape it
			byte nextByte = dataToEncode[i+j];
			if (nextByte == guard) {
				out.put(guard);
				out.put(GUARD_ESCAPE);
			} else {
				out.put(nextByte);
			}
		}
	}

	private boolean runLengthIsTooSmall(int runLength) {
		return runLength< MIN_RUN_LENGTH;
	}

	private int getNextRunLength(byte[] dataToEncode, int currentOffset) {
		int k = currentOffset;
		while(k < dataToEncode.length && dataToEncode[currentOffset] == dataToEncode[k] && k-currentOffset != MAX_RUN_LENGTH){
		    k++;
		}
		return k-currentOffset;
	}
}
