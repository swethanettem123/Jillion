package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.trace.sff.SffFileParserCallback.SffFileMemento;




public class IndexedSffFileDataStore2 {
	
	public static FlowgramDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffFileParser2 parser = new SffFileParser2(sffFile);
		parser.accept(visitor);
		
		return visitor.build(parser);
	}
	
	
	
	private static final class Visitor implements SffFileVisitor2{
		private Map<String, SffFileMemento> mementos;
		
		private final DataStoreFilter filter;
		
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			mementos = new LinkedHashMap<String,SffFileMemento>((int)header.getNumberOfReads());
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			if(filter.accept(readHeader.getId())){
				mementos.put(readHeader.getId(), callback.createMemento());
			}
			//always skip read data we'll read it later
			return null;
		}

		@Override
		public void endSffFile() {
			//no-op
			
		}
	
		FlowgramDataStore build(SffFileParser2 parser){
			return new DataStoreImpl(parser, mementos);
		}
		
	}
	
	
	private static class DataStoreImpl implements FlowgramDataStore{
		private final SffFileParser2 parser; //parser has the file ref
		private volatile boolean closed=false;
		
		private final Map<String, SffFileMemento> momentos;

		public DataStoreImpl(SffFileParser2 parser,
				Map<String, SffFileMemento> momentos) {
			this.parser = parser;
			this.momentos = momentos;
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			checkNotYetClosed();
			return IteratorUtil.createStreamingIterator(momentos.keySet().iterator());
		}

		@Override
		public Flowgram get(String id) throws DataStoreException {
			checkNotYetClosed();
			SffFileMemento momento = momentos.get(id);
			if(momento == null){
				return null;
			}
			SingleRecordVisitor visitor = new SingleRecordVisitor();
			try {
				parser.accept(visitor, momento);
			} catch (IOException e) {
				throw new DataStoreException("error reparsing file", e);
			}
			return visitor.flowgram;
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			checkNotYetClosed();
			return momentos.containsKey(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			checkNotYetClosed();
			return momentos.size();
		}

		@Override
		public boolean isClosed() {
			return closed;
		}

		@Override
		public StreamingIterator<Flowgram> iterator() throws DataStoreException {
			checkNotYetClosed();
			// too lazy to write faster implementation for now
			return new DataStoreIterator<Flowgram>(this);
		}

		@Override
		public void close() throws IOException {
			closed=true;
			
		}
		
		private void checkNotYetClosed() throws DataStoreException{
			if(closed){
				throw new DataStoreException("datastore is closed");
			}
		}
		
		
		
	}
	
	private static class SingleRecordVisitor implements SffFileVisitor2{
		private Flowgram flowgram;
		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			//we should only see the read we care about
			return new SffFileReadVisitor(){

				@Override
				public void visitReadData(SffFileParserCallback callback,
						SffReadData readData) {
					flowgram =SffFlowgram.create(readHeader, readData);
					
				}

				@Override
				public void visitEndOfRead(SffFileParserCallback callback) {
					callback.stopParsing();
					
				}
				
			};
		}

		@Override
		public void endSffFile() {
			// TODO Auto-generated method stub
			
		}
		
	}
}