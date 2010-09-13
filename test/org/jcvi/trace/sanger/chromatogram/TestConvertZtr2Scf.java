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
 * Created on Jan 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestConvertZtr2Scf {
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestConvertZtr2Scf.class);
    
    ZTRChromatogramParser ztrParser = new ZTRChromatogramParser();
    SCFCodec scfCodec = new Version3SCFCodec();
    
    @Test
    public void ztr2scf() throws TraceDecoderException, IOException{
        
        Chromatogram decodedZTR = ztrParser.decode(
                RESOURCES.getFileAsStream("ztr/files/GBKAK82TF.ztr"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.encode(new SCFChromatogramImpl(decodedZTR), out);
        
        Chromatogram encodedScf = scfCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        assertEquals(decodedZTR, encodedScf);
    }
    
    @Test
    public void scfequalsZtr() throws TraceDecoderException, IOException{
        Chromatogram decodedScf = scfCodec.decode(new DataInputStream(
        		RESOURCES.getFileAsStream("scf/files/GBKAK82TF.scf")));
        Chromatogram decodedZTR = ztrParser.decode(
                RESOURCES.getFileAsStream("ztr/files/GBKAK82TF.ztr"));
        assertEquals(decodedZTR, decodedScf);        
    }
    /**
     * ZTR files can have no qualities (ex: trash data)
     * but SCF requires the same # of qualities as basecalls
     * so just set them to 0.
     * @throws IOException 
     * @throws TraceDecoderException 
     */
    @Test
    public void ztrWithNoQualitiesShouldGetPaddedQualitiesInScf() throws TraceDecoderException, IOException{
        Chromatogram ztr = ztrParser.decode(RESOURCES.getFileAsStream("ztr/files/515866_G07_AFIXF40TS_026.ab1.afg.trash.ztr"));
        System.out.println(ztr.getProperties());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.encode(new SCFChromatogramImpl(ztr), out);
        
        Chromatogram encodedScf = scfCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        
        int numberOfBases = (int)encodedScf.getBasecalls().getLength();
        List<PhredQuality> expectedQualities = new ArrayList<PhredQuality>(numberOfBases);
        for(int i=0; i< numberOfBases; i++){
            expectedQualities.add(PhredQuality.valueOf(0));
        }
        assertEquals(expectedQualities,encodedScf.getQualities().decode());
    }
    
    
}
