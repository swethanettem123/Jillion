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
 * Created on Dec 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.conflict;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.LowestFlankingQualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.DefaultSliceMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.assembly.slice.conflict.SliceConflictDetector.SliceConflicts;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.fasta.DefaultQualityFastaFileDataStore;
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.QualityDataStoreAdapter;

public class SliceConflictMap implements Iterable<ConflictedRegion>{

    private final Map<Range, ConflictedRegion> map= new LinkedHashMap<Range, ConflictedRegion>();
    
    public SliceConflictMap(SliceMap sliceMap , NucleotideEncodedGlyphs consensus,SliceConflictDetector conflictDetector)   {
        int start=0;
        Conflict currentConflict=null;
       for(int i=0; i< consensus.getLength(); i++){
           Slice slice = sliceMap.getSlice(i);
            Conflict conflict =conflictDetector.analyize(slice, consensus.get(i));
            if(currentConflict ==null){
                currentConflict = conflict;
            }
            else{
                if(!conflict.equals(currentConflict)){
                    Range range = Range.buildRange(start, i-1);
                    ConflictedRegion region= new DefaultConflictedRegion(currentConflict, range);
                    map.put(range, region);
                    start=i;
                    currentConflict = conflict;
                }
            }
            
        }
        Range range = Range.buildRange(start, consensus.getLength()-1);
        ConflictedRegion region= new DefaultConflictedRegion(currentConflict, range);
        map.put(range, region);
    }
    
    @Override
    public Iterator<ConflictedRegion> iterator() {
        return map.values().iterator();
    }
    
    

}
