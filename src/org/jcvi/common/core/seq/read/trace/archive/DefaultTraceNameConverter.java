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
 * Created on Sep 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.IdConverter;
import org.jcvi.common.core.datastore.DataStoreException;
/**
 * {@code DefaultTraceNameConverter} maps 
 * a TraceArchiveInfo's trace_name to id
 * generated by the given {@link TraceArchiveRecordIdGenerator}.
 * @author dkatzel
 *
 *
 */
public class DefaultTraceNameConverter implements IdConverter<String,Long> {
    private final Map<String, Long> map;
    
    /**
     * Iterate over the given traceInfo and map all it's trace_name's
     * to the id generated by the given {@link TraceArchiveRecordIdGenerator}.
     * @param traceInfo the traceInfo to examine.
     * @param traceIdGenerator the generator used to generate IDs.
     * @throws DataStoreException if there is a problem reading the traceInfo.
     */
    public DefaultTraceNameConverter(TraceArchiveInfo traceInfo, TraceArchiveRecordIdGenerator traceIdGenerator ) throws DataStoreException {
        map = new HashMap<String, Long>(traceInfo.size());
        for(TraceArchiveRecord record : traceInfo){
            String traceId = traceIdGenerator.generateIdFor(record);
            Long traceName = Long.parseLong(record.getAttribute(TraceInfoField.TRACE_NAME));
            map.put(traceId, traceName);
        }
    }

    @Override
    public Long convertId(String id) {
        if(map.containsKey(id)){
            return map.get(id);
        }
        throw new NoSuchElementException(String.format("trace name with id %s not found", id));
    }

}
