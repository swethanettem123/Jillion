package org.jcvi.jillion.sam.cigar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.core.util.iter.IteratorUtil;

public final class Cigar implements Iterable<CigarElement>{

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
		String trimmedString = cigarString.trim();
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
				if(!isDigit(next)){
					
					if(length ==0){
						throw new IllegalArgumentException("invalid cigar string " + cigarString);
					}
					CigarOperation op = CigarOperation.parseOp(next);
					builder.addElement(new CigarElement(op, length));
					break;
				}else{
					length = length*10 +( next - '0'); 
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
		//defensive copy
		CigarElement[] copy = new CigarElement[elements.length];
		System.arraycopy(elements, 0, copy, 0, elements.length);
		return new ArrayIterator<CigarElement>(copy);
	}



	public int getRawUnPaddedReadLength(){
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
	
	public int getRawPaddedReadLength(){
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
	
	public int getPaddedReadLength(){
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
	 * does not match the cigar unpadded length returned by {@link #getRawUnPaddedReadLength()}
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
	 * @return
	 */
	public  String toCigarString() {
		StringBuilder builder = new StringBuilder(3*elements.length);
		for(CigarElement e : elements){
			builder.append(e.getLength());
			builder.append(e.getOp().getOpCode());
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



	public static class Builder{
		private final List<CigarElement> elements;
		
		public Builder(int size){
			elements = new ArrayList<CigarElement>(size);
		}
		public Builder(){
			elements = new ArrayList<CigarElement>();
		}
		public Builder addElement(CigarOperation op, int length){
			return addElement(new CigarElement(op, length));
		}
		public Builder addElement(CigarElement e){
			if(e ==null){
				throw new NullPointerException("element can not be null");
			}
			elements.add(e);
			return this;
		}
		
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
	 * does not match the cigar unpadded length returned by {@link #getRawUnPaddedReadLength()}
	 */
	@SuppressWarnings("fallthrough")
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SF_SWITCH_FALLTHROUGH")
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
			case DELETION : 
			case PADDING : 
							char[] gaps = new char[e.getLength()];
							Arrays.fill(gaps, '-');
							builder.insert(currentOffset, gaps);
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
}