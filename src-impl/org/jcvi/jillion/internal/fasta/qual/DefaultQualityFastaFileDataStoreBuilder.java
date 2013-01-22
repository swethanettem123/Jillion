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
package org.jcvi.jillion.internal.fasta.qual;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.qual.AbstractQualityFastaRecordVisitor;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaRecord;

public class DefaultQualityFastaFileDataStoreBuilder implements FastaFileVisitor, Builder<QualitySequenceFastaDataStore>{

	private final Map<String, QualitySequenceFastaRecord> fastaRecords = new LinkedHashMap<String, QualitySequenceFastaRecord>();
	
	private final DataStoreFilter filter;
	
	public DefaultQualityFastaFileDataStoreBuilder(DataStoreFilter filter){
		this.filter = filter;
	}
	@Override
	public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
			final String id, String optionalComment) {
		if(!filter.accept(id)){
			return null;
		}
		return new AbstractQualityFastaRecordVisitor(id,optionalComment){

			@Override
			protected void visitRecord(
					QualitySequenceFastaRecord fastaRecord) {
				fastaRecords.put(id, fastaRecord);
				
			}
			
		};
	}

	@Override
	public void visitEnd() {
		//no-op			
	}
	@Override
	public QualitySequenceFastaDataStore build() {
		return DataStoreUtil.adapt(QualitySequenceFastaDataStore.class,fastaRecords);
	}
	
}
