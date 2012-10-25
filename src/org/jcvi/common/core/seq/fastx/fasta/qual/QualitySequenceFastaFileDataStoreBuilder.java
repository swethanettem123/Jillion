package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaFileDataStoreBuilder;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

/**
 * {@code QualitySequenceFastaFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link QualitySequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>{

	
	public QualitySequenceFastaFileDataStoreBuilder(File fastaFile)
			throws IOException {
		super(fastaFile);
	}

	
	@Override
	protected QualitySequenceFastaDataStore createNewInstance(File fastaFile,
			DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		switch(hint){
			case OPTIMIZE_RANDOM_ACCESS_SPEED: return DefaultQualityFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY: return IndexedQualityFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_ITERATION: return LargeQualityFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown hint : "+ hint);
		}
	}
}