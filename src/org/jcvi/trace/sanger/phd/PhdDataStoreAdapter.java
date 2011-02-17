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

package org.jcvi.trace.sanger.phd;

import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.util.CloseableIterator;
/**
 * {@code PhdDataStoreAdapter} wraps a {@link DataStore}
 * of {@link Phd}s to match the interface of a
 * {@link PhdDataStore}.
 * 
 * @author dkatzel
 *
 */
public class PhdDataStoreAdapter implements PhdDataStore{

	private final DataStore<Phd> delegate;

	public PhdDataStoreAdapter(DataStore<Phd> delegate) {
		this.delegate = delegate;
	}


	@Override
	public CloseableIterator<String> getIds() throws DataStoreException {
		return delegate.getIds();
	}


	@Override
	public Phd get(String id) throws DataStoreException {
		return delegate.get(id);
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		return delegate.contains(id);
	}


	@Override
	public int size() throws DataStoreException {
		return delegate.size();
	}


	@Override
	public boolean isClosed() throws DataStoreException {
		return delegate.isClosed();
	}

	@Override
	public CloseableIterator<Phd> iterator() {
		return delegate.iterator();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
		
	}
	
	
}
