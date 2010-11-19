/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Aug 1, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
/**
 * <code>AlphabetData</code> is a composite object
 * containing a {@link ByteBuffer} of data and an {@link Alphabet}
 * to decode it.
 * @author dkatzel
 *
 *
 */
public class  DefaultEncodedGlyphs<T extends Glyph> implements EncodedGlyphs<T> {
    /**
     * codec used to decode the data.
     */
    private GlyphCodec<T> codec;
    /**
     * Our data.
     */
    private byte[] data;
    /**
     * Convenience constructor.  This is
     * the same as calling 
     * <code>new DefaultEncodedGlyphs(codec, codec.encode(glyphsToEncode));</code>
     * @param codec
     * @param glyphsToEncode
     */
    public DefaultEncodedGlyphs(GlyphCodec<T> codec, Collection<T> glyphsToEncode) {
        this(codec, codec.encode(glyphsToEncode));
    }
    /**
     * @param codec
     * @param data
     */
    public DefaultEncodedGlyphs(GlyphCodec<T> codec, byte[] data) {
        this.codec = codec;
        //defensive copy
        this.data = Arrays.copyOf(data, data.length);
    }

    public List<T> decode(){
        return codec.decode(data);
    }
    public long getLength(){
        return codec.decodedLengthOf(data);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codec == null) ? 0 : codec.hashCode());
        result = prime * result + Arrays.hashCode(data);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultEncodedGlyphs))
            return false;
        DefaultEncodedGlyphs other = (DefaultEncodedGlyphs) obj;
        return CommonUtil.similarTo(codec, other.codec) &&
        Arrays.equals(data, other.data);
       
    }
    @Override
    public T get(int index) {
        return codec.decode(data, index);
    }
    @Override
    public List<T> decode(Range range) {
        if(range ==null){
            return decode();
        }
        List<T> result = new ArrayList<T>();
        if(range.isSubRangeOf(Range.buildRangeOfLength(0, getLength()))){
            for(long index : range){
                result.add(get((int)index));
            }
        }
        return result;
    }
    @Override
    public String toString() {
        return decode().toString();
    }
    


}
