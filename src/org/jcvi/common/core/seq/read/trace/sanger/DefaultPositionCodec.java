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
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.GlyphCodec;


enum DefaultPositionCodec implements GlyphCodec<Position>{

   
    INSTANCE;
   
    @Override
    public List<Position> decode(byte[] encodedGlyphs) {
        List<Position> glyphs = new ArrayList<Position>();
        ShortBuffer buf = ByteBuffer.wrap(encodedGlyphs).asShortBuffer();
        while(buf.hasRemaining()){
            glyphs.add(Position.valueOf(IOUtil.toUnsignedShort(buf.get())));
        }
        return glyphs;
    }

    @Override
    public Position decode(byte[] encodedGlyphs, int index) {
        int indexIntoShortAray = index*2;
        final int hi = encodedGlyphs[indexIntoShortAray]<<8;
        final byte low = encodedGlyphs[indexIntoShortAray+1];
        int value = hi | (low & 0xFF);
        return Position.valueOf(IOUtil.toUnsignedShort((short)value));
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length/2;
    }

    @Override
    public byte[] encode(Collection<Position> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size()*2);
        for(Position g : glyphs){
            buf.putShort(IOUtil.toSignedShort(g.getValue()));
        }
        return buf.array();
    }

}
