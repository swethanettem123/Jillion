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
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.net.URI;

import org.jcvi.jillion.assembly.clc.cas.transform.ReadData.Builder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
/**
 * {@code FastaConsedPhdAdaptedIterator}
 * 
 *  that will try to find a corresponding qual file
 * and look up the quality scores.
 *  
 *  is a PhdReadRecord generator
 * for chromatograms.  Since CLC's reference mappers don't handle chromatograms,
 * they have to be passed in as fasta files.  This class will find the chromatogram in the 
 * chromat_dir folder with the same name as the current read id in the fasta and create a {@link PhdReadRecord}
 * with the correct sequence, qualities and postions from the chromatogram as well
 * as a correctly formatted {@link PhdInfo} so consed can correctly display the chromatogram
 * wave forms.
 * @author dkatzel
 *
 */
class FastaReadDataAdaptedIterator implements StreamingIterator<ReadData>{
	
	private final StreamingIterator<NucleotideFastaRecord> fastaIterator;
	private final QualityFastaDataStore qualDataStore;
	
	private final URI fastaFileUri;
	
	public FastaReadDataAdaptedIterator(
			StreamingIterator<NucleotideFastaRecord> fastaIterator,
			File fastaFile){
		this.fastaIterator = fastaIterator;	
		this.fastaFileUri = fastaFile.toURI();
		
		File qualFile = new File(fastaFile.getParentFile(), FileUtil.getBaseName(fastaFile)+".qual");
		if(qualFile.exists()){
			try {
				qualDataStore = new QualityFastaFileDataStoreBuilder(qualFile)
									.build();
			} catch (Exception e) {
				throw new IllegalStateException("error parsing corresponding qual file : " + qualFile.getAbsolutePath(), e);
			}
		}else{
			qualDataStore = null;
		}
	}
	@Override
	public boolean hasNext() {
		return fastaIterator.hasNext();
	}

	@Override
	public ReadData next() {
		NucleotideFastaRecord nextFasta = fastaIterator.next();
		String id = nextFasta.getId();
		ReadData.Builder builder = new ReadData.Builder(nextFasta.getId(), nextFasta.getSequence())
										.setUri(fastaFileUri);
		//check for qualities
		if(qualDataStore != null){
			try {
				QualityFastaRecord qualRecord = qualDataStore.get(id);
				if(qualRecord !=null){
					builder.setQualities(qualRecord.getSequence());
				}
			} catch (DataStoreException e) {
				throw new IllegalStateException("error getting qualities from fasta", e);
			}
			
		}
		
		updateField(builder);
		return builder.build();
				
	}
	
	protected void updateField(Builder builder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void remove() {
		fastaIterator.remove();
		
	}
	@Override
	public void close() {
		fastaIterator.close();
		IOUtil.closeAndIgnoreErrors(qualDataStore);
	}

}
