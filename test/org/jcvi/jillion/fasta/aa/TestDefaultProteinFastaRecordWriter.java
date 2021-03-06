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
package org.jcvi.jillion.fasta.aa;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.junit.Test;
public class TestDefaultProteinFastaRecordWriter {
	private final ProteinFastaRecord record1 = new ProteinFastaRecordBuilder("id_1", "CVGITPSA")
																.comment("a comment")
																.build();
	private final ProteinFastaRecord record2 = new ProteinFastaRecordBuilder("id_2", "CVGITPSAKASILHEV").build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new ProteinFastaWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws IOException{
		new ProteinFastaWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ProteinFastaWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ProteinFastaWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGITPSA\n"+
							">id_2\n"+
							"CVGITPSAKASILHEV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void multiLineFastas() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
								.numberPerLine(5)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGIT\nPSA\n"+
							">id_2\n"+
							"CVGIT\nPSAKA\nSILHE\nV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void nullEOLShouldUseDefault() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
								.numberPerLine(5)	
								.lineSeparator(null)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGIT\nPSA\n"+
							">id_2\n"+
							"CVGIT\nPSAKA\nSILHE\nV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void allOnOneLine() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
								.allBasesOnOneLine()
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		
		char[] seq = new char[1000];
		Arrays.fill(seq, 'P');
		
		ProteinFastaRecord longRecord = new ProteinFastaRecordBuilder("long", new ProteinSequenceBuilder(new String(seq)).build()).build();
		sut.write(longRecord);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGITPSA\n"+
							">id_2\n"+
							"CVGITPSAKASILHEV\n"+
							">long\n"+
							new String(seq)+"\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void differentEOL() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
								.numberPerLine(5)	
								.lineSeparator("\r\n")
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\r\n"+
							"CVGIT\r\nPSA\r\n"+
							">id_2\r\n"+
							"CVGIT\r\nPSAKA\r\nSILHE\r\nV\r\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
								.numberPerLine(4)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGI\nTPSA\n"+
							">id_2\n"+
							"CVGI\nTPSA\nKASI\nLHEV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void differentCharSet() throws IOException{
		Charset charSet = Charset.forName("UTF-16");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ProteinFastaWriter sut = new ProteinFastaWriterBuilder(out)
								.numberPerLine(5)	
								.charset(charSet)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
						"CVGIT\nPSA\n"+
						">id_2\n"+
						"CVGIT\nPSAKA\nSILHE\nV\n";
		byte[] expectedBytes = expected.getBytes(charSet);
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
}
