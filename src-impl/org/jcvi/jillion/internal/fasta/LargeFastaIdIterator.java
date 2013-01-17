/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.jillion.internal.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaFileParser2;
import org.jcvi.jillion.fasta.FastaFileVisitor2;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class LargeFastaIdIterator extends AbstractBlockingStreamingIterator<String>{

    private final File fastaFile;
    private final DataStoreFilter filter;
    public static LargeFastaIdIterator createNewIteratorFor(File fastaFile, DataStoreFilter filter){
    	if(fastaFile ==null){
    		throw new NullPointerException("fasta file can not be null");
    	}
    	if(filter ==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	LargeFastaIdIterator iter= new LargeFastaIdIterator(fastaFile,filter);
		iter.start();
    	
    	return iter;
    }
    public static LargeFastaIdIterator createNewIteratorFor(File fastaFile){
    	return createNewIteratorFor(fastaFile, DataStoreFilters.alwaysAccept());
    }
	
    /**
     * @param fastaFile
     */
    private LargeFastaIdIterator(File fastaFile, DataStoreFilter filter) {
    	
        this.fastaFile = fastaFile;
        this.filter = filter;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
    	FastaFileVisitor2 visitor = new FastaFileVisitor2() {

			@Override
			public FastaRecordVisitor visitDefline(
					FastaVisitorCallback callback, String id,
					String optionalComment) {
				if(filter.accept(id)){
    				LargeFastaIdIterator.this.blockingPut(id);
    			}
				return null;
			}

			@Override
			public void visitEnd() {
				//no-op
				
			}
        };
        try {
        	new FastaFileParser2(fastaFile).accept(visitor);
        } catch (IOException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
