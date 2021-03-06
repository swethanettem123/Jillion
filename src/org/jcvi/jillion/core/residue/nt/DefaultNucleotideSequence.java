/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
import java.util.Objects;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;

/**
 * Default
 * implementation of a {@link NucleotideSequence}.  
 * Depending on the codec used,
 * the nucleotides can be encoded as 4 bits, 2 bits
 * or some other efficient manner.
 * @author dkatzel
 *
 *
 */
final class DefaultNucleotideSequence extends AbstractResidueSequence<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder> implements NucleotideSequence{

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
    public Range toUngappedRange(Range gappedRange){
        ensureRangeWithinSequence(gappedRange, this::getLength);
        return codec.toUngappedRange(data, gappedRange);
    }
    @Override
    public Range toGappedRange(Range ungappedRange){
        ensureRangeWithinSequence(ungappedRange, this::getUngappedLength);
        return codec.toGappedRange(data, ungappedRange);
    }



    @Override
    public Stream<Range> findMatches(Pattern pattern) {
        return codec.matches(data, pattern);
    }




    @Override
    public Stream<Range> findMatches(Pattern pattern, Range subSequenceRange) {
        return codec.matches(data, pattern, subSequenceRange);
    }




    /**
     * Check the input Range boundaries against this sequence.
     * @param gappedRange the range to check.
     * @param lengthSupplier a lambda that will return the length
     * of this sequence as a long.  This method can be used to determine
     * if we want to check gapped length or ungapped length without 
     */
    private void ensureRangeWithinSequence(Range gappedRange, LongSupplier lengthSupplier) {
        Objects.requireNonNull(gappedRange);
        long end = gappedRange.getEnd();
        long begin = gappedRange.getBegin();
        //the conditions are broken into 2 
        //separate if blocks because getLength()
        //may be an expensive operation
        //so we only want to compute it if we have to.
        if(end < 0 || begin < 0){
            throw new IndexOutOfBoundsException();
            
        }
        long length = lengthSupplier.getAsLong();
        if(end >= length || begin >= length){
            throw new IndexOutOfBoundsException();
        }
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

	@Override
	public NucleotideSequenceBuilder toBuilder() {
		return new NucleotideSequenceBuilder(this);
	}
	
	@Override
        public NucleotideSequenceBuilder toBuilder(Range range) {
                return new NucleotideSequenceBuilder(this, range);
        }
	
	 @Override
	    public NucleotideSequence asSubtype(){
	        return this;
	    }




	@Override
	public List<Range> getRangesOfNs() {
		return codec.getNRanges(data);
	}
	
	
}
