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
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.archive;

import java.io.IOException;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.Caches;
/**
 * {@code CachedTraceArchiveDataStore} is a {@link TraceArchiveDataStore}
 * implementation which cache's {@link TraceArchiveTrace} records
 * in an LRUCache.
 * @author dkatzel
 *
 *
 */
public class CachedTraceArchiveDataStore implements TraceArchiveDataStore<TraceArchiveTrace>{

    private final Map<String, TraceArchiveTrace> lruCahce;
    private final TraceArchiveDataStore<TraceArchiveTrace> traceArchiveDataStore;
    /**
     * Create a new CachedTraceArchiveDataStore wrapping the given
     * TraceArchiveDataStore with an LRUCache of the given size.
     * @param traceArchiveDataStore the TraceArchiveDataStore to cache (can not be null).
     * @param cacheSize the size of the LRUCache to use.
     */
    public CachedTraceArchiveDataStore(TraceArchiveDataStore<TraceArchiveTrace> traceArchiveDataStore, int cacheSize) {
        if(traceArchiveDataStore ==null){
            throw new NullPointerException("traceArchiveDataStore can not be null");
        }
        this.traceArchiveDataStore = traceArchiveDataStore;
        lruCahce = Caches.createLRUCache(cacheSize);
    }

    @Override
    public TraceArchiveTrace get(String id) throws DataStoreException {
        if(lruCahce.containsKey(id)){
            return lruCahce.get(id);
        }
        TraceArchiveTrace trace = traceArchiveDataStore.get(id);
        lruCahce.put(id, trace);
        return trace;
    }

    @Override
    public void close() throws IOException {
        lruCahce.clear();
        traceArchiveDataStore.close();
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return traceArchiveDataStore.contains(id);
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return traceArchiveDataStore.getNumberOfRecords();
    }

    @Override
    public StreamingIterator<TraceArchiveTrace> iterator() throws DataStoreException {
        return traceArchiveDataStore.iterator();
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return traceArchiveDataStore.idIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return traceArchiveDataStore.isClosed();
    }

}
