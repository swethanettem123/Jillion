/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;

/**
 * {@code DefaultNucleotideSequence} is the default
 * implementation of a {@link NucleotideSequence}.  
 * Depending on the {@link NucleotideCodec} used,
 * the nucleotides can be encoded as 4 bits, 2 bits
 * or some other efficient manner.
 * @author dkatzel
 *
 *
 */
final class DefaultNucleotideSequence extends AbstractResidueSequence<Nucleotide> implements NucleotideSequence{

	//This classes uses the Serialization Proxy Pattern
	//described in Effective Java 2nd Edition
	//to serialize final fields.
    /**
	 * 
	 */
	private static final long serialVersionUID = 7441128261035593978L;
	/**
     * {@link NucleotideCodec} used to decode the data.
     */
    private final transient NucleotideCodec codec;
    /**
     * Our data.
     */
    private final transient byte[] data;
    /**
     * Our HashCode value,
     * This value is lazy loaded
     * so we only have 
     * to compute the hashcode value
     * once.
     * 
     * We can afford to store it because
     * the Java memory model will padd out
     * the bytes anyway so we don't
     * take up any extra memory.
     */
    private transient int hash;

   
    
    DefaultNucleotideSequence(NucleotideCodec codec, byte[] data) {
		this.codec = codec;
		this.data = data;
	}



    
    @Override
	public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
		return codec.getNumberOfGapsUntil(data, gappedValidRangeIndex);
	}




	@Override
    public List<Integer> getGapOffsets() {
    	return codec.getGapOffsets(data);
    }

    @Override
    public Nucleotide get(long index) {     
    	return codec.decode(data, index);
    }

    @Override
    public long getLength() {
    	return codec.decodedLengthOf(data);
    }
    @Override
    public boolean isGap(int index) {
    	return codec.isGap(data, index);
    }
    @Override
    public int hashCode() {
		long length = getLength();
		if(hash==0 && length >0){
	        final int prime = 31;
	        int result = 1;
	        Iterator<Nucleotide> iter = iterator();
	        while(iter.hasNext()){
	        	result = prime * result + iter.next().hashCode();
	        }
	        hash= result;
		}
	    return hash;
        
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof NucleotideSequence)){
            return false;
        }
        NucleotideSequence other = (NucleotideSequence) obj;
        if(getLength() != other.getLength()){
        	return false;
        }
       Iterator<Nucleotide> iter = iterator();
       Iterator<Nucleotide> otherIter = other.iterator();
       while(iter.hasNext()){
    	   if(!iter.next().equals(otherIter.next())){
    		   return false;
    	   }
       }
       return true;
    }
    @Override
    public String toString() {
        return codec.toString(data);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps() {
    	return codec.getNumberOfGaps(data);
    }
	@Override
	public Iterator<Nucleotide> iterator() {
		return codec.iterator(data);
	}
	@Override
	public Iterator<Nucleotide> iterator(Range range) {
		return codec.iterator(data,range);
	}

	private Object writeReplace(){
		return new DefaultNucleotideSequenceProxy(this);
	}
	
	private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
		throw new java.io.InvalidObjectException("Proxy required");
	}
	
	private static final class DefaultNucleotideSequenceProxy implements Serializable{

		private static final long serialVersionUID = 6476363248864141050L;
		private final String bases;
		
		DefaultNucleotideSequenceProxy(DefaultNucleotideSequence seq){
			this.bases = seq.toString();
		}
		
		private Object readResolve(){
			DefaultNucleotideSequence seq = (DefaultNucleotideSequence) new NucleotideSequenceBuilder(bases)
																				.build();
			
			return new DefaultNucleotideSequence(seq.codec, seq.data);
		}
	}
	
}
