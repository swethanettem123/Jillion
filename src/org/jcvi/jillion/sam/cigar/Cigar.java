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
package org.jcvi.jillion.sam.cigar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
/**
 * {@code Cigar} is an Object for a single
 * read alignment encoded in the CIGAR format.
 * 
 * @author dkatzel
 *
 */
public final class Cigar implements Iterable<CigarElement>{
    
        private static Pattern REMOVE_WHITESPACE = Pattern.compile("\\s+");
	/**
	 * Singleton instance of an empty {@link Cigar}.
	 */
	public static final Cigar EMPTY_CIGAR = new Cigar.Builder(0).build();
	/**
	 * The various types of clipping
	 * operations performed on a sequence.
	 * @author dkatzel
	 *
	 */
	public static enum ClipType {
		/**
		 * The raw sequence including
		 * all bases provided that are clipped.
		 */
		RAW,
		/**
		 * The sequence excluding all
		 * clipped bases (no hard or soft
		 * clipped bases are included).
		 */
		SOFT_CLIPPED,
		/**
		 * The sequence excluding
		 * any hard clipped based
		 * (no hard clipped bases are included,
		 * but soft clipped bases are included).
		 */
		HARD_CLIPPED,
	}


	private static final String UN_AVAILABLE = "*";
	private final CigarElement[] elements;
	/**
	 * Parse the given Cigar String into a {@link Cigar}
	 * object.  If the cigarString is '*' then this 
	 * value will return {@code null} since there is
	 * no alignment information.  An invalid cigar string
	 * will throw an Exception.
	 * @param cigarString the cigarString to parse;
	 * can not be null.
	 * @return a {@link Cigar} if the cigar is a valid
	 * cigar string that represents a known alignment;
	 * {@code null} if the cigar string "*".
	 * @throws NullPointerException if cigarString is null.
	 * @throws IllegalArgumentException if the cigarString is invalild.
	 */
	public static Cigar parse(String cigarString){
	    //some tools like exonerate have whitespace
		String trimmedString = REMOVE_WHITESPACE.matcher(cigarString).replaceAll("");
		if(trimmedString.isEmpty()){
			throw new IllegalArgumentException("cigar string can not be null");
		}
		if(trimmedString.equals(UN_AVAILABLE)){
			return null;
		}
		Cigar.Builder builder = new Cigar.Builder();
		//format is ([0-9]+[MIDNSHPX=])+
		//probably fastest to just
		//parse char by char
		PrimitiveCharIterator iter = new PrimitiveCharIterator(trimmedString);
		while(iter.hasNext()){
			//parse next Element
			int length=0;
			do{
				char next = iter.next();
				if(isDigit(next)){
					length = length*10 +( next - '0');					
				}else{
					if(length ==0){
						throw new IllegalArgumentException("invalid cigar string " + cigarString);
					}
					CigarOperation op = CigarOperation.parseOp(next);
					builder.addElement(new CigarElement(op, length));
					break;
				}
				
			}while(true);			
		}
		return builder.build();
		
	}
	
	
	
	private static boolean isDigit(char c){
		return c >='0' && c<= '9';
	}
	
	private Cigar(CigarElement[] elements) {
		this.elements = elements;
	}

	@Override
	public Iterator<CigarElement> iterator() {
		return new ArrayIterator<CigarElement>(elements);
	}

	/**
	 * Get the length of this cigar
	 * including padding (gaps)
	 * with the provided clip type
	 * @param type the {@link ClipType}
	 * of clipping operations to include in the length
	 * calculation; can not be null.
	 * @return an int.
	 * @throws NullPointerException if type is null.
	 */
	public int getPaddedReadLength(ClipType type){
		switch(type){
			case RAW : return getRawPaddedReadLength();
			case SOFT_CLIPPED : return getPaddedReadLength();
			case HARD_CLIPPED : return getSoftPaddedReadLength();
			default : 
				//shouldn't happen unless we add a new type 
				//and forget to include it in the switch()
				throw new IllegalArgumentException("unknown clip type : " + type);
		}
	}
	/**
	 * Get the length of this cigar
	 * excluding padding (gaps)
	 * with the provided clip type
	 * @param type the {@link ClipType}
	 * of clipping operations to include in the length
	 * calculation; can not be null.
	 * @return an int.
	 * @throws NullPointerException if type is null.
	 */
	public int getUnpaddedReadLength(ClipType type){
		switch(type){
			case RAW : return getRawUnPaddedReadLength();
			case SOFT_CLIPPED : return getUnPaddedReadLength();
			case HARD_CLIPPED : return getSoftUnPaddedReadLength();
			default : 
				//shouldn't happen unless we add a new type 
				//and forget to include it in the switch()
				throw new IllegalArgumentException("unknown clip type : " + type);
		}
	}

