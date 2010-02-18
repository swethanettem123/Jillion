/*
 * Created on Nov 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class QualitySffDataStore implements QualityDataStore{

    private static final GlyphCodec<PhredQuality> QUALITY_CODEC = 
        RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
    private final DataStore<? extends Flowgram> flowgramDataStore;
    private final boolean trim;
    /**
     * @param flowgramDataStore
     */
    public QualitySffDataStore(
            DataStore<? extends Flowgram> flowgramDataStore, boolean trim) {
        this.flowgramDataStore = flowgramDataStore;
        this.trim = trim;
    }
    public QualitySffDataStore(
            DataStore<? extends Flowgram> flowgramDataStore){
        this(flowgramDataStore, false);
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return flowgramDataStore.contains(id);
    }

    @Override
    public EncodedGlyphs<PhredQuality> get(String id) throws DataStoreException {
        final Flowgram flowgram = flowgramDataStore.get(id);
        EncodedGlyphs<PhredQuality> qualities= flowgram.getQualities();
        if(trim){
         return new DefaultEncodedGlyphs<PhredQuality>(QUALITY_CODEC, 
                 qualities.decode(SFFUtil.getTrimRangeFor(flowgram)));   
        }
        return qualities;
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return flowgramDataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return flowgramDataStore.size();
    }

    @Override
    public void close() throws IOException {
        flowgramDataStore.close();
        
    }

    @Override
    public Iterator<EncodedGlyphs<PhredQuality>> iterator() {
        return new DataStoreIterator<EncodedGlyphs<PhredQuality>>(this);
    }


}
