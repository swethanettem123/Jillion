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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.read.trace.Trace;
import org.jcvi.common.core.seq.read.trace.TraceNucleotideDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.TraceQualityDataStoreAdapter;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code ArtificialPhdDataStore} is a {@link DataStore} of
 * Nucleotide and PhredQuality data that has been adapted to 
 * match the {@link Phd} interface.
 * @author dkatzel
 *
 *
 */
public class ArtificalPhdDataStore extends AbstractDataStore<Phd> implements PhdDataStore{
    private final DataStore<NucleotideSequence> seqDataStore;
    private final DataStore<QualitySequence> qualDataStore;
    private final Properties comments = new Properties();
    
    /**
     * Create a new PhdDataStore from a DataStore of {@link Trace}
     * objects.  The Trace's nucleotide and quality sequences
     * will be used in the Phd objects but trace positions will be artificially
     * created. 
     * @param <T>  the Trace type.
     * @param traceDataStore the Datastore of traces to wrap.
     * @param phdDate the date for the phd records to be included
     * in a comment in the phd records (required for consed).
     * @return a new {@link PhdDataStore} object;
     * never null.
     */
    public static <T extends Trace>  PhdDataStore createFromTraceDataStore(DataStore<T> traceDataStore, Date phdDate){
        return new ArtificalPhdDataStore(TraceNucleotideDataStoreAdapter.adapt(traceDataStore), 
                TraceQualityDataStoreAdapter.adapt(traceDataStore), phdDate);
    }
    /**
     * @param seqDataStore
     * @param qualDataStore
     * @param phdDate
     */
    public ArtificalPhdDataStore(DataStore<NucleotideSequence> seqDataStore,
            DataStore<QualitySequence> qualDataStore, Date phdDate) {
        this.seqDataStore = seqDataStore;
        this.qualDataStore = qualDataStore;
        comments.putAll(PhdUtil.createPhdTimeStampCommentFor(phdDate));
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return seqDataStore.contains(id);
    }

    @Override
    public synchronized Phd get(String id) throws DataStoreException {
        super.get(id);
       final NucleotideSequence basecalls = seqDataStore.get(id);
       if(basecalls ==null){
           throw new NullPointerException("could not find basecalls for "+id);
       }
    final QualitySequence qualities = qualDataStore.get(id);
    if(qualities ==null){
        throw new NullPointerException("could not find qualities for "+id);
    }
    return ArtificialPhd.createNewbler454Phd(id,
    			basecalls, 
                qualities,
                comments,Collections.<PhdTag>emptyList());
    }

    @Override
    public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
        super.idIterator();
        return seqDataStore.idIterator();
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        super.getNumberOfRecords();
        return seqDataStore.getNumberOfRecords();
    }

	@Override
	protected void handleClose() throws IOException {
		 seqDataStore.close();
	        qualDataStore.close();
	        comments.clear();
		
	}

    
}
