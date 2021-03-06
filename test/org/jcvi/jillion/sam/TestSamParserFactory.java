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
package org.jcvi.jillion.sam;

import static org.easymock.EasyMock.*;

import java.io.IOException;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamReferenceSequenceBuilder;
import org.jcvi.jillion.sam.header.SamVersion;
import org.junit.Test;

public class TestSamParserFactory {

	ResourceHelper resourceHelper = new ResourceHelper(TestSamParserFactory.class);
	
	private static final boolean IS_BAM = true;
	
	private static final boolean IS_SAM = false;
	
	private static enum HaltVisitor implements IAnswer<Void> {

		INSTANCE;
		
		@Override
		public Void answer() throws Throwable {

			SamVisitorCallback callback = (SamVisitorCallback) EasyMock.getCurrentArguments()[0];
			callback.haltParsing();
			
			return null;
		}
		
	}
	
	@Test
	public void parseHeaderOnlySamFile() throws IOException, InvalidAttributeException{
		SamParser sut = SamParserFactory.create(resourceHelper.getFile("example.sam"));
		
		SamVisitor visitor = createMockVisitorWithHeaderOnlyExpectations();
		
		replay(visitor);
		sut.parse(visitor);
		verify(visitor);
	}
	
	@Test
	public void parseHeaderOnlyBamFile() throws IOException, InvalidAttributeException{
		SamParser sut = SamParserFactory.create(resourceHelper.getFile("example.bam"));
		
		SamVisitor visitor = createMockVisitorWithHeaderOnlyExpectations();
		
		replay(visitor);
		sut.parse(visitor);
		verify(visitor);
	}
	@Test
	public void parseEntireSamFile() throws IOException, InvalidAttributeException{
		SamParser sut = SamParserFactory.create(resourceHelper.getFile("example.sam"));
		
		SamVisitor visitor = createMockVisitorWithEntireFileExpectations(IS_SAM);
		
		replay(visitor);
		sut.parse(visitor);
		verify(visitor);
	}
	
	@Test
	public void parseEntireBamFile() throws IOException, InvalidAttributeException{
		SamParser sut = SamParserFactory.create(resourceHelper.getFile("example.bam"));
		
		SamVisitor visitor = createMockVisitorWithEntireFileExpectations(IS_BAM);
		
		replay(visitor);
		sut.parse(visitor);
		verify(visitor);
	}
	
	@Test
	public void parseHalfSamFile() throws IOException, InvalidAttributeException{
		SamParser sut = SamParserFactory.create(resourceHelper.getFile("example.sam"));
		
		SamVisitor visitor = createMockVisitorWithHaltHalfwaythroughFileExpectations(IS_SAM);
		
		replay(visitor);
		sut.parse(visitor);
		verify(visitor);
	}
	
	@Test
	public void parseHalfBamFile() throws IOException, InvalidAttributeException{
		SamParser sut = SamParserFactory.create(resourceHelper.getFile("example.bam"));
		
		SamVisitor visitor = createMockVisitorWithHaltHalfwaythroughFileExpectations(IS_BAM);
		
		replay(visitor);
		sut.parse(visitor);
		verify(visitor);
	}
	

