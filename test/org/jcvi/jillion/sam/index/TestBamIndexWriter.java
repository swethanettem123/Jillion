package org.jcvi.jillion.sam.index;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SamWriterBuilder;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestBamIndexWriter {
	ResourceHelper resources = new ResourceHelper(TestBamIndexWriter.class);
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private File bamFile, expectedBaiFile;
	
	@Before
	public void setup() throws IOException{
		bamFile = resources.getFile("index_test.bam");
		expectedBaiFile = resources.getFile("index_test.bam.bai");
	}
	
	@Test(expected = NullPointerException.class)
	public void nullInputFileShouldThrowNPE() throws IOException{
		
		new BamIndexFileWriterBuilder(null, new File("a.bai"));
	}
	@Test(expected = NullPointerException.class)
	public void nullOutputFileShouldThrowNPE() throws IOException{
		
		new BamIndexFileWriterBuilder(new File("a.bam"), null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void wrongExtensionForOutputFileShouldThrowException() throws IOException{
		
		new BamIndexFileWriterBuilder(new File("a.bam"), new File("notABai"));
	}
	@Test(expected = IllegalArgumentException.class)
	public void wrongExtensionForInputFileShouldThrowException() throws IOException{
		
		new BamIndexFileWriterBuilder(new File("notABam"), new File("a.bai"));
	}
	
	@Test(expected = FileNotFoundException.class)
	public void inputFileDoesNotExistShouldThrowFileNotFoundException() throws IOException{
		File nonExistentFile = new File(tmpDir.getRoot(), "dne.bam");
		new BamIndexFileWriterBuilder(nonExistentFile, new File("a.bai"));
	}
	
	@Test
	public void writterWithMetaDataMatchesByteForByteToPicard() throws IOException{
		
		
		File actualBaiFile = tmpDir.newFile("actual.bai");
		
		new BamIndexFileWriterBuilder(bamFile, actualBaiFile)
					.includeMetaData(true)
					.build();
		
		assertTrue(TestUtil.contentsAreEqual(expectedBaiFile, actualBaiFile));
	}
	
	@Test
	public void writingNonCoordinateSortedBamShouldThrowException() throws IOException{

		SamHeader originalHeader = parseSamHeaderFrom(bamFile);
		
		File incorrectlySortedFile = tmpDir.newFile("wrongSort.bam");
		File actualBaiFile = tmpDir.newFile("actual.bai");
		for(SortOrder incorrectSortOrder : Arrays.asList(SortOrder.QUERY_NAME, SortOrder.UNKNOWN, SortOrder.UNSORTED)){
			SamWriter writer = new SamWriterBuilder(incorrectlySortedFile, originalHeader)
														.forceHeaderSortOrder(incorrectSortOrder)
														.build();
			
			writeAllRecords(bamFile, writer);
			verifyIndexWriterThrowsException(incorrectlySortedFile,	actualBaiFile, incorrectSortOrder);
		}
	}
	@Test
	public void assumeSortedFlagSetWillWriteIndexEvenIfHeaderSaysOtherwise() throws IOException{

		SamHeader originalHeader = parseSamHeaderFrom(bamFile);
		
		File incorrectlySortedFile = tmpDir.newFile("wrongSort.bam");

		//the input bam file is from an old version of picard
		//which didn't compress as well (at all?)
		//so since we are writing out bams using compression,
		//also the different header sort order cause the header
		//length to be different so we have to compensate for that as well.
		//(see createHeader() method for more details)
		//Therefore, we need to write out a new file so that our
		//indexes will match.		
		File newBam = reWriteBam(bamFile, createHeader(originalHeader, SortOrder.COORDINATE));
		File expectedBai = createIndex(newBam);
		for(SortOrder incorrectSortOrder : Arrays.asList(SortOrder.QUERY_NAME, SortOrder.UNKNOWN, SortOrder.UNSORTED)){
			SamWriter writer = new SamWriterBuilder(incorrectlySortedFile, createHeader(originalHeader, incorrectSortOrder))
														.forceHeaderSortOrder(incorrectSortOrder)
														.build();
			
			writeAllRecords(newBam, writer);
			File actualBai = createIndex(incorrectlySortedFile, incorrectSortOrder);
			assertTrue(TestUtil.contentsAreEqual(expectedBai, actualBai));
		}
	}

	private SamHeader createHeader(SamHeader originalHeader, SortOrder order){
		SamHeader.Builder builder = new SamHeader.Builder(originalHeader);
		//because the index uses byte offsets,
		//the different sort orders will cause the header to be different
		//byte lengths which will throw off the index offsets by a few bytes.
		//To get around this, we will add a comment String to the header
		//that is a variable amount depending on the sort order so that
		//all the records start at the same byte offset.
		//since coordinate sort is the largest word, we will padd compared to that.
		if(order ==SortOrder.COORDINATE){
			builder.addComment("");
		}else{
			int padding = SortOrder.COORDINATE.getEncodedName().length() -order.getEncodedName().length();
			StringBuilder paddedString = new StringBuilder(padding);
			for(int i=0; i<padding; i++){
				paddedString.append("*");
			}
			builder.addComment(paddedString.toString());
		}
		return builder.build();
		
	}
	
	
	private File reWriteBam(File bamFile, SamHeader header) throws IOException{
		File newBam = tmpDir.newFile(bamFile.getName());
		
		SamWriter writer = new SamWriterBuilder(newBam, header)
								.build();

		writeAllRecords(bamFile, writer);
		return newBam;
		
	}
	
	
	private File createIndex(File inputBam) throws IOException{
		return createIndex(inputBam, null);
	}
	private File createIndex(File inputBam, SortOrder order) throws IOException{
		StringBuilder baiName = new StringBuilder(inputBam.getName());
		if(order!=null){
			baiName.append(".").append(order);
		}
		baiName.append(".bai");
		
		File outputBai = tmpDir.newFile(baiName.toString());
		new BamIndexFileWriterBuilder(inputBam, outputBai)
						.assumeSorted(true)
						.includeMetaData(true)
						.build();
		
		return outputBai;
	}

	private void verifyIndexWriterThrowsException(File incorrectlySortedFile,
			File actualBaiFile, SortOrder sortOrder) throws IOException {
		try{
			new BamIndexFileWriterBuilder(incorrectlySortedFile, actualBaiFile)
					.build();
			fail("should throw IllegalStateException when " + sortOrder);
		}catch(IllegalStateException expected){
			//expected
		}
	}
	
	private void writeAllRecords(File bamFile, final SamWriter writer) throws IOException {
		try{
			SamParserFactory.create(bamFile)
			.accept(new SamVisitor() {
				
				@Override
				public void visitRecord(SamVisitorCallback callback, SamRecord record,
						VirtualFileOffset start, VirtualFileOffset end) {
					try {
						writer.writeRecord(record);
					} catch (IOException e) {
						throw new IllegalStateException("error writing out record",e);
					}
					
				}
				
				@Override
				public void visitRecord(SamVisitorCallback callback, SamRecord record) {
					try {
						writer.writeRecord(record);
					} catch (IOException e) {
						throw new IllegalStateException("error writing out record",e);
					}
				}
				
				@Override
				public void visitHeader(SamVisitorCallback callback, SamHeader header) {
					//no-op
				}
				
				@Override
				public void visitEnd() {
					//no-op
				}
				
				@Override
				public void halted() {
					//no-op
				}
			});
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}
		
	}

	private SamHeader parseSamHeaderFrom(File samOrBam) throws IOException{
		//create final array so we can reference it in our 
		//anonymous class
		final SamHeader[] singleHeaderBuilder = new SamHeader[1];
		SamParserFactory.create(samOrBam)
						.accept(new SamVisitor() {
							
							@Override
							public void visitRecord(SamVisitorCallback callback, SamRecord record,
									VirtualFileOffset start, VirtualFileOffset end) {
								//no-op								
							}
							
							@Override
							public void visitRecord(SamVisitorCallback callback, SamRecord record) {
								//no-op
							}
							
							@Override
							public void visitHeader(SamVisitorCallback callback, SamHeader header) {
								singleHeaderBuilder[0] = header;
								callback.haltParsing();
							}
							
							@Override
							public void visitEnd() {
								//no-op
							}
							
							@Override
							public void halted() {
								//no-op
							}
						});
		
		return singleHeaderBuilder[0];
	}
}