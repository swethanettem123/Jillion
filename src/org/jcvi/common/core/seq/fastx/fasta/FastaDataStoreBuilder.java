package org.jcvi.common.core.seq.fastx.fasta;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.util.Builder;
/**
 * {@code FastaDataStoreBuilder} is a {@link Builder}
 * for {@link DataStore}s of {@link FastaRecord}s.
 * @author dkatzel
 *
 * @param <S> the type of {@link Symbol} in the sequence of the fasta.
 * @param <T> the {@link Sequence} of the fasta.
 * @param <F> the {@link FastaRecord} type.
 * @param <D> the {@link DataStore} type to build.
 */
public interface FastaDataStoreBuilder<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>, D extends DataStore<F>> extends Builder<D>{
	/**
	 * Add the given {@link FastaRecord} to this builder.
	 * @param fastaRecord the fastaRecord to add.
	 * @return this.
	 * @throws NullPointerException if fastaRecord is null.
	 */
	public FastaDataStoreBuilder<S,T,F,D> addFastaRecord(F fastaRecord);
	
}
