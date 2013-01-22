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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ace;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;

public abstract class TestAbstractAceParserMatchesAce2ContigMultipleContigs extends AbstractTestAceParserMatchesAce2Contig{
    private static final String ACE_FILE = "files/fluSample.ace";
    private static final String CONTIG_FILE = "files/fluSample.contig";
    public TestAbstractAceParserMatchesAce2ContigMultipleContigs() throws IOException, DataStoreException {
        super(ACE_FILE,CONTIG_FILE);
       
    }

}
