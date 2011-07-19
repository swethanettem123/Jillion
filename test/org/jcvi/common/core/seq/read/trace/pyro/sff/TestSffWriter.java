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

package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFCommonHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFReadData;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFReadHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFCommonHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFReadData;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFReadHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffWriter;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSffWriter {
    int numberOfBases=123;
    Range qualityClip = Range.buildRange(10, 20);
    Range adapterClip = Range.buildRange(4, 122);
    String name = "readName";
    @Test
    public void writeCommonHeaderWithNoIndex() throws IOException{
        String keySequence = "TCAG";
        short flowLength= (short)800;
        short paddedHeaderLength = 840;
        SFFCommonHeader header = new DefaultSFFCommonHeader.Builder()
                                    .withNoIndex()
                                    .keySequence(keySequence)
                                    .numberOfReads(1234)
                                    .numberOfFlowsPerRead(flowLength)
                                    .build();
        
        StringBuilder flows = new StringBuilder();
        for(int i=0; i< flowLength; i+=4){
            flows.append(keySequence);
        }
        byte[] expected = ByteBuffer.allocate(paddedHeaderLength)
                                        .put(SFFUtil.SFF_MAGIC_NUMBER)
                                        .putLong(0L)
                                        .putInt(0)
                                        .putInt((int)header.getNumberOfReads())
                                        .putShort(paddedHeaderLength)
                                        .putShort((short)4)
                                        .putShort(flowLength)
                                        .put(SFFUtil.FORMAT_CODE)
                                        .put(flows.toString().getBytes())
                                        .put(keySequence.getBytes())
                                        .put(new byte[3])
                                        .array();
        ByteArrayOutputStream actual = new ByteArrayOutputStream(paddedHeaderLength);
        SffWriter.writeCommonHeader(header, actual);
        assertArrayEquals(expected, actual.toByteArray());
    }
    
    private byte[] encodeReadHeader(SFFReadHeader readHeader){
        //I wrap a newly allocated byte array
        //so that it is automatically filled with zeros
        //this allows me to not worry about padding.
        final int nameLength = readHeader.getName().length();
        int padding = SFFUtil.caclulatePaddedBytes(16+nameLength);
        final int headerLength = padding+16+nameLength;
        ByteBuffer buf = ByteBuffer.wrap(new byte[headerLength]);
        buf.putShort((short)headerLength);
        
        buf.putShort((short)nameLength);
        buf.putInt(readHeader.getNumberOfBases());
        final Range qClip = readHeader.getQualityClip();
        if(qClip ==null){
            buf.put(SFFUtil.EMPTY_CLIP_BYTES);
        }
        else{
            Range residueConverted = qClip.convertRange(CoordinateSystem.RESIDUE_BASED);
            buf.putShort((short)residueConverted.getLocalStart());
            buf.putShort((short)residueConverted.getLocalEnd());
        }
        final Range aClip = readHeader.getAdapterClip();
        if(aClip==null){
            buf.put(SFFUtil.EMPTY_CLIP_BYTES);
        }
        else{
            Range residueConverted = aClip.convertRange(CoordinateSystem.RESIDUE_BASED);
            buf.putShort((short)residueConverted.getLocalStart());
            buf.putShort((short)residueConverted.getLocalEnd());
        }
        buf.put(readHeader.getName().getBytes());
        return buf.array();
    }

    @Test
    public void valid() throws IOException{
        
        SFFReadHeader readHeader =new DefaultSFFReadHeader(numberOfBases,
                qualityClip, adapterClip, name);
        byte[] expectedEncodedBytes = encodeReadHeader(readHeader);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadHeader(readHeader, actual);
        assertArrayEquals(expectedEncodedBytes, actual.toByteArray());
    }
    @Test
    public void nullAdapterClipShouldEncodeWithZeros() throws IOException{
        DefaultSFFReadHeader nullAdpaterClip = new DefaultSFFReadHeader(numberOfBases,
                qualityClip, null, name);
        byte[] expectedEncodedBytes = encodeReadHeader(nullAdpaterClip);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadHeader(nullAdpaterClip, actual);
        assertArrayEquals(expectedEncodedBytes, actual.toByteArray());

    }
    @Test
    public void nullQualityClip() throws IOException{
        DefaultSFFReadHeader nullQualityClip = new DefaultSFFReadHeader(numberOfBases,
                null, adapterClip, name);
        byte[] expectedEncodedBytes = encodeReadHeader(nullQualityClip);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadHeader(nullQualityClip, actual);
        assertArrayEquals(expectedEncodedBytes, actual.toByteArray());

    }
    
    @Test
    public void encodeReadData() throws IOException{
        byte[] qualities = new byte[]{20,30,40,35};
        short[] values = new short[]{100,8,97,4,200};
        byte[] indexes = new byte[]{1,2,2,0};
        String bases = "TATT";

        DefaultSFFReadData readData = new DefaultSFFReadData(bases, indexes,  values,
                                                qualities);
        byte[] expected = encodeExpectedReadData(readData);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadData(readData, actual);
        assertArrayEquals(expected, actual.toByteArray());
        
    }
    private byte[] encodeExpectedReadData(SFFReadData readData){
        int basesLength =readData.getBasecalls().length();
        int numberOfFlows = readData.getFlowgramValues().length;
        int readDataLength = numberOfFlows * 2 + 3*basesLength;
        int padding =SFFUtil.caclulatePaddedBytes(readDataLength);
        ByteBuffer buf = ByteBuffer.wrap(new byte[readDataLength+padding]);
        IOUtil.putShortArray(buf, readData.getFlowgramValues());
        buf.put(readData.getFlowIndexPerBase());
        buf.put(readData.getBasecalls().getBytes());
        buf.put(readData.getQualities());
        return buf.array();
    }
}
