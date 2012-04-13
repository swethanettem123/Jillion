package org.jcvi.common.core.seq.fastx.fasta;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.AcceptingFastXFilter;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;

public abstract class AbstractFastaFileDataStoreBuilderVisitor<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>, D extends DataStore<F>> extends AbstractFastaVisitor implements FastaFileDataStoreBuilderVisitor<S,T,F,D>{
	private final FastaDataStoreBuilder<S,T,F,D> builder;
	private final FastXFilter filter;
	public AbstractFastaFileDataStoreBuilderVisitor(FastaDataStoreBuilder<S,T,F,D> builder){
		this(builder,null);
	}
	public AbstractFastaFileDataStoreBuilderVisitor(FastaDataStoreBuilder<S,T,F,D> builder, FastXFilter filter){
		if(builder==null){
			throw new NullPointerException("builder can not be null");
		}
		if(filter ==null){
			this.filter = AcceptingFastXFilter.INSTANCE;
		}else{
			this.filter = filter;
		}
		this.builder = builder;
		
	}
	
	@Override
	public D build() {
		return builder.build();
	}

	/**
	 * Adds the current record to this datastore by 
	 * delegating the parameters to {@link #createFastaRecord(String, String, String)}
	 * and inputing the resulting {@link FastaRecord} to
	 * {@link #addFastaRecord(FastaRecord)}.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean visitRecord(String id, String comment, String entireBody) {
		addFastaRecord(createFastaRecord(id, comment, entireBody));
		return true;
	}
	
	protected abstract F createFastaRecord(String id, String comment, String entireBody);

	@Override
	public <E extends F> FastaDataStoreBuilder<S, T, F, D>  addFastaRecord(E fastaRecord) {
		if(filter.accept(fastaRecord.getId())){
			builder.addFastaRecord(fastaRecord);
		}
		return this;
	}
	/**
	 * @return true.
	 */
	@Override
	public boolean supportsAddFastaRecord() {
		return true;
	}
	
}