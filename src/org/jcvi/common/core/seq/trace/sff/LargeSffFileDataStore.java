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
 * Created on Jan 28, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.DataStoreUtil;
import org.jcvi.common.core.datastore.impl.AbstractDataStore;
import org.jcvi.common.core.datastore.impl.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code LargeSffFileDataStore} is a {@link FlowgramDataStore}
 * implementation that doesn't store any read information in memory.
 *  No data contained in this
 * sff file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the sff file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.common.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 */
final class LargeSffFileDataStore extends AbstractDataStore<Flowgram> implements FlowgramDataStore{

    private final File sffFile;
    private Long size=null;
    private final DataStoreFilter filter;
    
    /**
     * Create a new instance of {@link LargeSffFileDataStore}.
     * @param sffFile the sff file to parse.
     * @return a new SffDataStore; never null.
     * @throws NullPointerException if sffFile is null.
     * @throws IOException if sffFile does not exist or is not a valid sff file.
     */
    public static FlowgramDataStore create(File sffFile) throws IOException{
    	return create(sffFile, DataStoreFilters.alwaysAccept());
    }
    
    /**
     * Create a new instance of {@link LargeSffFileDataStore}.
     * @param sffFile the sff file to parse.
     * @return a new SffDataStore; never null.
     * @throws NullPointerException if sffFile is null.
     * @throws IOException if sffFile does not exist or is not a valid sff file.
     */
    public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
    	verifyFileExists(sffFile);
    	verifyIsValidSff(sffFile);
    	
    	return new LargeSffFileDataStore(sffFile,filter);
    }
	private static void verifyFileExists(File sffFile)
			throws FileNotFoundException {
		if(sffFile ==null){
    		throw new NullPointerException("file can not be null");
    	}
    	if(!sffFile.exists()){
    		throw new FileNotFoundException("sff file does not exist");
    	}
	}
    private static void verifyIsValidSff(File f) throws IOException {
    	DataInputStream in=null;
    	try{
    		in = new DataInputStream(new FileInputStream(f));
    		//don't care about return value
    		//this will throw IOException if the file isn't valid
    		DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(in);
    	}finally{
    		IOUtil.closeAndIgnoreErrors(in);
    	}
		
	}
	/**
     * @param sffFile
     */
    private LargeSffFileDataStore(File sffFile, DataStoreFilter filter) {
        this.sffFile = sffFile;
        this.filter = filter;
    }

    @Override
	protected boolean containsImpl(String id) throws DataStoreException {
		return get(id)!=null;
	}
	@Override
	protected Flowgram getImpl(String id) throws DataStoreException {
		if(!filter.accept(id)){
			return null;
		}
		try{
        	SingleFlowgramVisitor singleVisitor = new SingleFlowgramVisitor(id);
        	SffFileParser.parse(sffFile, singleVisitor);
        	return singleVisitor.getFlowgram();
        } catch (IOException e) {
            throw new DataStoreException("could not read sffFile ",e);
        }
	}
	@Override
	protected synchronized long getNumberOfRecordsImpl() throws DataStoreException {
		if(this.size ==null){
			//since filter could mean we won't
			//accept everything we can't just
			//look at the value in the common header
			//must iterate through everything
			StreamingIterator<Flowgram> iter=null;
			
        	
        	try{
        		iter = iterator();
        		long count=0;
        		while(iter.hasNext()){
        			iter.next();
        			count++;
        		}
        		size = count;
        	}catch(Exception e){
        		 throw new DataStoreException("could not parse sffFile ",e);
        	}finally{
        		IOUtil.closeAndIgnoreErrors(iter);
        	}
        }
        return size;
	}
	@Override
	protected StreamingIterator<String> idIteratorImpl()
			throws DataStoreException {
		return new SffIdIterator(this.iterator());
	}
	@Override
	protected StreamingIterator<Flowgram> iteratorImpl()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this,
				SffFileIterator.createNewIteratorFor(sffFile,filter));
	}
	
   

   

    @Override
	protected void handleClose() throws IOException {
		//no-op
		
	}
	
    /**
     * {@code SffIdIterator} is a {@link StreamingIterator}
     * that wraps the Iterator of Flowgrams and returns just
     * the id of each record when {@link Iterator#next()}
     * is called.
     * @author dkatzel
     *
     */
    private static final class SffIdIterator implements StreamingIterator<String>{
        
        private final StreamingIterator<Flowgram> iter;
       
        SffIdIterator(StreamingIterator<Flowgram> iter){
        	this.iter= iter;
        }
        
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            return iter.next().getId();
        }

        @Override
        public void remove() {
            iter.remove();            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            iter.close();
            
        }
        
    }
    
    
    private static final class SingleFlowgramVisitor implements SffFileVisitor{
        private final String idToFind;
        private SffReadHeader readHeader=null;
        private Flowgram flowgram=null;
        private SingleFlowgramVisitor(String idToFind) {
			this.idToFind = idToFind;
		}

		public Flowgram getFlowgram() {
			return flowgram;
		}

		@Override
        public ReadDataReturnCode visitReadData(SffReadData readData) {
            flowgram = SffFlowgram.create(readHeader, readData);
            return ReadDataReturnCode.STOP_PARSING;
        }
        
        @Override
        public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
            return CommonHeaderReturnCode.PARSE_READS;
        }

        @Override
        public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
        	if(readHeader.getId().equals(idToFind)){
        		this.readHeader = readHeader;
        		return ReadHeaderReturnCode.PARSE_READ_DATA;
        	}
        	return ReadHeaderReturnCode.SKIP_CURRENT_READ;
        }

        @Override
        public void visitEndOfFile() {
        	//no-op
        }

        @Override
        public void visitFile() {
        	//no-op
        }
    }
    
}
