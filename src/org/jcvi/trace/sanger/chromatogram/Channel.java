/*
 * Created on Oct 23, 2007
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.nio.ShortBuffer;
import java.util.Arrays;

import org.jcvi.CommonUtil;
import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.DefaultConfidence;


/**
 * <code>Channel</code> represents the
 * data from a single trace channel (lane).
 * @author dkatzel
 *
 */
public class Channel{

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
       if(obj == this){
           return true;
       }
       if(!(obj instanceof Channel)){
           return false;
       }
       Channel other = (Channel) obj;
      return  CommonUtil.similarTo(getConfidence(), other.getConfidence())
               && similarPositions(other);
    }
    private boolean similarPositions(Channel other){
        return !CommonUtil.onlyOneIsNull(getPositions(), other.getPositions())
        && Arrays.equals(getPositions().array(), other.getPositions().array());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getConfidence() == null? 0: getConfidence().hashCode());
        result = prime * result + ((getPositions() == null) ? 0 :Arrays.hashCode(getPositions().array()));

        return result;
    }
    private Confidence confidence;
    private ShortBuffer positions;
    /**
     * Default constructor, sets confidence and positions to <code>null</code>.
     */
    public Channel() {
        super();
    }



    /**
     * Constructs a newly allocated {@link Channel} with the provided
     * values for confidence and positions.
     * <p>
     *  In other words, this method returns an {@link Channel} object equal to the value of:
     * <br>
     * <code>new Channel(ByteBuffer.wrap(confidence), ShortBuffer.wrap(positions))</code>
     * </p>
     * @param confidence
     * @param positions
     */
    public Channel(byte[] confidence, short[] positions){
        this(new DefaultConfidence(confidence), ShortBuffer.wrap(positions));
    }
    /**
     * Constructs a newly allocated {@link Channel} with the provided
     * values for confidence and positions.
     * @param confidence
     * @param positions
     */
    public Channel(Confidence confidence, ShortBuffer positions) {
        this.confidence = confidence;
        this.positions = positions;
    }
    /**
     * Retrieves the phred Confidence values.
     * @return the confidence
     */
    public Confidence getConfidence() {
        return confidence;
    }
    /**
     * Sets the phred Confidence values.
     * @param confidence the confidence to set
     */
    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }
    /**
     * Retrieves the trace sample position data.
     * @return the positions
     */
    public ShortBuffer getPositions() {
        return positions;
    }
    /**
     * Sets the trace sample position data.
     * @param positions the positions to set
     */
    public void setPositions(ShortBuffer positions) {
        this.positions = positions;
    }


}
