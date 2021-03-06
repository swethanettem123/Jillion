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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData;
import org.junit.Test;
public class TestDelta16Data {
    private static byte[] uncompressedArray = new byte[]{16,32,49,16};
    
    private static final byte[] compressedLevel1;
    private static final byte[] compressedLevel2;
    private static final byte[] compressedLevel3;
    DeltaEncodedData sut = DeltaEncodedData.SHORT;
    
   
    static{
        compressedLevel1 = createCompressedDeltaLevel1();
        compressedLevel2 = createCompressedDeltaLevel2();
        compressedLevel3= createCompressedDeltaLevel3();
       
    }
	private static byte[] createCompressedDeltaLevel1() {
		ShortBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asShortBuffer();
         
    	ByteBuffer compressedLevel1 = ByteBuffer.allocate(6);
        compressedLevel1.put((byte)65);
        compressedLevel1.put((byte)1);  //level
        int delta=0;
        int prevValue=0;
        while(uncompressed.hasRemaining()){
            delta = prevValue;
            prevValue = uncompressed.get();
            compressedLevel1.putShort((short)(prevValue -delta));
        }        
        compressedLevel1.flip();
        byte[] temp = compressedLevel1.array();
		return temp;
	}
	
	private static byte[] createCompressedDeltaLevel2() {
		 ShortBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asShortBuffer();
	        ByteBuffer compressed = ByteBuffer.allocate(6);
	        compressed.put((byte)65);
	        compressed.put((byte)2);  //level
	        int delta=0;
	        int prevValue=0;
	        int prevPrevValue=0;
	        while(uncompressed.hasRemaining()){
	            delta = 2*prevValue -prevPrevValue;
	            prevPrevValue= prevValue;
	            prevValue = uncompressed.get();
	            compressed.putShort((short)(prevValue -delta));
	        }        
	        compressed.flip();
	       
        return compressed.array();
	}
	
	private static byte[] createCompressedDeltaLevel3() {
		 ShortBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asShortBuffer();
	        ByteBuffer compressed = ByteBuffer.allocate(6);
	        compressed.put((byte)65);
	        compressed.put((byte)3);  //level
	        int delta=0;
	        int prevValue=0;
	        int prevPrevValue=0;
	        int prevPrevPrevValue =0;
	        while(uncompressed.hasRemaining()){
	            delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
	            prevPrevPrevValue= prevPrevValue;
	            prevPrevValue= prevValue;
	            prevValue = uncompressed.get();
	            compressed.putShort((short)(prevValue -delta));
	        }        
	        compressed.flip();
	        return compressed.array();
	}
    @Test
    public void level1(){       
        byte[] actual = sut.parseData(compressedLevel1);
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void compressLevel1() throws IOException{
    	byte[] actual = sut.encodeData(uncompressedArray, DeltaEncodedData.Level.DELTA_LEVEL_1);
    	assertArrayEquals(compressedLevel1, actual);
    }
    
    @Test
    public void level2(){
        byte[] actual = sut.parseData(compressedLevel2);
        assertTrue(Arrays.equals(actual, uncompressedArray));        
    }
    @Test
    public void compressLevel2() throws IOException{
    	byte[] actual = sut.encodeData(uncompressedArray, DeltaEncodedData.Level.DELTA_LEVEL_2);
    	assertArrayEquals(compressedLevel2, actual);
    }
    @Test
    public void level3(){
      byte[] actual = sut.parseData(compressedLevel3);
      assertArrayEquals(actual, uncompressedArray);        
    }
    @Test
    public void compressLevel3() throws IOException{
    	byte[] actual = sut.encodeData(uncompressedArray, DeltaEncodedData.Level.DELTA_LEVEL_3);
    	assertArrayEquals(compressedLevel3, actual);
    }

}
