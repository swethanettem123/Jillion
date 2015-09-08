/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.internal.core.EncodedSequence;

/**
 * {@code DefaultEncodedQualitySequence} 
 * decorates an {@link EncodedSequence} to allow  
 * it to implement the {@link QualitySequence}
 * interface.
 * @author dkatzel
 */
final class EncodedQualitySequence extends EncodedSequence<PhredQuality> implements QualitySequence{

   
   

	public EncodedQualitySequence(QualitySymbolCodec codec, byte[] data) {
		super(codec, data);
	}


	@Override
	public int hashCode(){
		return super.hashCode();
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof QualitySequence)){
        	return false;
        }
        return super.equals(obj);
    }
    
    /**
	 * {@inheritDoc}
	 */
	public byte[] toArray(){
       return ((QualitySymbolCodec)getCodec()).toQualityValueArray(data);
    }


	@Override
	public double getAvgQuality() {
		return ((QualitySymbolCodec)getCodec()).getAvgQuality(data);
	}


	@Override
	public PhredQuality getMinQuality() {
		return ((QualitySymbolCodec)getCodec()).getMinQuality(data);
	}


	@Override
	public PhredQuality getMaxQuality() {
		return ((QualitySymbolCodec)getCodec()).getMaxQuality(data);
	}
	
	
}
