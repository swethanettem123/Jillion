/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.examples.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.sam.index.BamIndexFileWriterBuilder;

public class CreateBamIndexFromBam {

	public static void main(String[] args) throws IOException {
		File bamFile = new File("/local/netapp_scratch/dkatzel/bamIndexExample/picard.index_test.bam");
		File baiFile = new File("/local/netapp_scratch/dkatzel/bamIndexExample/jillion.index_test.bam.withMetadata.bai");
		
		new BamIndexFileWriterBuilder(bamFile, baiFile)
				.includeMetaData(true) //includes metadata that Picard and samtools use
				.build();
				

	}

}