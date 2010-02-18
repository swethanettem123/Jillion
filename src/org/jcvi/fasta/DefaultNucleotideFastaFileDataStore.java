/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.IOUtil;
/**
 * {@code DefaultNucleotideFastaFileDataStore} is the default implementation
 * of {@link AbstractNucleotideFastaFileDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeNucleotideFastaFileDataStore
 *
 */
public class DefaultNucleotideFastaFileDataStore extends AbstractNucleotideFastaFileDataStore{
    private final Map<String, NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> map = new HashMap<String, NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>>();
    private DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> datastore;
    /**
     * @param fastaRecordFactory
     */
    public DefaultNucleotideFastaFileDataStore(
            NucleotideFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
    }
    
    /**
     * Convenience constructor using the {@link DefaultNucleotideFastaRecordFactory}.
     * This call is the same as {@link #DefaultNucelotideFastaFileDataStore(QualityFastaRecordFactory)
     * new DefaultNucelotideFastaFileDataStore(DefaultNucleotideFastaRecordFactory.getInstance());}
     */
    public DefaultNucleotideFastaFileDataStore() {
        super();
    }

    public DefaultNucleotideFastaFileDataStore(File fastaFile,NucleotideFastaRecordFactory fastaRecordFactory) throws FileNotFoundException {
        super(fastaRecordFactory);
        parseFastaFile(fastaFile);
    }
    public DefaultNucleotideFastaFileDataStore(File fastaFile) throws FileNotFoundException {
        super();
        parseFastaFile(fastaFile);
    }
    private void parseFastaFile(File fastaFile) throws FileNotFoundException {
        InputStream in = new FileInputStream(fastaFile);
        try{
        FastaParser.parseFasta(in, this);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Override
    public void visitRecord(String id, String comment, String recordBody) {
        map.put(id  , this.getFastaRecordFactory().createFastaRecord(id, comment,recordBody));
        
    }
    @Override
    public void close() throws IOException {
        map.clear();
        datastore.close();
    }
    
    
    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        datastore = new SimpleDataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>>(map);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    @Override
    public NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> get(String id)
            throws DataStoreException {
        return datastore.get(id);
    }
    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }
    @Override
    public Iterator<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> iterator() {
        return datastore.iterator();
    }
    

}
