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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.internal.fasta.AbstractFastaFileDataStoreBuilderVisitor;
/**
 * {@code DefaultQualityFastaFileDataStore} is the default implementation
 * of {@link AbstractQualityFastaFileDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeQualityFastaFileDataStore
 *
 */
final class DefaultQualityFastaFileDataStore {
    
	private DefaultQualityFastaFileDataStore(){
		//can not instantiate
	}
    public static QualitySequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
    	return create(fastaFile,null);
    }
    
    public static QualitySequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
    	QualityFastaDataStoreBuilderVisitor builder = createBuilder(filter);
    	FastaFileParser.parse(fastaFile, builder);
    	return builder.build();
    }
    
    public static QualitySequenceFastaDataStore create(InputStream fastaStream) throws FileNotFoundException{
    	return create(fastaStream,null);
    }
    public static QualitySequenceFastaDataStore create(InputStream fastaStream, DataStoreFilter filter) throws FileNotFoundException{
    	QualityFastaDataStoreBuilderVisitor builder = createBuilder(filter);
    	FastaFileParser.parse(fastaStream, builder);
    	return builder.build();
    }
    public static QualityFastaDataStoreBuilderVisitor createBuilder(){
    	return createBuilder(null);
    }
    public static QualityFastaDataStoreBuilderVisitor createBuilder(DataStoreFilter filter){
    	return new DefaultQualityFastaDataStoreBuilderVisitor(filter);
    }
    
    
    private static class DefaultQualityFastaDataStoreBuilderVisitor 
    				extends AbstractFastaFileDataStoreBuilderVisitor<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>
    		implements QualityFastaDataStoreBuilderVisitor{
		
		public DefaultQualityFastaDataStoreBuilderVisitor(DataStoreFilter filter){
			super(new DefaultQualityFastaDataStoreBuilder(),filter);
		}

		@Override
		protected QualitySequenceFastaRecord createFastaRecord(String id,
				String comment, String entireBody) {
			return new QualitySequenceFastaRecordBuilder(id, entireBody)
											.comment(comment)
											.build();
		}
    	
    }

}