/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.glyph.phredQuality.datastore;

import java.io.File;
import java.sql.SQLException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.QualityFastaRecordUtil;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class H2QualityDataStore extends AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> implements QualityDataStore{

    protected static final RunLengthEncodedGlyphCodec CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
    /**
     * @throws DataStoreException
     */
    public H2QualityDataStore() throws DataStoreException {
        super();
    }

    /**
     * @param database
     * @throws DataStoreException
     */
    public H2QualityDataStore(File database) throws DataStoreException {
        super(database);
    }

    @Override
    public EncodedGlyphs<PhredQuality> get(String id) throws DataStoreException {
        try {
            byte[] ret = this.getData(id);
            if(ret==null){
                return null;
            }
            return new DefaultEncodedGlyphs<PhredQuality>(CODEC, ret);
        } catch (SQLException e) {
            throw new DataStoreException("could not get data for "+id, e);
        }
       
    }

    @Override
    public void insertRecord(String id, String positions)
            throws DataStoreException {
        try {
            this.insertRecord(id,
                    CODEC.encode(
                            QualityFastaRecordUtil.buildFastaRecord(id,null,positions).getValues().decode()));
        } catch (SQLException e) {
            throw new DataStoreException("error inserting qualities for "+id, e);
        }
        
    }
    

}
