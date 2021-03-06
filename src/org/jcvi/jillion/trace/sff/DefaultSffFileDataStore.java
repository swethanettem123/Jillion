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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
/**
* {@code DefaultSffFileDataStore} creates {@link SffFileDataStore}
* instances that store all the {@link SffFlowgram}s
* in a Map.  This implementation is not very 
* memory efficient and therefore should not be used
* for large sff files.
* @author dkatzel
*/
final class DefaultSffFileDataStore {

	private DefaultSffFileDataStore(){
		//can not instantiate
	}
	/**
	 * Create a new {@link SffFileDataStore} by parsing
	 * the entire given sff file and include all
	 * the reads in the DataStore.
	 * @param sffFile the sff encoded file to parse.
	 * @return a new {@link SffFileDataStore} containing
	 * all the reads in the sff file; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile is null.
	 */
	public static SffFileDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new {@link SffFileDataStore} by parsing
	 * the entire given sff file but include only
	 * the reads that are accepted by the given {@link DataStoreFilter}.
	 * @param sffFile the sff encoded file to parse.
	 * @param filter the {@link DataStoreFilter} to use
	 * to filter out any reads in the sff file; can not be null.
	 * @return a new {@link SffFileDataStore} containing
	 * only the reads accepted by the given filter; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile or filter are null.
	 */
	public static SffFileDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffParser parser = SffFileParser.create(sffFile);
		parser.parse(visitor);
		
		return visitor.builder.build();
	}
	
	
	/**
	 * {@link SffVisitor} implementation 
	 * that puts flowgrams into a datastore
	 * as each record is visited.
	 * @author dkatzel
	 *
	 */
	private static final class Visitor implements SffVisitor{
		private SffDataStoreBuilder builder;
		
		private final DataStoreFilter filter;
		
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffVisitorCallback callback,
				SffCommonHeader header) {
			builder = new SffDataStoreBuilder(header.getKeySequence(), header.getFlowSequence(), (int)header.getNumberOfReads());
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffVisitorCallback callback,
				final SffReadHeader readHeader) {
			if(filter.accept(readHeader.getId())){
				return new SffFileReadVisitor(){

					@Override
					public void visitReadData(SffReadData readData) {
						 builder.addFlowgram(SffFlowgramImpl.create(readHeader, readData));
						
					}

					@Override
					public void visitEnd() {
						//no-op
						
					}
					
				};
			}
			return null;
		}

		@Override
		public void end() {
			//no-op
			
		}
		
	}
}
