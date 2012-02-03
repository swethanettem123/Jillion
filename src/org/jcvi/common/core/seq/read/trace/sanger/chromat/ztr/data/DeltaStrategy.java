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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.io.ValueSizeStrategy;
/**
 * There are several different possible Delta strategies
 * that can be used to compute the delta between 2 consecutive
 * values.
 * @author dkatzel
 *
 *
 */
public enum DeltaStrategy {
	
	LEVEL_1(1){
		 @Override
		    protected int computeDelta(int u1, int u2, int u3) {
		        return u1;
		    }

		 
	},
	LEVEL_2(2){
		@Override
	    protected int computeDelta(int u1, int u2, int u3) {
	        return  2*u1 - u2;
	    }
	},
	LEVEL_3(3){
		@Override
	    protected int computeDelta(int u1, int u2, int u3) {
	        return 3*u1 - 3*u2 + u3;
	    }
		
	};
	
	private final int level;
	
	private DeltaStrategy(int level){
		this.level = level;
	}
	private static final Map<Integer, DeltaStrategy> MAP;
	static{
		MAP = new HashMap<Integer, DeltaStrategy>();
		for(DeltaStrategy strategy : values()){
			MAP.put(Integer.valueOf(strategy.level), strategy);
		}
	}
	
	public static DeltaStrategy getStrategyFor(int level){
		Integer i = Integer.valueOf(level);
		if(MAP.containsKey(i)){
			return MAP.get(i);
		}
		throw new IllegalArgumentException("no delta strategy for level "+level);
	}
    /**
     * use the delta strategy computation
     * to uncompress the next value from the given compressed buffer
     * and write it to the given out buffer. 
     * @param compressed buffer containing compressed data.
     * @param out buffer to write uncompressed (undelta'ed) value.
     */    
    public void unCompress(ByteBuffer compressed, ValueSizeStrategy valueSizeStrategy ,ByteBuffer out) {
        int u1 = 0,u2 = 0,u3=0;
        
        while(compressed.hasRemaining()){
            int value =valueSizeStrategy.getNext(compressed) + computeDelta(u1, u2, u3);
            valueSizeStrategy.put(out, value);
            //update previous values for next round
            u3 = u2;
            u2 = u1;            
            u1 = value;            
        }
    }
    
    public void compress(ByteBuffer uncompressed, ValueSizeStrategy valueSizeStrategy, ByteBuffer out){
    	 int u1 = 0,u2 = 0,u3=0;
    	 while(uncompressed.hasRemaining()){
             int next = valueSizeStrategy.getNext(uncompressed);
			int value =next - computeDelta(u1, u2, u3);
             valueSizeStrategy.put(out, value);
             //update previous values for next round
             u3 = u2;
             u2 = u1;            
             u1 = next;            
         }
    }
    /**
     * Computes the current Delta given the previous 3 delta values. 
     * @param u1 previous delta value.
     * @param u2 the 2nd previous delta value.
     * @param u3 the 3rd previous delta value.
     * @return the current delta value.
     */
    protected abstract int computeDelta(int u1, int u2, int u3);
    
}
