/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceDataStore;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractProteinFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.ProteinFastaFileDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.internal.fasta.AdaptedFastaDataStore;

public final class DefaultProteinFastaDataStore{
	
	private DefaultProteinFastaDataStore(){		
		//can not instantiate
	}
	public static ProteinFastaFileDataStore create(File fastaFile) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder();
		return parseFile(fastaFile, builder);
	}
	
	public static ProteinFastaFileDataStore create(InputStream in) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder();
		return parseFile(in, builder);
	}
	public static ProteinFastaFileDataStore create(File fastaFile, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder(filter, recordFilter);
		return parseFile(fastaFile, builder);
	}
	public static ProteinFastaFileDataStore create(FastaParser parser, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder(filter, recordFilter);
		return create(parser, builder);
	}
	public static ProteinFastaFileDataStore create(FastaParser parser) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder();
		return create(parser, builder);
	}
	public static ProteinFastaFileDataStore create(InputStream in, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder(filter, recordFilter);
		return parseFile(in, builder);
	}
	private static ProteinFastaFileDataStore parseFile(InputStream in, DefaultProteinFastaDataStoreBuilder visitor) throws IOException{
		FastaParser parser = FastaFileParser.create(in);
		return create(parser, visitor);
	}
	private static ProteinFastaFileDataStore create(FastaParser parser,
			DefaultProteinFastaDataStoreBuilder builder) throws IOException {
		parser.parse(builder);
		return builder.build();
	}
	private static ProteinFastaFileDataStore parseFile(File fastaFile, DefaultProteinFastaDataStoreBuilder visitor) throws IOException{
		FastaParser parser = FastaFileParser.create(fastaFile);
		return create(parser, visitor);
	}
	private static DefaultProteinFastaDataStoreBuilder createBuilder(){
		return createBuilder(DataStoreFilters.alwaysAccept(),null);
	}
	private static DefaultProteinFastaDataStoreBuilder createBuilder(Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter){
		return new DefaultProteinFastaDataStoreBuilder(filter, recordFilter);
	}
	private static final class DefaultProteinFastaDataStoreBuilder implements FastaVisitor, Builder<ProteinFastaFileDataStore>{

		private final Map<String, ProteinFastaRecord> fastaRecords = new LinkedHashMap<String, ProteinFastaRecord>();
		
		private final Predicate<String> filter;
		private final Predicate<ProteinFastaRecord> recordFilter;
		
		public DefaultProteinFastaDataStoreBuilder(Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter){
			this.filter = filter;
			this.recordFilter = recordFilter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.test(id)){
				return null;
			}
			return new AbstractProteinFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						ProteinFastaRecord fastaRecord) {
				    if(recordFilter ==null || recordFilter.test(fastaRecord)){
					fastaRecords.put(id, fastaRecord);
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
		public ProteinFastaFileDataStore build() {
			return new AdaptedProteinFastaDataStore(fastaRecords);
		}
		
	}
	
	private static final class AdaptedProteinFastaDataStore extends AdaptedFastaDataStore<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinSequenceDataStore> implements ProteinFastaFileDataStore{

        public AdaptedProteinFastaDataStore(
                Map<String, ProteinFastaRecord> map) {
            super(map);
        }

        @Override
        public ProteinSequenceDataStore asSequenceDataStore(){
            return DataStore.adapt(ProteinSequenceDataStore.class, this, ProteinFastaRecord::getSequence);
        }
	    
	}
}
