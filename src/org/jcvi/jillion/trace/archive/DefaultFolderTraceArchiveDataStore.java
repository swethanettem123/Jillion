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

import org.jcvi.jillion.core.datastore.DataStoreException;

public class DefaultFolderTraceArchiveDataStore extends AbstractFolderTraceArchiveDataStore{

    public DefaultFolderTraceArchiveDataStore(String rootDirPath,
            TraceArchiveInfo traceArchiveInfo) {
        super(rootDirPath, traceArchiveInfo);
    }

    @Override
    public TraceArchiveTrace get(String id) throws DataStoreException {
        return createTraceArchiveTrace(id);
    }
    protected TraceArchiveTrace createTraceArchiveTrace(String id)
                                        throws DataStoreException {
      TraceArchiveRecord record = getTraceArchiveInfo().get(id);
      if(record ==null){
    	  return null;
      }
      return new DefaultTraceArchiveTrace(record,getRootDirPath());
      
    }

    @Override
    public String toString() {
        return "Trace archive for folder " + getRootDirPath();
    }

    
    
    
}
