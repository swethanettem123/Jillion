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

public final class ShortGlyphFactory implements GlyphFactory<ShortGlyph, Short>{
    private static final Map<Number, ShortGlyph> MAP = new HashMap<Number, ShortGlyph>();
    
    private static final ShortGlyphFactory INSTANCE = new ShortGlyphFactory();
    
    private ShortGlyphFactory(){}
    
    public static ShortGlyphFactory getInstance(){
        return INSTANCE;
    }
    
    public List<ShortGlyph> getGlyphsFor(short[] shorts) {
        List<ShortGlyph> glyphs = new ArrayList<ShortGlyph>();
        for(int i=0; i<shorts.length; i++){
            glyphs.add(getGlyphFor(shorts[i]));
        }
        return glyphs;
    }
    
    public synchronized ShortGlyph getGlyphFor(int b) {
        return getGlyphFor(Short.valueOf((short)Math.min(b, Short.MAX_VALUE)));
    }
    public synchronized ShortGlyph getGlyphFor(Short b) {
        if(MAP.containsKey(b)){
            return MAP.get(b);
        }
        ShortGlyph newGlyph = new ShortGlyph(b);
        MAP.put(b, newGlyph);
        return newGlyph;
    }
    
    @Override
    public List<ShortGlyph> getGlyphsFor(List<Short> shorts) {
        List<ShortGlyph> glyphs = new ArrayList<ShortGlyph>();
        for(int i=0; i<shorts.size(); i++){
            glyphs.add(getGlyphFor(shorts.get(i)));
        }
        return glyphs;
    }


}
