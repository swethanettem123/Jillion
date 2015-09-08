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
package org.jcvi.jillion.internal.fasta;

import java.io.IOException;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

public abstract class AbstractLargeFastaFileDataStore<T,S extends Sequence<T>, F extends FastaRecord<T, S>> implements FastaDataStore<T,S,F>{

    private final FastaParser parser;
    private final DataStoreFilter filter;
    private Long size;
    private volatile boolean closed=false;
    
    /**
     * Construct a {@link AbstractLargeFastaFileDataStore} using
     * the given fasta file and filter.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
    protected AbstractLargeFastaFileDataStore(FastaParser parser, DataStoreFilter filter) {
        if(parser ==null){
            throw new NullPointerException("fasta parser can not be null");
        }
        if(filter ==null){
            throw new NullPointerException("filter file can not be null");
        }
        this.filter =filter;
        this.parser = parser;
    }
    
    private void checkNotYetClosed(){
        if(closed){
            throw new DataStoreClosedException("already closed");
        }
    }


	@Override
    public  void close() throws IOException {
        closed=true;
        
    }

    @Override
    public  boolean isClosed() {
        return closed;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        return get(id)!=null;
    }

    @Override
    public F get(String id)
            throws DataStoreException {
        StreamingIterator<F> iter = iterator();
        try{
	        while(iter.hasNext()){
	        	F next = iter.next();
	        	if(next.getId().equals(id)){
	        		return next;
	        	}
	        }
	        //we get here if we didn't find it
	        return null;
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        checkNotYetClosed();
        return DataStoreStreamingIterator.create(this,LargeFastaIdIterator.createNewIteratorFor(parser,filter));
        
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
            	FastaVisitor visitor = new FastaVisitor(){
        			long numDeflines=0L;
        			@Override
        			public FastaRecordVisitor visitDefline(
        					FastaVisitorCallback callback, String id,
        					String optionalComment) {
        				if(filter.accept(id)){
        					numDeflines++;
        				}
        				//always skip since we only count deflines
        				return null;
        			}

        			@Override
        			public void visitEnd() {
        				//no-op
        				size = Long.valueOf(numDeflines);
        			}
        			@Override
        			public void halted() {
        				//this shouldn't happen
        				//throw an exception so we don't
        				//return a null value or try 
        				//to reparse the size 
        				//next time it's asked 
        				throw new IllegalStateException("parser was halted when trying to compute size");		
        			}
        		};      
        		parser.parse(visitor);
            } catch (IOException e) {
                throw new IllegalStateException("could not get record count",e);
            }
        }   
        return size;

    }
	

    @Override
    public final StreamingIterator<F> iterator() throws DataStoreException {
        checkNotYetClosed();
        return createNewIterator(parser,filter);
       
    }

	protected abstract StreamingIterator<F> createNewIterator(FastaParser parser ,DataStoreFilter filter) throws DataStoreException;
   

	@Override
	public final StreamingIterator<DataStoreEntry<F>> entryIterator()
			throws DataStoreException {
		checkNotYetClosed();
		return new StreamingIterator<DataStoreEntry<F>>(){
			StreamingIterator<F> iter = iterator();
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public void close() {
				iter.close();
			}

			@Override
			public DataStoreEntry<F> next() {
				F next = iter.next();
				return new DataStoreEntry<F>(next.getId(), next);
			}

			@Override
			public void remove() {
				iter.remove();
			}
			
		};
	}

   
}
