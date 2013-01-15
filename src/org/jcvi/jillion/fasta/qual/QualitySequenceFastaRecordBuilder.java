package org.jcvi.jillion.fasta.qual;

import java.util.Scanner;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordBuilder;
/**
 * {@code QualitySequenceFastaRecordBuilder} is a factory class
 * that makes instances of {@link QualitySequenceFastaRecord}s.
 * Depending on the different parameters, the factory might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaRecordBuilder extends AbstractFastaRecordBuilder<PhredQuality, QualitySequence,QualitySequenceFastaRecord> {

	/**
	 * Create a new builder instance for the given id and 
	 * entire quality values as a human readable string.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param entireRecordBody the body of the quality fasta record,
     *  may contain whitespace to separate quality values; can not be null.
     *  @throws NullPointerException if either parameter is null.
	 */
	public QualitySequenceFastaRecordBuilder(String id, String entireRecordBody){
		this(id,parseQualitySequence(entireRecordBody));
	}
	/**
	 * Create a new builder instance for the given id and {@link QualitySequence}.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param sequence the {@link QualitySequence} for this quality fasta record;
	 * can not be null.
	 *  @throws NullPointerException if either parameter is null.
	 */
	
	private static QualitySequence parseQualitySequence(String sequence) {
		Scanner scanner = new Scanner(sequence);
    	QualitySequenceBuilder builder = new QualitySequenceBuilder();
    	while(scanner.hasNextByte()){
    		builder.append(scanner.nextByte());
    	}
    	scanner.close();
    	return builder.build();
	}
	public QualitySequenceFastaRecordBuilder(String id, QualitySequence sequence) {
		super(id, sequence);
	}
	@Override
	protected QualitySequenceFastaRecord createNewInstance(String id, QualitySequence sequence,
			String comment) {
		if(comment ==null){
			return new UncommentedQualitySequenceFastaRecord(id, sequence);
		}
		return new CommentedQualitySequenceFastaRecord(id, sequence, comment);
	}
	
	
}