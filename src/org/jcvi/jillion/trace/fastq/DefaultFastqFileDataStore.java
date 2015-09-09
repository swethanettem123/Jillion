/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code DefaultFastqFileDataStore} is the default implementation
 * of {@link FastqDataStore} which stores
 * all {@link FastqRecord}s from a file in memory.  This is only recommended for small fastq
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeFastqFileDataStore
 *
 */
final class DefaultFastqFileDataStore{
	
	private DefaultFastqFileDataStore(){
		//can not instantiate
	}


	/**
	 * Create a new {@link FastqDataStore} instance for all the
	 * {@link FastqRecord}s that are contained in the given fastq file. All
	 * records in the file must have their qualities encoded in a manner that
	 * can be parsed by the given {@link FastqQualityCodec} (if provided).
	 * 
	 * @param fastqFile
	 *            the fastq file to parse, must exist and can not be null.
	 * @param qualityCodec
	 *            an optional {@link FastqQualityCodec} that should be used to
	 *            decode the fastq file. If this value is null, then the
	 *            datastore implementation will try to guess the codec used
	 *            which might have a performance penalty associated with it.
	 * @return a new {@link FastqFileDataStore} instance.
	 * @throws IOException
	 *             if there is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if fastqFile is null.
	 */
   public static FastqFileDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws IOException{
	  return create(fastqFile, DataStoreFilters.alwaysAccept(), qualityCodec);
   }

	/**
	 * Create a new {@link FastqDataStore} instance for the {@link FastqRecord}s
	 * that are accepted by the given {@link DataStoreFilter} that are contained in
	 * the given fastq file. Any records that are not accepted by the filter
	 * will not be included in the returned {@link FastqDataStore}. All of those
	 * records must have their qualities encoded a manner that can be parsed by
	 * the given {@link FastqQualityCodec} (if provided).
	 * 
	 * @param fastqFile
	 *            the fastq file to parse, must exist and can not be null.
	 * @param filter
	 *            an instance of {@link Predicate}  that can be used to filter
	 *            out some {@link FastqRecord}s from the datastore by their ID.
	 * @param qualityCodec
	 *            the {@link FastqQualityCodec} needed to parse the encoded
	 *            quality values in each record. If this value is null, then the
	 *            datastore implementation will try to guess the codec used
	 *            which might have a performance penalty associated with it.
	 * @return a new {@link FastqFileDataStore} instance containing only those
	 *         records that pass the filter.
	 * @throws IOException
	 *             if thre is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if either fastqFile or filter is null.
	 */
   public static FastqFileDataStore create(File fastqFile, Predicate<String> filter,FastqQualityCodec qualityCodec) throws IOException{
	   FastqParser parser = FastqFileParser.create(fastqFile);
	   return create(parser, qualityCodec, filter, null);
   }

   /**
	 * Create a new {@link FastqDataStore} instance for the {@link FastqRecord}s
	 * that are accepted by the given {@link DataStoreFilter} that are contained in
	 * the given fastq file. Any records that are not accepted by the filter
	 * will not be included in the returned {@link FastqDataStore}. All of those
	 * records must have their qualities encoded a manner that can be parsed by
	 * the given {@link FastqQualityCodec} (if provided).
	 * 
	 * @param parser
	 *            the {@link FastqParser} instance to parse, must exist and can not be null.
	 * @param filter
	 *            an instance of {@link DataStoreFilter} that can be used to filter
	 *            out some {@link FastqRecord}s from the datastore.
	 * @param qualityCodec
	 *            the {@link FastqQualityCodec} needed to parse the encoded
	 *            quality values in each record. If this value is null, then the
	 *            datastore implementation will try to guess the codec used
	 *            which might have a performance penalty associated with it.
	 * @return a new {@link FastqFileDataStore} instance containing only those
	 *         records that pass the filter.
	 * @throws IOException
	 *             if thre is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if either fastqFile or filter is null.
	 */
	public static FastqFileDataStore create(FastqParser parser,
			FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter)
			throws IOException {
		DefaultFastqFileDataStoreBuilderVisitor2 visitor = new DefaultFastqFileDataStoreBuilderVisitor2(qualityCodec,filter, recordFilter);
		   
		   parser.parse(visitor);

		   return visitor.build();
	}
    
	private static final class DefaultFastqFileDataStoreBuilderVisitor2 implements FastqVisitor, Builder<FastqFileDataStore> {
		private final Predicate<String> filter;
		private final Predicate<FastqRecord> recordFilter;
		private final FastqQualityCodec qualityCodec;
		private final Map<String, FastqRecord> map = new LinkedHashMap<>();

		public DefaultFastqFileDataStoreBuilderVisitor2(
				FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter) {
			if(qualityCodec==null){
				throw new NullPointerException("quality codec can not be null");
			}
			if(filter==null){
				throw new NullPointerException("filter can not be null");
			}
			
			this.qualityCodec = qualityCodec;
			this.filter = filter;
			this.recordFilter = recordFilter;
		}

		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(!filter.test(id)){
				return null;
			}
			return new AbstractFastqRecordVisitor(id,optionalComment, qualityCodec) {
				
				@Override
				protected void visitRecord(FastqRecord record) {
				    if(recordFilter==null || recordFilter.test(record)){
					map.put(record.getId(), record);	
				    }
				}
			};
		}

		@Override
		public void visitEnd() {
			//no-op
		}
		@Override
		public void halted() {
			//no-op			
		}
		@Override
		public FastqFileDataStore build() {
			return new FastqFileDataStoreImpl(DataStoreUtil.adapt(FastqDataStore.class, map),
			                                    qualityCodec);
		}

	}
}
