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
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class DefaultNucleotideSffDataStore implements NucleotideDataStore{

    private final SffDataStore flowgramDataStore;
    private final boolean trim;
    /**
     * Create a {@link NucleotideDataStore} using the 
     * basecalls from the given {@link SffDataStore}.
     *
     * @param flowgramDataStore the {@link SffDataStore} to
     * wrap.
     * @param trim should the basecalls be trimmed as specified
     * by the flowgram.
     */
    public DefaultNucleotideSffDataStore(
            SffDataStore flowgramDataStore, boolean trim) {
        this.flowgramDataStore = flowgramDataStore;
        this.trim = trim;
    }
    /**
     * Create a {@link NucleotideDataStore} using the 
     * basecalls from the given {@link SffDataStore}.
     * This is the same as {@link #DefaultNucleotideSffDataStore(SffDataStore, boolean)
     * new DefaultNucleotideSffDataStore( flowgramDataStore,false)}
     *
     * @param flowgramDataStore the {@link SffDataStore} to
     * wrap.
     * @see #DefaultNucleotideSffDataStore(SffDataStore, boolean)
     */
    public DefaultNucleotideSffDataStore(
            SffDataStore flowgramDataStore) {
        this(flowgramDataStore, false);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return flowgramDataStore.contains(id);
    }

    @Override
    public NucleotideSequence get(String id) throws DataStoreException {
        final Flowgram flowgram = flowgramDataStore.get(id);
        NucleotideSequence fullRange= flowgram.getBasecalls();
        if(trim){
           
            return new DefaultNucleotideSequence(
                    fullRange.asList(SFFUtil.getTrimRangeFor(flowgram)));
        }
        return fullRange;
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return flowgramDataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return flowgramDataStore.size();
    }

    @Override
    public void close() throws IOException {
        flowgramDataStore.close();
        
    }

    @Override
    public CloseableIterator<NucleotideSequence> iterator() {
        return new DataStoreIterator<NucleotideSequence>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return flowgramDataStore.isClosed();
    }

}
