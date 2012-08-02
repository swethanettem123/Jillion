package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.ResidueSequenceBuilder;
import org.jcvi.common.core.util.GrowableByteArray;

public final class AminoAcidSequenceBuilder implements ResidueSequenceBuilder<AminoAcid,AminoAcidSequence>{
	private static final int DEFAULT_CAPACITY = 20;
	private GrowableByteArray builder;
	private int numberOfGaps=0;
	public AminoAcidSequenceBuilder(){
		builder = new GrowableByteArray(DEFAULT_CAPACITY);
	}
	
	public AminoAcidSequenceBuilder(int initialCapacity){
		builder = new GrowableByteArray(initialCapacity);
	}
	public AminoAcidSequenceBuilder(CharSequence sequence){
		builder = new GrowableByteArray(sequence.length());
		append(AminoAcids.parse(sequence.toString()));
	}
	@Override
	public AminoAcidSequenceBuilder append(
			AminoAcid residue) {
		if(residue==AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.append(residue.getOrdinalAsByte());
		return this;
	}

	
	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> append(
			Iterable<AminoAcid> sequence) {
		for(AminoAcid aa : sequence){
			append(aa);
		}
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> append(
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return append(otherBuilder.asList());
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> append(
			String sequence) {
		return append(AminoAcids.parse(sequence));
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset, String sequence) {
		List<AminoAcid> list = AminoAcids.parse(sequence);
		byte[] array = new byte[list.size()];
		int i=0;
		for(AminoAcid aa :list){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			array[i]=(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, array);
		return this;
	}

	@Override
	public long getLength() {
		return builder.getCurrentLength();
	}
	@Override
	public long getUngappedLength() {
		return builder.getCurrentLength() - numberOfGaps;
	}
	
	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> replace(
			int offset, AminoAcid replacement) {
		if(AminoAcid.values()[builder.get(offset)] == AminoAcid.Gap){
			numberOfGaps--;			
		}
		if(replacement == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.replace(offset, replacement.getOrdinalAsByte());
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> delete(
			Range range) {
		for(AminoAcid aa : asList(range)){
			if(aa == AminoAcid.Gap){
				numberOfGaps --;
			}
		}
		builder.remove(range);
		return this;
	}

	@Override
	public int getNumGaps() {
		return numberOfGaps;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> prepend(
			String sequence) {			
		return insert(0, sequence);
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset, Iterable<AminoAcid> sequence) {
		GrowableByteArray temp = new GrowableByteArray(DEFAULT_CAPACITY);
		for(AminoAcid aa :sequence){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			temp.append(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, temp);
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset,
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return insert(offset,otherBuilder.toString());
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset, AminoAcid base) {
		if(base == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.insert(offset, base.getOrdinalAsByte());
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> prepend(
			Iterable<AminoAcid> sequence) {
		return insert(0, sequence);
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> prepend(
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return prepend(otherBuilder.toString());
	}

	@Override
	public AminoAcidSequence build() {
		return build(builder.toArray());
	}

	@Override
	public AminoAcidSequence build(Range range) {
		byte[] temp = trimBytes(range);
		return build(temp);
	}

	private byte[] trimBytes(Range range) {
		byte[] fullArray =builder.toArray();
		byte[] temp = new byte[(int)range.getLength()];
		System.arraycopy(fullArray, (int)range.getBegin(), temp, 0, temp.length);
		return temp;
	}
	private List<AminoAcid> convertFromBytes(byte[] array){
		List<AminoAcid> aas = new ArrayList<AminoAcid>(array.length);
		for(int i=0; i<array.length; i++){
			aas.add(AminoAcid.values()[array[i]]);
		}
		return aas;
	}
	private AminoAcidSequence build(byte[] seqToBuild){
		List<AminoAcid> asList = convertFromBytes(seqToBuild);
		if(numberOfGaps>0 && hasGaps(asList)){
			return new CompactAminoAcidSequence(asList);
		}
		//no gaps
		
		return new UngappedAminoAcidSequence(asList);
	}
	private boolean hasGaps(List<AminoAcid> asList) {
		for(AminoAcid aa : asList){
			if(aa == AminoAcid.Gap){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<AminoAcid> asList(Range range) {
		AminoAcidSequence s = build();
		List<AminoAcid> list = new ArrayList<AminoAcid>((int)range.getLength());
		Iterator<AminoAcid> iter = s.iterator(range);
		while(iter.hasNext()){
			list.add(iter.next());
		}
		return list;
	}


	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> trim(Range range) {
		byte[] temp = trimBytes(range);
		AminoAcidSequence seq =build(temp);
		this.builder = new GrowableByteArray(temp);
		this.numberOfGaps =seq.getNumberOfGaps();
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> copy() {
		return new AminoAcidSequenceBuilder(builder.toString());
		
	}

	@Override
	public List<AminoAcid> asList() {
		return AminoAcids.parse(builder.toString());
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> reverse() {
		builder.reverse();
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> ungap() {

		AminoAcidSequence list = build(builder.toArray());
		if(list.getNumberOfGaps() !=0){
			List<Integer> gapOffsets =list.getGapOffsets();
			for(int i=gapOffsets.size()-1; i>=0; i--){
				builder.remove(i);
			}
		}
		numberOfGaps=0;
		return this;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

}
