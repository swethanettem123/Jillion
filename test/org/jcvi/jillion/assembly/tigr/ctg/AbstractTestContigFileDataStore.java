/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.tigr.ctg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.junit.After;
import org.junit.Test;
public abstract class AbstractTestContigFileDataStore<R extends AssembledRead, C extends Contig<R>, D extends DataStore<C>> extends TestAbstractContigFileParser{

	protected final NucleotideFastaDataStore fullLengthSequences;
	private D dataStore;
	
	public AbstractTestContigFileDataStore() throws FileNotFoundException, IOException{
		fullLengthSequences = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("files/gcv_23918.raw.seq.fasta.fasta")).build();
		dataStore = buildContigFileDataStore(fullLengthSequences, getFile() );
	}
	@After
	public void closeDataStores(){
		IOUtil.closeAndIgnoreErrors(dataStore, fullLengthSequences);
	}
    @Test
    public void thereAre4Contigs() throws DataStoreException, IOException{
        assertEquals(4, dataStore.getNumberOfRecords());
    }
    @Override
    protected C getContig925From(File file) throws FileNotFoundException {
        return getContig(dataStore, "925");
    }
    @Override
    protected C getContig928From(File file) throws Exception{
        return getContig(dataStore, "928");
    }
    private C getContig(
            D dataStore, String id) {
        try {
            return dataStore.get(id);
        } catch (DataStoreException e) {
            e.printStackTrace();
            throw new RuntimeException("error getting contig "+id,e);
        }
    }
    protected abstract D buildContigFileDataStore(
    		NucleotideFastaDataStore fullLengthSequences, File contigFile) throws IOException;
    

    @Test
    public void idIterator() throws DataStoreException{
    	StreamingIterator<String> iter =dataStore.idIterator();
    	Iterator<String> expected = Arrays.asList("925","926","927","928").iterator();
    	while(expected.hasNext()){
    		assertTrue(iter.hasNext());
    		assertEquals(expected.next(), iter.next());
    	}
    	assertFalse(iter.hasNext());
    }
    @Test
    public void iterator() throws DataStoreException{
    	StreamingIterator<C> iter =dataStore.iterator();
    	List<String> expectedIds = Arrays.asList("925","926","927","928");
    	List<String> actualIds = new ArrayList<String>();
    	
    	while(iter.hasNext()){
    		C contig = iter.next();
    		actualIds.add(contig.getId());
    		if("925".equals(contig.getId())){
    			assertContig925Matches(contig);
    		}else if("928".equals(contig.getId())){
    			assertContig928Correct(contig);
    		}
    		
    	}
    	assertEquals(expectedIds, actualIds);
    }

}
