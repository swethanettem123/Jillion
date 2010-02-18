/*
 * Created on Jan 27, 2010
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
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.io.IOUtil;

public class DefaultPositionFastaFileDataStore extends AbstractPositionFastaFileDataStore{

private final Map<String, PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> map = new HashMap<String, PositionFastaRecord<EncodedGlyphs<ShortGlyph>>>();
private DataStore<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> datastore;
/**
 * @param fastaRecordFactory
 */
public DefaultPositionFastaFileDataStore(
        PositionFastaRecordFactory fastaRecordFactory) {
    super(fastaRecordFactory);
}

/**
 * Convenience constructor using the {@link DefaultPositionFastaFileDataStore}.
 * This call is the same as {@link #DefaultPositionFastaFileDataStore(QualityFastaRecordFactory)
 * new DefaultPositionFastaFileDataStore(DefaultPositionFastaRecordFactory.getInstance());}
 */
public DefaultPositionFastaFileDataStore() {
    super();
}

public DefaultPositionFastaFileDataStore(File fastaFile,PositionFastaRecordFactory fastaRecordFactory) throws FileNotFoundException {
    super(fastaRecordFactory);
    parseFastaFile(fastaFile);
}
public DefaultPositionFastaFileDataStore(File fastaFile) throws FileNotFoundException {
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
    datastore = new SimpleDataStore<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>>(map);
}
@Override
public boolean contains(String id) throws DataStoreException {
    return datastore.contains(id);
}
@Override
public PositionFastaRecord<EncodedGlyphs<ShortGlyph>> get(String id)
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
public Iterator<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> iterator() {
    return datastore.iterator();
}
}
