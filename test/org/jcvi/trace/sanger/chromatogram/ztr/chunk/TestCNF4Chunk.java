/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.CNF4Chunk;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCNF4Chunk {

    String bases = "ACGT-";
    byte[] aconf = new byte[]{40,0,0,0,0};
    byte[] cconf = new byte[]{0,30,0,0,0};
    byte[] gconf = new byte[]{0,0,35,0,0};
    //- confidence is put in T
    byte[] tconf = new byte[]{0,0,0,38,37};
    CNF4Chunk sut = new CNF4Chunk();
    @Test
    public void valid() throws TraceDecoderException{
        ZTRChromatogramBuilder struct = new ZTRChromatogramBuilder();
        struct.basecalls(bases);
        
        
        ByteBuffer buf = ByteBuffer.allocate(bases.length()*4 +1);
        buf.put((byte)0);//padding
        //called bases
        buf.put(aconf[0]);
        buf.put(cconf[1]);
        buf.put(gconf[2]);
        buf.put(tconf[3]);
        buf.put(tconf[4]);
        //uncalled confidences
        buf.put(cconf[0]);
        buf.put(gconf[0]);
        buf.put(tconf[0]);
        
        buf.put(aconf[1]);
        buf.put(gconf[1]);
        buf.put(tconf[1]);
        
        buf.put(aconf[2]);
        buf.put(cconf[2]);
        buf.put(tconf[2]);
        
        buf.put(aconf[3]);
        buf.put(cconf[3]);
        buf.put(gconf[3]);
        
        buf.put(aconf[4]);
        buf.put(cconf[4]);
        buf.put(gconf[4]);
        
        sut.parseData(buf.array(), struct);
        
        assertTrue(Arrays.equals(aconf, struct.aConfidence()));
        assertTrue(Arrays.equals(cconf, struct.cConfidence()));
        assertTrue(Arrays.equals(gconf, struct.gConfidence()));
        assertTrue(Arrays.equals(tconf, struct.tConfidence()));
    }
    
}