	private SamVisitor createMockVisitorWithHeaderOnlyExpectations() throws InvalidAttributeException {
		SamVisitor visitor = createMock(SamVisitor.class);
		
		String ref = "ref";
		
		SamHeader expectedHeader = new SamHeaderBuilder()
											.setVersion(new SamVersion(1, 5))
											.setSortOrder(SortOrder.COORDINATE)
											.addReferenceSequence(new SamReferenceSequenceBuilder(ref, 45).build())
											.build();
		
		visitor.visitHeader(isA(SamVisitorCallback.class), eq(expectedHeader));
		
		EasyMock.expectLastCall().andAnswer(HaltVisitor.INSTANCE);
		
		visitor.halted();
		return visitor;
		
	}
	private SamVisitor createMockVisitorWithEntireFileExpectations(boolean isBam)
			throws InvalidAttributeException {
		SamVisitor visitor = createMock(SamVisitor.class);
		
		String ref = "ref";
		
		SamHeader expectedHeader = new SamHeaderBuilder()
											.setVersion(new SamVersion(1, 5))
											.setSortOrder(SortOrder.COORDINATE)
											.addReferenceSequence(new SamReferenceSequenceBuilder(ref, 45).build())
											.build();
		
		visitor.visitHeader(isA(SamVisitorCallback.class), eq(expectedHeader));
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
								.setQueryName("r001")
								.setFlags(SamRecordFlag.parseFlags(163))
								.setReferenceName(ref)
								.setStartPosition(7)
								.setMappingQuality(30)
								.setCigar(Cigar.parse("8M2I4M1D3M"))
								.setNextReferenceName(ref)
								.setNextPosition(37)
								.setObservedTemplateLength(39)
								.setSequence(new NucleotideSequenceBuilder("TTAGATAAAGGATACTG").build())
								.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r002")
				.setFlags(SamRecordFlag.parseFlags(0))
				.setReferenceName(ref)
				.setStartPosition(9)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("3S6M1P1I4M"))
				.setSequence(new NucleotideSequenceBuilder("AAAAGATAAGGATA").build())
				.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r003")
				.setFlags(SamRecordFlag.parseFlags(0))
				.setReferenceName(ref)
				.setStartPosition(9)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("5S6M"))
				.setSequence(new NucleotideSequenceBuilder("GCCTAAGCTAA").build())
				.addAttribute(new SamAttribute(ReservedSamAttributeKeys.parseKey("SA"), "ref,29,-,6H5M,17,0;"))
				.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r004")
				.setFlags(SamRecordFlag.parseFlags(0))
				.setReferenceName(ref)
				.setStartPosition(16)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("6M14N5M"))
				.setSequence(new NucleotideSequenceBuilder("ATAGCTTCAGC").build())
				.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r003")
				.setFlags(SamRecordFlag.parseFlags(2064))
				.setReferenceName(ref)
				.setStartPosition(29)
				.setMappingQuality(17)
				.setCigar(Cigar.parse("6H5M"))
				.setSequence(new NucleotideSequenceBuilder("TAGGC").build())
				.addAttribute(new SamAttribute(ReservedSamAttributeKeys.parseKey("SA"), "ref,9,+,5S6M,30,1;"))
				.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r001")
				.setFlags(SamRecordFlag.parseFlags(83))
				.setReferenceName(ref)
				.setStartPosition(37)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("9M"))
				.setNextReferenceName(SamRecord.IDENTICAL)
				.setNextPosition(7)
				.setObservedTemplateLength(-39)
				.setSequence(new NucleotideSequenceBuilder("CAGCGGCAT").build())
				.addAttribute(new SamAttribute(ReservedSamAttributeKeys.parseKey("NM"), "1"))
				.build(), isBam);
		
		visitor.visitEnd();
		return visitor;
	}
	
	
	private SamVisitor createMockVisitorWithHaltHalfwaythroughFileExpectations(boolean isBam)
			throws InvalidAttributeException {
		SamVisitor visitor = createMock(SamVisitor.class);
		
		String ref = "ref";
		
		SamHeader expectedHeader = new SamHeaderBuilder()
											.setVersion(new SamVersion(1, 5))
											.setSortOrder(SortOrder.COORDINATE)
											.addReferenceSequence(new SamReferenceSequenceBuilder(ref, 45).build())
											.build();
		
		visitor.visitHeader(isA(SamVisitorCallback.class), eq(expectedHeader));
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
								.setQueryName("r001")
								.setFlags(SamRecordFlag.parseFlags(163))
								.setReferenceName(ref)
								.setStartPosition(7)
								.setMappingQuality(30)
								.setCigar(Cigar.parse("8M2I4M1D3M"))
								.setNextReferenceName(ref)
								.setNextPosition(37)
								.setObservedTemplateLength(39)
								.setSequence(new NucleotideSequenceBuilder("TTAGATAAAGGATACTG").build())
								.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r002")
				.setFlags(SamRecordFlag.parseFlags(0))
				.setReferenceName(ref)
				.setStartPosition(9)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("3S6M1P1I4M"))
				.setSequence(new NucleotideSequenceBuilder("AAAAGATAAGGATA").build())
				.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r003")
				.setFlags(SamRecordFlag.parseFlags(0))
				.setReferenceName(ref)
				.setStartPosition(9)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("5S6M"))
				.setSequence(new NucleotideSequenceBuilder("GCCTAAGCTAA").build())
				.addAttribute(new SamAttribute(ReservedSamAttributeKeys.parseKey("SA"), "ref,29,-,6H5M,17,0;"))
				.build(), isBam);
		
		visitRecord(visitor, createRecordBuilder(expectedHeader)
				.setQueryName("r004")
				.setFlags(SamRecordFlag.parseFlags(0))
				.setReferenceName(ref)
				.setStartPosition(16)
				.setMappingQuality(30)
				.setCigar(Cigar.parse("6M14N5M"))
				.setSequence(new NucleotideSequenceBuilder("ATAGCTTCAGC").build())
				.build(), isBam);
		
		EasyMock.expectLastCall().andAnswer(HaltVisitor.INSTANCE);
		
		visitor.halted();
		
		
		return visitor;
	}
	
	private SamRecordBuilder createRecordBuilder(SamHeader header){
		return new SamRecordBuilder(header, ReservedAttributeValidator.INSTANCE);
	}
	
	private void visitRecord(SamVisitor visitor, SamRecord record, boolean isBam){
		if(isBam){
			visitor.visitRecord(isA(SamVisitorCallback.class), eq(record), isA(VirtualFileOffset.class), isA(VirtualFileOffset.class));
		}else{
			visitor.visitRecord(isA(SamVisitorCallback.class), eq(record), isNull(), isNull());
		}
	}
}
