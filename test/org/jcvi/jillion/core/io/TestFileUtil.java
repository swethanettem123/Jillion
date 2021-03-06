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
package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFileUtil {
	File root = new File("tests/org/jcvi/io");
	File higherFile = new File("tests/org/jcvi");
	File lowerFile = new File("tests/org/jcvi/io/fileServer/files/README.txt");
	File siblingFile = new File("tests/org/jcvi/fasta");
	
	@Test
	public void getExtensionFromFile(){
		assertEquals("txt", FileUtil.getExtension(lowerFile));
	}
	@Test
	public void getExtensionFromString(){
		assertEquals("txt", FileUtil.getExtension("README.txt"));
	}
	@Test
	public void getExtensionFromStringFullPath(){
		assertEquals("txt", FileUtil.getExtension("tests/org/jcvi/io/fileServer/files/README.txt"));
	}
	@Test
	public void getExtensionFromStringWithNoExtensionShouldReturnEmptyString(){
		assertTrue(FileUtil.getExtension("filename").isEmpty());
	}
	@Test
	public void getExtensionFromFileWithNoExtensionShouldReturnEmptyString(){
		assertTrue(FileUtil.getExtension(root).isEmpty());
	}
	
	@Test
	public void getBaseNameFromFile(){
		assertEquals("README", FileUtil.getBaseName(lowerFile));
	}
	@Test
	public void getBaseNameFromString(){
		assertEquals("README", FileUtil.getBaseName("README.txt"));
	}
	@Test
	public void getBaseNameFromStringFullPath(){
		assertEquals("README", FileUtil.getBaseName("tests/org/jcvi/io/fileServer/files/README.txt"));
	}
	@Test
	public void getBaseNameFromStringWithNoExtension(){
		assertEquals("filename",FileUtil.getBaseName("filename"));
	}
	@Test
	public void getBaseNameFromFileWithNoExtensionShouldReturnEmptyString(){
		assertEquals("io",FileUtil.getBaseName(root));
	}
	@Test
	public void relativePathSameFileShouldReturnEmptyString() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, root, '/');
		assertEquals("", actual);
	}
	@Test
	public void relativePathSiblingFile() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, siblingFile, '/');
		assertEquals("../fasta", actual);
	}
	
	@Test
	public void relativePathToHigherFile() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, higherFile, '/');
		assertEquals("..", actual);
	}
	
	@Test
	public void relativePathToLowerFile() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, lowerFile, '/');
		assertEquals("fileServer/files/README.txt", actual);
	}
}
