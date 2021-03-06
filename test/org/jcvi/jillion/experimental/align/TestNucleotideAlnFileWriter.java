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

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestNucleotideAlnFileWriter {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private File out;
	private NucleotideSequenceDataStore datastore;
	@Before
	public void setup() throws IOException{
		out = tmpDir.newFile();
		File alnIn =new ResourceHelper(TestNucleotideAlnFileWriter.class).getFile("files/example.aln");
	
		datastore = GappedNucleotideAlignmentDataStore.createFromAlnFile(alnIn);
	}
	
	@Test
	public void writeDefaultNumberOfResidues() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																	.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}
	@Test
	public void writeCumulativeResidueCounts() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.includeCumulativeCounts(true)
																		.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
		//confirm lines end in numbers
		Scanner scanner = null;
		try{
			scanner= new Scanner(out);
			Pattern linePattern = Pattern.compile("gi\\S+\\s+(\\S+)\\s(\\d+)");
			int cumulativeLength=0;
			int currentGroupLength=0;
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(line.contains("**")){
					//nextGroup
					cumulativeLength+=currentGroupLength;
				}
				Matcher matcher = linePattern.matcher(line);
				if(matcher.find()){
					int actualLength = matcher.group(1).length();
					int expectedLength = Integer.parseInt(matcher.group(2));
					assertEquals("cumulative count wrong for " + line, cumulativeLength+actualLength, expectedLength);
					//all lengths in the group should be the same
					currentGroupLength = actualLength;
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(scanner);
		}
	}
	
	@Test
	public void differentEOL() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.eol("\r\n")
																			.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}
	
	@Test
	public void differentNumberOfGroups() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.setNumResiduesPerGroup(10)
																			.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}
	
	@Test
	public void forceOnly1Group() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.forceOneGroupOnly()
																			.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}

	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE() throws IOException{
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																.build();
		try{
			writer.write(null, new NucleotideSequenceBuilder("ACGT").build());
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	@Test(expected = IllegalArgumentException.class)
	public void nullSequenceShouldThrowNPE() throws IOException{
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																.build();
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		try{
			writer.write("id", seq);
			writer.write("id", seq);
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	
	@Test(expected = NullPointerException.class)
	public void writingSameIdTwiceShouldThrowException() throws IOException{
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																.build();
		
		try{
			writer.write("id", null);
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	
	
	private void writeAndAssertDataWrittenCorrectly(
			AlnFileWriter<Nucleotide, NucleotideSequence> writer)
			throws DataStoreException, IOException {
		StreamingIterator<String> idIter = datastore.idIterator();
		try{
			while(idIter.hasNext()){
				String id =idIter.next();
				writer.write(id,  datastore.get(id));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(idIter, writer);
		}
		NucleotideSequenceDataStore actual = GappedNucleotideAlignmentDataStore.createFromAlnFile(out);
		assertEquals(datastore.getNumberOfRecords(), actual.getNumberOfRecords());
		StreamingIterator<String> iter = datastore.idIterator();
		try{
			while(iter.hasNext()){
				String id = iter.next();
				assertEquals(datastore.get(id), actual.get(id));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, actual);
		}
	}

}
