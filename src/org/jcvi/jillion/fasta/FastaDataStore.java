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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.Symbol;
import org.jcvi.jillion.core.datastore.DataStore;
/**
 * {@code FastaDataStore} is a marker interface
 * for a {@link DataStore} for {@link FastaRecord}s.
 * @author dkatzel
 *
 * @param <S> the type of {@link Symbol} in the fasta encoding.
 * @param <T> the type of {@link Sequence} of {@link Symbol}s in the fasta.
 * @param <F> the type of {@link FastaRecord} in the datastore.
 */
public interface FastaDataStore<S extends Symbol, T extends Sequence<S>,F extends FastaRecord<S,T>> extends DataStore<F>{

    

}
