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

package org.jcvi.assembly.trim;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.DefaultQualityFastaFileDataStore;
import org.jcvi.fasta.QualityFastaRecordDataStoreAdapter;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestLucyQualityTrimmer {
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestDefaultPrimerTrimmer_ActualData.class);
    private QualityDataStore qualities;
    
    LucyQualityTrimmer sut = new LucyQualityTrimmer.Builder(30)
                            .addTrimWindow(30, 0.1F)
                            .addTrimWindow(10, 0.35F)
                            .build();
    @Before
    public void setup() throws  IOException{
        qualities = QualityFastaRecordDataStoreAdapter.adapt(
                new DefaultQualityFastaFileDataStore(RESOURCES.getFile("files/fullLength.qual")));
    }
    
    @Test
    public void SAJJA07T27G07MP1F() throws DataStoreException{
        final EncodedGlyphs<PhredQuality> fullQualities = qualities.get("SAJJA07T27G07MP1F");
        Range actualTrimRange = sut.trim(fullQualities);
        Range expectedRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 12,679);
        assertEquals(expectedRange, actualTrimRange);
    }
    @Test
    public void SAJJA07T27G07MP675R() throws DataStoreException{
        final EncodedGlyphs<PhredQuality> fullQualities = qualities.get("SAJJA07T27G07MP675R");
        System.out.println(fullQualities.getLength());
        Range actualTrimRange = sut.trim(fullQualities);
        Range expectedRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 16,680);
        assertEquals(expectedRange, actualTrimRange);
    }
}
