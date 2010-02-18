/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.glyph.GlyphFactory;

public abstract class ByteGlyphFactory<G extends ByteGlyph> implements GlyphFactory<G, Byte>{

    private final Map<Number, G> MAP = new HashMap<Number, G>();
    
    @Override
    public List<G> getGlyphsFor(List<Byte> s) {
        List<G> glyphs = new ArrayList<G>();
        for(int i=0; i<s.size(); i++){
            glyphs.add(getGlyphFor(s.get(i)));
        }
        return glyphs;
    }

    public List<G> getGlyphsFor(byte[] bytes) {
        List<G> glyphs = new ArrayList<G>();
        for(int i=0; i<bytes.length; i++){
            glyphs.add(getGlyphFor(bytes[i]));
        }
        return glyphs;
    }

    @Override
    public synchronized G getGlyphFor(Byte b) {
        if(MAP.containsKey(b)){
            return MAP.get(b);
        }
        G newGlyph = createNewGlyph(b);
        MAP.put(b, newGlyph);
        return newGlyph;
    }

    protected abstract G createNewGlyph(Byte b);
    
}
