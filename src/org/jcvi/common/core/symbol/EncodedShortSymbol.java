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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
/**
 * {@code EncodedShortGlyph} encodes a {@link Sequence}
 * of {@link ShortSymbol}s.
 * @author dkatzel
 */
public class EncodedShortSymbol implements Sequence<ShortSymbol>{

    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private final short[] data;
    public EncodedShortSymbol(List<ShortSymbol> shorts){
        this.data = encode(shorts);
    }
    private short[] encode(List<ShortSymbol> shorts) {
        ShortBuffer buffer = ShortBuffer.allocate(shorts.size());
        for(ShortSymbol byteGlyph : shorts){
            buffer.put(byteGlyph.getValue().shortValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ShortSymbol> decode() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ShortSymbol get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ShortSymbol> decode(Range range) {
        if(range==null){
            return decode();
        }
        List<ShortSymbol> result = new ArrayList<ShortSymbol>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
     @Override
     public Iterator<ShortSymbol> iterator() {
         return new ShortSequenceIterator();
     }
     
     private class ShortSequenceIterator implements Iterator<ShortSymbol>{
         private int i=0;

         @Override
         public boolean hasNext() {
             return i< getLength();
         }
         @Override
         public ShortSymbol next() {
             ShortSymbol next = get(i);
             i++;
             return next;
         }

         @Override
         public void remove() {
             throw new UnsupportedOperationException("can not remove shorts");
             
         }
         
     }
}
