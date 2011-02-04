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

package org.jcvi.assembly.slice;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.GapQualityValueStrategies;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.QualityGlyphCodec;
import org.jcvi.glyph.phredQuality.datastore.QualityDataStoreAdapter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSliceMap {

    protected abstract SliceMap createSliceMapFor(Contig<PlacedRead> contig, QualityDataStore qualityDatastore, QualityValueStrategy qualityValueStrategy);
    private QualityDataStore qualityDataStore;
    private static final QualityGlyphCodec CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
    @Before
    public void setup(){
        Map<String, QualityEncodedGlyphs> qualities = new HashMap<String, QualityEncodedGlyphs>();
        qualities.put("read_0", new DefaultQualityEncodedGlyphs(CODEC, 
                        PhredQuality.valueOf(new byte[]{10,12,14,16,18,20,22,24})));
        qualities.put("read_1", new DefaultQualityEncodedGlyphs(CODEC, 
                PhredQuality.valueOf(new byte[]{1,2,3,4,5,6,7,8})));
        qualities.put("read_2", new DefaultQualityEncodedGlyphs(CODEC, 
                PhredQuality.valueOf(new byte[]{15,16,17,18})));
        qualityDataStore = new QualityDataStoreAdapter(
                            new SimpleDataStore<QualityEncodedGlyphs>(qualities));
    }
    @Test
    public void allSlicesSameDepth(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "ACGTACGT")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AA", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 14,3),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 16,4),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AA", 18,5),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 20,6),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,8),
                sut.getSlice(7));
    }
    @Test
    public void multipleDepthSlices(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "ACGTACGT")
                                    .addRead("read_2", 2,   "GTAC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AA", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TTT", 16,4,16),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AAA", 18,5,17),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CCC", 20,6,18),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,8),
                sut.getSlice(7));
    }
    
    @Test
    public void multipleBasecallsPerSlice(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "RCGTACGT")
                                    .addRead("read_2", 2,   "GWAC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AR", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TTW", 16,4,16),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AAA", 18,5,17),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CCC", 20,6,18),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,8),
                sut.getSlice(7));
    }
    
    @Test
    public void gapsInSliceShouldUseLowestFlankingQualityValues(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "RCGTA-GT")
                                    .addRead("read_2", 2,   "G-AC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AR", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT-", 16,4,15),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AAA", 18,5,16),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C-C", 20,5,17),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,6),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,7),
                sut.getSlice(7));
    }
    
}
