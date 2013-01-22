/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AbstractAceFileVisitorContigBuilder;
import org.jcvi.jillion.assembly.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.ace.AceFileParser;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public class TestParseInvalidAceNotAnAceFile {

	@Test(expected = IOException.class)
	public void tryingToParseNonAceFileShouldThrowIOException() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestParseInvalidAceNotAnAceFile.class);
		File nonAce = resources.getFile("files/sample.contig");
		AceFileParser.parse(nonAce, new AbstractAceFileVisitorContigBuilder() {
			
			@Override
			protected void visitContig(AceContigBuilder contig) {
				throw new IllegalStateException("should not get this far");			
			}
		});
		
		
	}
	
	
}
