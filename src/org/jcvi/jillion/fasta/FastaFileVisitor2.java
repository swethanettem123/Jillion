package org.jcvi.jillion.fasta;


public interface FastaFileVisitor2 {

	/**
     * Visit the definition line of the current fasta record.
     * @param id the id of this record as a String
     * @param optionalComment the comment for this record.  This comment
     * may have white space.  If no comment exists, then this
     * parameter will be null.
     * @return an instance of {@link FastaRecordVisitor};
     * if this method returns null, then that means
     * to skip the current record.
     */
	FastaRecordVisitor visitDefline(FastaVisitorCallback callback, String id, String optionalComment);
	
	void visitEnd();
}
