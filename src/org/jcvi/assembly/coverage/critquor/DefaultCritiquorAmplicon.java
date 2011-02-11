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
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import org.jcvi.Range;

public class DefaultCritiquorAmplicon implements CritiquorAmplicon {
    private final String id;
    private final String region;
    private final Range range;
    private final String forwardPrimerSequence;
    private final String reversePrimerSequence;
    /**
     * @param id
     * @param region
     * @param range
     * @param forwardPrimerSequence
     * @param reversePrimerSequence
     */
    public DefaultCritiquorAmplicon(String id, String region, Range range,
            String forwardPrimerSequence, String reversePrimerSequence) {
        this.id = id;
        this.region = region;
        this.range = range;
        this.forwardPrimerSequence = forwardPrimerSequence;
        this.reversePrimerSequence = reversePrimerSequence;
    }
    public String getId() {
        return id;
    }
    public String getRegion() {
        return region;
    }
    public String getForwardPrimerSequence() {
        return forwardPrimerSequence;
    }
    public String getReversePrimerSequence() {
        return reversePrimerSequence;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((forwardPrimerSequence == null) ? 0 : forwardPrimerSequence
                        .hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((range == null) ? 0 : range.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime
                * result
                + ((reversePrimerSequence == null) ? 0 : reversePrimerSequence
                        .hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultCritiquorAmplicon))
            return false;
        DefaultCritiquorAmplicon other = (DefaultCritiquorAmplicon) obj;
        if (forwardPrimerSequence == null) {
            if (other.forwardPrimerSequence != null)
                return false;
        } else if (!forwardPrimerSequence.equals(other.forwardPrimerSequence))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (range == null) {
            if (other.range != null)
                return false;
        } else if (!range.equals(other.range))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (reversePrimerSequence == null) {
            if (other.reversePrimerSequence != null)
                return false;
        } else if (!reversePrimerSequence.equals(other.reversePrimerSequence))
            return false;
        return true;
    }
    @Override
    public long getEnd() {
        return range.getEnd();
    }
    @Override
    public long getLength() {
        return range.size();
    }
    @Override
    public long getStart() {
        return range.getStart();
    }
    @Override
    public String toString() {
        return "DefaultCritiquorAmplicon [forwardPrimerSequence="
                + forwardPrimerSequence + ", id=" + id + ", range=" + range
                + ", region=" + region + ", reversePrimerSequence="
                + reversePrimerSequence + "]";
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(CritiquorAmplicon o) {
        return asRange().compareTo(o.asRange());
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return range;
    }
    
    
    
}
