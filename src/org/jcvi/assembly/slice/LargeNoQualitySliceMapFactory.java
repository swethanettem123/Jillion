/*
 * Created on Feb 22, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class LargeNoQualitySliceMapFactory implements SliceMapFactory{

    private final int cacheSize;
    private final PhredQuality phredQuality;
    public LargeNoQualitySliceMapFactory(PhredQuality phredQuality,int cacheSize) {
        this.phredQuality = phredQuality;
        this.cacheSize = cacheSize;
    }
    public LargeNoQualitySliceMapFactory(){
        this(LargeNoQualitySliceMap.DEFAULT_PHRED_QUALITY, LargeNoQualitySliceMap.DEFAULT_CACHE_SIZE);
    }

    @Override
    public SliceMap createNewSliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore) {
        return new LargeNoQualitySliceMap(coverageMap, cacheSize,this.phredQuality);
    }
    

}
