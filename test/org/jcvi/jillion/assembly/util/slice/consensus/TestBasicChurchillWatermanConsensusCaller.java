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
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.slice.consensus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.util.slice.IdedSlice;
import org.jcvi.jillion.assembly.util.slice.Slice;
import org.jcvi.jillion.assembly.util.slice.consensus.BasicChurchillWatermanConsensusCaller;
import org.jcvi.jillion.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.jillion.assembly.util.slice.consensus.ConsensusResult;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.junit.runners.Parameterized.Parameters;

public class TestBasicChurchillWatermanConsensusCaller extends
        TestConicConsensusCaller {

    public TestBasicChurchillWatermanConsensusCaller(List<Slice<?>> slices,
            List<ConsensusResult> expectedConsensus) {
        super(slices, expectedConsensus);
    }
    @Override
    protected ConsensusCaller getConsensusCaller() {
        return new BasicChurchillWatermanConsensusCaller(PhredQuality.valueOf(30));
    }
    
    
    @Parameters
    public static Collection<?> data(){
       List<Object[]> data = new ArrayList<Object[]>();
       for(Entry<List<IdedSlice>, List<ConsensusResult>> entry: ConsensusCallerTestUtil.generateChurchillWatermanData().entrySet()){
           data.add(new Object[]{entry.getKey(), entry.getValue()});
       }
        
        return data;
        
    }
}