	private int getRawUnPaddedReadLength(){
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					length += element.getLength();
					break;
				case DELETION:
					break;
				case SKIPPED:
					break;
				case SOFT_CLIP:
					length += element.getLength();
					break;
				case HARD_CLIP:
					length += element.getLength();
					break;
				case PADDING:
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
	
	private int getRawPaddedReadLength(){
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					length += element.getLength();
					break;
				case DELETION:
					length += element.getLength();
					break;
				case SKIPPED:
					break;
				case SOFT_CLIP:
					length += element.getLength();
					break;
				case HARD_CLIP:
					length += element.getLength();
					break;
				case PADDING:
					length += element.getLength();
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
	
	private int getSoftPaddedReadLength(){
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					length += element.getLength();
					break;
				case DELETION:
					length += element.getLength();
					break;
				case SKIPPED:
					break;
				case SOFT_CLIP:
					length += element.getLength();
					break;
				case HARD_CLIP:					
					break;
				case PADDING:
					length += element.getLength();
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
	public int getUnPaddedReadLength(){
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					length += element.getLength();
					break;
				case DELETION:
					break;
				case SKIPPED:
					break;
				case SOFT_CLIP:
					//don't count clip points
					break;
				case HARD_CLIP:
					//don't count clip points
					break;
				case PADDING:
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
	
	private int getSoftUnPaddedReadLength(){
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					length += element.getLength();
					break;
				case DELETION:
					break;
				case SKIPPED:
					break;
				case SOFT_CLIP:
					length += element.getLength();
					break;
				case HARD_CLIP:
					//don't count clip points
					break;
				case PADDING:
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
	
	private int getPaddedReadLength(){
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					length += element.getLength();
					break;
				case DELETION:
					length += element.getLength();
					break;
				case SKIPPED:
					break;
				case SOFT_CLIP:
					//don't count clip points
					break;
				case HARD_CLIP:
					//don't count clip points
					break;
				case PADDING:
					length += element.getLength();
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
	
	public int getNumberOfElements(){
		return elements.length;
	}
	
	public CigarElement getElement(int i){
		return elements[i];
	}
	
	public Iterator<CigarElement> getElementIterator(){
		return IteratorUtil.createIteratorFromArray(elements);
	}
	/**
	 * Given this Cigar and the corresponding raw ungapped
	 * sequence from the sequencing machine, create the trimmed
	 * gapped {@link NucleotideSequence}.
	 * @param rawUngappedSequence the raw ungapped
	 * sequence from the sequencing machine; can not be null.
	 * @return a {@link NucleotideSequence} of the gapped
	 * trimmed sequence, will not be null.
	 * @throws NullPointerException if rawUngappedSequence
	 * is null.
	 * @throws IllegalArgumentException if rawUngappedSequence has gaps.
	 * @throws IllegalArgumentException if rawUngappedSequence ungapped length
	 * does not match the cigar unpadded length.
	 */
	public NucleotideSequence toGappedTrimmedSequence(NucleotideSequence rawUngappedSequence){
		
		return toGappedTrimmedSequenceBuilder(rawUngappedSequence)
								.build();
	}
	
	@Override
	public String toString(){
		return toCigarString();
	}
	/**
	 * Convert this {@link Cigar} into a formatted "cigar String".
	 * The returned cigar string should be valid input
	 * into {@link Cigar#parse(String)} such that: 
	 * <pre>
	 * Cigar.parse( this.toCigarString() ).equals(this);
	 * </pre>
	 * @return the Cigar as a String using the standard CIGAR format.
	 */
	public  String toCigarString() {
		StringBuilder builder = new StringBuilder(3*elements.length);
		for(CigarElement e : elements){
			builder.append(e.getLength())
					.append(e.getOp().getOpCode());
		}
		return builder.toString();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Cigar)) {
			return false;
		}
		Cigar other = (Cigar) obj;
		if (!Arrays.equals(elements, other.elements)) {
			return false;
		}
		return true;
	}


	/**
	 * Builder class to programmatically build
	 * up a {@link Cigar} object.
	 * 
	 * @author dkatzel
	 *
	 */
	public static class Builder{
		private final List<CigarElement> elements;
		/**
		 * Create a new Builder object
		 * with the expected number of cigar elements.
		 * This is only a memory optimization,
		 * if it turns out there are more elements
		 * then specified, then the builder will
		 * grow the backing array automatically.
		 * 
		 * @param size the expected number of cigar elements
		 * that will be in this builder; can not be negative.
		 * 
		 * @throws IllegalArgumentException if size is negative.
		 */
		public Builder(int size){
			elements = new ArrayList<CigarElement>(size);
		}
		/**
		 * Create a new Builder object.
		 */
		public Builder(){
			elements = new ArrayList<CigarElement>();
		}
		/**
		 * Create a new {@link Builder}
		 * that is initialized to the contents
		 * of the provided {@link Cigar} object;
		 * 
		 * @param cigar the cigar to copy into this Builder;
		 * can not be null.
		 * 
		 * @throws NullPointerException if cigar is null.
		 */
		public Builder(Cigar cigar) {
			elements = new ArrayList<CigarElement>(cigar.getNumberOfElements());
			for(CigarElement e : cigar){
				elements.add(e);
			}
		}
		public Builder addElement(CigarOperation op, int length){
			return addElement(new CigarElement(op, length));
		}
		/**
		 * Add the given {@link CigarElement} to this builder
		 * as the next element.
		 * @param e the {@link CigarElement} to add; can not be null.
		 * 
		 * @return this.
		 * 
		 * @throws NullPointerException if e is null.
		 */
		public Builder addElement(CigarElement e){
			if(e ==null){
				throw new NullPointerException("element can not be null");
			}
			elements.add(e);
			return this;
		}
		/**
		 * Create a new {@link Cigar} object using
		 * the current contents of the builder.
		 * 
		 * @return a new {@link Cigar}; will never be null.
		 * 
		 * @throws IllegalStateException if the builder built a cigar
		 * with an illegal combination of {@link CigarElement}s.
		 * For example, {@link CigarOperation#HARD_CLIP}s if present,
		 * must be the first/and or last elements and {@link CigarOperation#SOFT_CLIP}
		 * must be immediately inside a {@link CigarOperation#HARD_CLIP}s.
		 */
		public Cigar build(){
			
			CigarElement[] array = elements.toArray(new CigarElement[elements.size()]);
			validate(array);
			return new Cigar(array);
		}
		private void validate(CigarElement[] array) {
			//only first and last ops may be hard_clips
			for(int i=0; i<array.length; i++){
				if(i !=0 && i!=array.length-1 && array[i].getOp() ==CigarOperation.HARD_CLIP){
					throw new IllegalStateException("hard clips may only be first and/or last operations");
				}
				if(array[i].getOp() == CigarOperation.SOFT_CLIP){
					if(i<array.length/2){
						//check left
						for(int j=0; j<i; j++){
							if(array[j].getOp() != CigarOperation.HARD_CLIP){
								throw new IllegalStateException("soft clips may only have hard clips between them and the end of the CIGAR string");
							}
						}
					}else{
						//check right
						for(int j=i+1; j<array.length; j++){
							if(array[j].getOp() != CigarOperation.HARD_CLIP){
								throw new IllegalStateException("soft clips may only have hard clips between them and the end of the CIGAR string");
							}
						}
					}
				}
			}
			
		}
		/**
		 * Remove the {@link CigarOperation#HARD_CLIP} at the
		 * edges of this builder.  Will only remove the 
		 * {@link CigarOperation#HARD_CLIP}s that are already present.
		 * Calling this method will not affect any new {@link CigarOperation#HARD_CLIP}s
		 * that are added later.
		 * 
		 * @return this.
		 */
		public Builder removeHardClips() {
			//hard clips must be the first and/or last operations
			int lastIndex = elements.size()-1;
			if(elements.get(lastIndex).getOp() == CigarOperation.HARD_CLIP){
				elements.remove(lastIndex);
			}
			if(elements.get(0).getOp() == CigarOperation.HARD_CLIP){
				elements.remove(0);
			}
			return this;
		}
	}
	
	private static final class PrimitiveCharIterator{
		private final char[] array;
		private int i=0;
		public PrimitiveCharIterator(char[] array) {
			this.array = array;
		}
		
		public PrimitiveCharIterator(String string) {
			this(string.toCharArray());
		}

		public boolean hasNext(){
			return i < array.length;
		}
		
		public char next(){
			if(!hasNext()){
				throw new IllegalStateException("no more elements");
			}
			return array[i++];
		}
	}

	public Range getValidRange() {
		int ungappedAlignedLength = getUnPaddedReadLength();
		int numberOfClippedLeadingBases = computeLeadingClippedBases();
		
		return new Range.Builder(ungappedAlignedLength)
						.shift(numberOfClippedLeadingBases)
						.build();
	}



	private int computeLeadingClippedBases() {
		int count=0;
		for(CigarElement e : elements){
			if(e.getOp()==CigarOperation.HARD_CLIP || e.getOp() == CigarOperation.SOFT_CLIP){
				count+=e.getLength();
			}else{
				break;
			}
		}
		return count;
	}


	/**
	 * Given this Cigar and the corresponding raw ungapped
	 * sequence from the sequencing machine, create the trimmed
	 * gapped {@link NucleotideSequenceBuilder}.
	 * @param rawUngappedSequence the raw ungapped
	 * sequence from the sequencing machine; can not be null.
	 * @return a {@link NucleotideSequenceBuilder} of the gapped
	 * trimmed sequence, will not be null.
	 * @throws NullPointerException if rawUngappedSequence
	 * is null.
	 * @throws IllegalArgumentException if rawUngappedSequence has gaps.
	 * @throws IllegalArgumentException if rawUngappedSequence ungapped length
	 * does not match the cigar unpadded length.
	 */
	@SuppressWarnings("fallthrough")
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
	public NucleotideSequenceBuilder toGappedTrimmedSequenceBuilder( NucleotideSequence rawUngappedSequence) {
		if(rawUngappedSequence.getNumberOfGaps() !=0){
			throw new IllegalArgumentException("rawUngapped Sequence can not have gaps");
		}
		
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(rawUngappedSequence);
		int currentOffset=0;
		int ungappedLength=0;
		for(CigarElement e : elements){
			switch(e.getOp()){
			case HARD_CLIP:
			case SOFT_CLIP: builder.delete(new Range.Builder(e.getLength())
												.shift(currentOffset)
												.build());
							ungappedLength+=e.getLength();
							break;
			//insert gap into read
			case DELETION : char[] gaps = new char[e.getLength()];
							Arrays.fill(gaps, '-');
							builder.insert(currentOffset, gaps);
							currentOffset+=e.getLength();
							break;
			case PADDING : //silent deletion against padded ref?
						/*	char[] pads = new char[e.getLength()];
							Arrays.fill(pads, '-');
							builder.insert(currentOffset, pads);
							currentOffset+=e.getLength();
							*/
							break;
			case SKIPPED:
				//In cDNA-to-genome alignment, we may want to distinguish introns from deletions in exons.
				//We introduce openation 'N' to represent long skip on the reference sequence.
				//Suppose the spliced alignment is:
				//REF: AGCTAGCATCGTGTCGCCCGTCTAGCATACGCATGATCGACTGTCAGCTAGTCAGACTAGTCGATCGATGTG
				//READ:          GTGTAACCC................................TCAGAATA
				//where '...' on the read sequence indicates intron. 
				//The CIGAR for this alignment is : 9M32N8M.
				//
				//it looks like tophat just skips them completely?
				//skip completely
				
				char[] skips = new char[e.getLength()];
				Arrays.fill(skips, '-');
				builder.insert(currentOffset, skips);
				
				currentOffset+=e.getLength();
				
				break;
			default :
				currentOffset+=e.getLength();
				ungappedLength+=e.getLength();
			}
		}
		if(ungappedLength != rawUngappedSequence.getLength()){
			throw new IllegalArgumentException("invalid input sequence length, expected " + ungappedLength + " but was " + rawUngappedSequence.getLength());
		}
		return builder;
	}



	public int getNumberOfReferenceBasesAligned() {
		int length=0;
		for(CigarElement element : elements){
			//This is an optimization to allow the 
	    	//compiler to use a tableswitch opcode
	    	//instead of the more general purpose
	    	//lookupswitch opcode.
	    	//tableswitch is an O(1) lookup
	    	//while lookupswitch is O(n) where n
	    	//is the number of case statements in the switch.
	    	//tableswitch requires consecutive case values.
	    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
			switch (element.getOp()) {
				case ALIGNMENT_MATCH:
					length += element.getLength();
					break;
				case INSERTION:
					//don't count insertions
					break;
				case DELETION:
					length += element.getLength();
					break;
				case SKIPPED:
					//skip does get counted!
					length += element.getLength();
					break;
				case SOFT_CLIP:
					//don't count clip points
					break;
				case HARD_CLIP:
					//don't count clip points
					break;
				case PADDING:
					//don't count padding
					break;
				case SEQUENCE_MATCH:
					length += element.getLength();
					break;
				case SEQUENCE_MISMATCH:
					length += element.getLength();
					break;
	
				default:
					// do not increase length
			}		
		}
		return length;
	}
}
