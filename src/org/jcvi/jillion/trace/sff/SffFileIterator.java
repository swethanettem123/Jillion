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
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * {@code SffFileIterator} is a {@link StreamingIterator}
 * that can iterate over {@link SffFlowgram}s contained
 * in a sff file.
 * @author dkatzel
 *
 */
final class SffFileIterator extends AbstractBlockingStreamingIterator<SffFlowgram>{

	private final File sffFile;
	private final DataStoreFilter filter;
	public static SffFileIterator createNewIteratorFor(File sffFile){
		return createNewIteratorFor(sffFile, DataStoreFilters.alwaysAccept());
	}
    public static SffFileIterator createNewIteratorFor(File sffFile, DataStoreFilter filter){
    	SffFileIterator iter;
			iter = new SffFileIterator(sffFile,filter);
			iter.start();
		
    	
    	return iter;
    }
	
	private SffFileIterator(File sffFile, DataStoreFilter filter){
		this.sffFile = sffFile;
		 this.filter =filter;
	}

	@Override
	protected void backgroundThreadRunMethod() {
		 try {
         	SffVisitor visitor = new SffVisitor() {
         		
         		
         		@Override
				public void visitHeader(SffVisitorCallback callback,
						SffCommonHeader header) {
					//no-op					
				}


				@Override
				public SffFileReadVisitor visitRead(
						SffVisitorCallback callback, final SffReadHeader readHeader) {
					if(filter.accept(readHeader.getId())){
						return new SffFileReadVisitor() {
							
							@Override
							public void visitReadData(SffReadData readData) {
								SffFileIterator.this.blockingPut(SffFlowgramImpl.create(readHeader, readData));
								
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
         	};
             SffFileParser.create(sffFile).parse(visitor);
         } catch (IOException e) {
             //should never happen
             throw new RuntimeException(e);
         }
		
	}
	
	

}
