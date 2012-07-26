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

package org.jcvi.common.core.seq.trim.lucy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.common.core.assembly.util.trim.TrimDataStoreAdatper;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code LucyTrimDataStore} is a TrimDataStore
 * that parses a lucy
 * seq fasta file and gets the trimpoints
 * from the fasta comments.
 * @author dkatzel
 *
 *
 */
public class LucySeqTrimDataStore implements TrimPointsDataStore {

    private final TrimPointsDataStore datastore;
    /**
     * 
     * @param lucySeqFile
     * @throws FileNotFoundException
     */
    public LucySeqTrimDataStore(File lucySeqFile) throws FileNotFoundException{
        final Map<String, Range> map = new LinkedHashMap<String, Range>();
        //our fasta visitor implementation
        //to parse the trim points from the comments
        FastaFileVisitor visitor = new AbstractFastaVisitor() {
            
            @Override
            public boolean visitRecord(String id, String comment, String entireBody) {
                //ex def line 
                //>name CLZ CLZ CLR CLR
                String[] trimpoints = comment.split("\\s+");
                Range range = Range.create(CoordinateSystem.RESIDUE_BASED, 
                        Long.parseLong(trimpoints[3]),
                        Long.parseLong(trimpoints[4])
                        );
                map.put(id, range);
                return true;
            }
        };
        FastaFileParser.parse(lucySeqFile, visitor);
        datastore = TrimDataStoreAdatper.adapt(MapDataStoreAdapter.adapt(map));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return datastore.idIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Range get(String id) throws DataStoreException {
        return datastore.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return datastore.getNumberOfRecords();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws DataStoreException {
        return datastore.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
       datastore.close();

    }

    /**
     * {@inheritDoc}
     * @throws DataStoreException 
     */
    @Override
    public StreamingIterator<Range> iterator() throws DataStoreException {
        return datastore.iterator();
    }

}
