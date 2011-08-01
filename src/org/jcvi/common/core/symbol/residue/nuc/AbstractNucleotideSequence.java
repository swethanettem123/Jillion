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
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;

public abstract class AbstractNucleotideSequence implements NucleotideSequence{

    


    @Override
    public long getUngappedLength(){
        return getLength() - getNumberOfGaps();
    }
    @Override
    public int computeNumberOfInclusiveGapsInGappedValidRangeUntil(int gappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapIndexes()){
            if(gapIndex.intValue() <=gappedValidRangeIndex){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }
    @Override
    public int computeNumberOfInclusiveGapsInUngappedValidRangeUntil(int ungappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapIndexes()){
            //need to account for extra length due to gaps being added to ungapped index
            if(gapIndex.intValue() <=ungappedValidRangeIndex + numberOfGaps){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }

   
    
    @Override
    public List<Nucleotide> decode(Range range) {
        if(range==null){
            return decode();
        }
        List<Nucleotide> result = new ArrayList<Nucleotide>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

    @Override
    public List<Nucleotide> decodeUngapped() {
        List<Nucleotide> withoutGaps = decode();
        final List<Integer> gapIndexes = getGapIndexes();
        for(int i= gapIndexes.size()-1; i>=0; i--){
            withoutGaps.remove(gapIndexes.get(i).intValue());
        }
        return withoutGaps;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int toUngappedIndex(int gappedIndex) {
        return gappedIndex - computeNumberOfInclusiveGapsInGappedValidRangeUntil(gappedIndex);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int toGappedIndex(int ungappedIndex) {
        return ungappedIndex +computeNumberOfInclusiveGapsInUngappedValidRangeUntil(ungappedIndex);
    }
    
    
}
