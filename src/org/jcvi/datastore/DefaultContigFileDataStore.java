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
/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.AbstractContigFileDataStore;
import org.jcvi.assembly.contig.DefaultContigFileParser;

public class DefaultContigFileDataStore extends AbstractContigFileDataStore implements ContigDataStore<PlacedRead, Contig<PlacedRead>>{
    private final Map<String,Contig<PlacedRead>> contigs;

    private boolean isClosed = false;
    
    /**
     * Construct an empty uninitialized Contig File DataStore.
     */
    public DefaultContigFileDataStore(){
        contigs = new HashMap<String, Contig<PlacedRead>>();
    }

    /**
     * Construct a ContigFileDataStore containing the contig
     * data from the given contig file.
     * @param contigFile the contig file containing the desired contig data.
     * @throws FileNotFoundException if the given contig file does not exist.
     */
    public DefaultContigFileDataStore(File contigFile) throws FileNotFoundException{
        this(new FileInputStream(contigFile));
    }
    /**
     * Construct a ContigFileDataStore containing the contig
     * data from the given {@link InputStream} of a contig file.
     * @param inputStream an {@link InputStream} of contig file data.
     */
    public DefaultContigFileDataStore(InputStream inputStream) {
        this();
        DefaultContigFileParser.parse(inputStream, this);
    }
    @Override
    protected void addContig(Contig contig) {
        contigs.put(contig.getId(), contig);
        
    }

    @Override
    public boolean contains(String contigId)throws DataStoreException {
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
        return contigs.containsKey(contigId);
    }
    
    private void throwExceptionIfNotInitialized() throws DataStoreException {
        if(!isInitialized()){
            throw new DataStoreException("DataStore not yet initialized");
        }
    }
    
    
    private void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }

    
    @Override
    public Contig<PlacedRead> get(String contigId)
            throws DataStoreException {
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
        return contigs.get(contigId);
    }

    @Override
    public void close() throws IOException {        
        isClosed = true;
        contigs.clear();
    }


    @Override
    public Iterator<String> getIds() {
        final List<String> sortedIds = new ArrayList<String>(contigs.keySet());
        Collections.sort(sortedIds);
        return sortedIds.iterator();
    }


    @Override
    public int size() {
        return contigs.size();
    }


    @Override
    public Iterator<Contig<PlacedRead>> iterator() {
        return new DataStoreIterator<Contig<PlacedRead>>(this);
    }
}
