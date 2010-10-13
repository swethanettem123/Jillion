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
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.util.Collections;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultCasPlacedRead implements CasPlacedRead{

    private final Read read;
    private final Range validRange;
    private final long startOffset;
    private final SequenceDirection dir;
    public DefaultCasPlacedRead(Read read, long startOffset,Range validRange, SequenceDirection dir){
        this.read= read;
        this.validRange = validRange;
        this.startOffset = startOffset;
        this.dir= dir;
    }
    @Override
    public long getEnd() {
        return startOffset+getLength()-1;
    }
    @Override
    public long getLength() {
        return read.getLength();
    }
    @Override
    public long getStart() {
        return startOffset;
    }
    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return read.getEncodedGlyphs();
    }
    @Override
    public String getId() {
        return read.getId();
    }
    public SequenceDirection getDirection() {
        return dir;
    }
    @Override
    public String toString() {
        return "DefaultCasPlacedRead [startOffset=" + startOffset
                + ", validRange=" + validRange + ", dir=" + dir + ", read="
                + read + "]";
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        
        long validRangeIndex= referenceIndex - getStart();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        checkValidRange(validRangeIndex);
        return getStart() +validRangeIndex;
    }
    private void checkValidRange(long validRangeIndex) {
        if(validRangeIndex <0){
            throw new IllegalArgumentException("reference index refers to index before valid range");
        }
        if(validRangeIndex > getLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return dir;
    }
    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return Collections.emptyMap();
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((read == null) ? 0 : read.hashCode());
        result = prime * result + (int) (startOffset ^ (startOffset >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultCasPlacedRead))
            return false;
        DefaultCasPlacedRead other = (DefaultCasPlacedRead) obj;
        if (read == null) {
            if (other.read != null)
                return false;
        } else if (!read.equals(other.read))
            return false;
        if (startOffset != other.startOffset)
            return false;
        return true;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        Range range= Range.buildRange(getStart(), getEnd());
        Range otherRange = Range.buildRange(o.getStart(), o.getEnd());
        return range.compareTo(otherRange);
    }
   
}
