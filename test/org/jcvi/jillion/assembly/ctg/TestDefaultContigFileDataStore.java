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
/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ContigDataStore;
import org.jcvi.jillion.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;

public class TestDefaultContigFileDataStore extends AbstractTestContigFileDataStore{
   
    public TestDefaultContigFileDataStore() throws FileNotFoundException,
			IOException {
		super();
	}

	@Override
    protected ContigDataStore<AssembledRead, Contig<AssembledRead>> buildContigFileDataStore(
    		NucleotideSequenceFastaDataStore fullLengthSequences, File file) throws FileNotFoundException {
        return DefaultContigFileDataStore.create(fullLengthSequences, file);
    }
}
