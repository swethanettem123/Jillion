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
package org.jcvi.jillion.experimental.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.align.NucleotideSequenceAlignment;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.align.NucleotideSequenceAlignmentBuilder;
import org.junit.Before;
import org.junit.Test;
public class TestNucleotideSequenceAlignmentBuilder {

	NucleotideSequenceAlignmentBuilder sut;
	
	@Before
	public void setup(){
		sut = new NucleotideSequenceAlignmentBuilder();
	}
	@Test
	public void noAlignmentShouldHave0PercentIdent(){
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(0, alignment.getAlignmentLength());
		assertEquals(0, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(0D, alignment.getPercentIdentity(),0D);
		assertTrue(alignment.getGappedQueryAlignment().toString().isEmpty());
		assertTrue(alignment.getGappedSubjectAlignment().toString().isEmpty());
	}
	
	@Test
	public void onlyOneMatch(){
		sut.addMatch(Nucleotide.Adenine);
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(1, alignment.getAlignmentLength());
		assertEquals(0, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(1D, alignment.getPercentIdentity(),0D);
		assertEquals("A", alignment.getGappedQueryAlignment().toString());
		assertEquals("A", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void onlyOneMisMatch(){
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(1, alignment.getAlignmentLength());
		assertEquals(1, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(0D, alignment.getPercentIdentity(),0D);
		assertEquals("G", alignment.getGappedQueryAlignment().toString());
		assertEquals("A", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void manymatchesAndMismatches(){
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());
		
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		sut.addMismatch(Nucleotide.Thymine,Nucleotide.Adenine);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());
		
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(10, alignment.getAlignmentLength());
		assertEquals(2, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(.8D, alignment.getPercentIdentity(),0D);
		assertEquals("ACGTGTACGT", alignment.getGappedQueryAlignment().toString());
		assertEquals("ACGTAAACGT", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void manymatchesAndMismatchesAndGap(){
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());
		
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		sut.addGap(Nucleotide.Thymine,Nucleotide.Gap);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());
		
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(10, alignment.getAlignmentLength());
		assertEquals(1, alignment.getNumberOfMismatches());
		assertEquals(1, alignment.getNumberOfGapOpenings());
		assertEquals(.8D, alignment.getPercentIdentity(),0D);
		assertEquals("ACGTGTACGT", alignment.getGappedQueryAlignment().toString());
		assertEquals("ACGTA-ACGT", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void buildFromTraceback(){
		sut = new NucleotideSequenceAlignmentBuilder(true);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());
		
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		sut.addGap(Nucleotide.Thymine,Nucleotide.Gap);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());

		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(10, alignment.getAlignmentLength());
		assertEquals(1, alignment.getNumberOfMismatches());
		assertEquals(1, alignment.getNumberOfGapOpenings());
		assertEquals(.8D, alignment.getPercentIdentity(),0D);

		assertEquals("TGCATGTGCA", alignment.getGappedQueryAlignment().toString());		
		assertEquals("TGCA-ATGCA", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void alignmentNotEqualToNull(){
		sut = new NucleotideSequenceAlignmentBuilder(true);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());		

		NucleotideSequenceAlignment alignment = sut.build();
		
		assertNotEquals(alignment, null);
	}
	@Test
	public void alignmentNotEqualToOtherObject(){
		sut = new NucleotideSequenceAlignmentBuilder(true);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());		

		NucleotideSequenceAlignment alignment = sut.build();
		
		assertNotEquals(alignment, "not an alignment");
	}
	
	@Test
	public void equalsSameRef(){
		sut = new NucleotideSequenceAlignmentBuilder(true);
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());		

		NucleotideSequenceAlignment alignment = sut.build();
		
		TestUtil.assertEqualAndHashcodeSame(alignment, alignment);
	}
	
	@Test
	public void equalsSameValues(){
		sut = new NucleotideSequenceAlignmentBuilder();
		sut.addMatches(new NucleotideSequenceBuilder("ACGT").build());		

		NucleotideSequenceAlignment alignment = sut.build();
		
		NucleotideSequenceAlignmentBuilder builder2 = new NucleotideSequenceAlignmentBuilder();
		builder2.addMatches(new NucleotideSequenceBuilder("ACGT").build());		

		NucleotideSequenceAlignment alignment2 = builder2.build();
		
		TestUtil.assertEqualAndHashcodeSame(alignment, alignment2);
	}
	
	@Test
	public void differentValuesNotEqual(){
		sut = new NucleotideSequenceAlignmentBuilder();
		sut.addMatches("ACGT");	
		
		NucleotideSequenceAlignment alignment = sut.build();
		
		NucleotideSequenceAlignmentBuilder builder2 = new NucleotideSequenceAlignmentBuilder();
		builder2.addMatches("AC");
		
		builder2.addMismatches("GT","TA");

		NucleotideSequenceAlignment alignment2 = builder2.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(alignment, alignment2);
	}
	
	
}
