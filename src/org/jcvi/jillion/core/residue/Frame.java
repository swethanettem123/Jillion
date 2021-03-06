/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.Triplet;
import org.jcvi.jillion.core.util.iter.IteratorUtil;

public enum Frame{
    ONE(1),
    TWO(2),
    THREE(3),
    
    
    NEGATIVE_ONE(-1){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    },
    NEGATIVE_TWO(-2){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    },
    NEGATIVE_THREE(-3){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    }
    ;
    
    private int frame;
    
    private static final Frame[] VALUES = values();
    //need ArrayList to get the rollover iterator to work
    //since we need marker interface RandomAccess
    private static final ArrayList<Frame> FORWARDS = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
    private static final ArrayList<Frame> REVERSE = new ArrayList<>(Arrays.asList(NEGATIVE_ONE, NEGATIVE_TWO, NEGATIVE_THREE));
    
    public  final int getFrame() {
        return frame;
    }
    Frame(int frame){
        this.frame = frame;
    }
    
    public Frame shift(int amount){
        
        if(amount <1){
            throw new IllegalArgumentException("amount must be positive");
        }
        int shift = amount %3;
        Iterator<Frame> rollOverIter;
        if(ordinal() < 3){
            rollOverIter = IteratorUtil.rollover(FORWARDS, ordinal()+1);
        }else{
            rollOverIter = IteratorUtil.rollover(REVERSE, ordinal()-2);
        }
        Frame f = this;
        for(int i=0; i< shift; i++){
            f = rollOverIter.next();
        }
        return f;
    }
    /**
     * Parse a {@link Frame} from the given int value.
     * Valid values are <code>-3</code> to <code>3</code>
     * inclusive excluding 0.
     * @param frame the frame number as an int (1, 2, 3, -1, -2, -3).
     * 
     * @return a {@link Frame}
     * @throws IllegalArgumentException if <code> frame &lt; 0 || frame &gt; 2</code>
     */
    public static Frame parseFrame(int frame){
        for(Frame f : Frame.values()){
            if(f.frame == frame){
                return f;
            }
        }
     
        throw new IllegalArgumentException("unable to parse frame " + frame);
    }
    
    
    public static List<Frame> forwardFrames(){
        return FORWARDS;
    }
    
    public static List<Frame> reverseFrames(){
        return REVERSE;
    }
    public boolean onReverseStrand() {
        // overridden by -1, -2 and -3
        return false;
    }
    
    public Frame getOppositeFrame(){
        return VALUES[ (this.ordinal() +3)%VALUES.length];
    }
    
    public Iterator<Triplet> asTriplets(NucleotideSequence sequence){
        Iterator<Nucleotide> iter = handleFrame(sequence, this);
        return new Iterator<Triplet>() {

            Triplet next;
            {
                next = getNextTriplet(iter);
            }
            @Override
            public boolean hasNext() {
                return next !=null;
            }

            @Override
            public Triplet next() {
                if(!hasNext()){
                    throw new NullPointerException();
                }
                Triplet ret = next;
                next = getNextTriplet(iter);
                return ret;
            }
        };
    }

    private Triplet getNextTriplet(Iterator<Nucleotide> iter) {

        Nucleotide first = getNextNucleotide(iter);
        Nucleotide second = getNextNucleotide(iter);
        Nucleotide third = getNextNucleotide(iter);
        if (first == null || second == null || third == null) {
            // no more bases
            return null;
        }
        return Triplet.create(first, second, third);
    }

    private Nucleotide getNextNucleotide(Iterator<Nucleotide> iter) {
        if (!iter.hasNext()) {
            return null;
        }
        Nucleotide n = iter.next();
        // if(n.isGap()){
        // throw new IllegalArgumentException("sequence can not contain gaps");
        // }
        return n;
    }
    
    
    @SuppressWarnings("fallthrough")
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
    private Iterator<Nucleotide> handleFrame(NucleotideSequence sequence, Frame frame) {
        Iterator<Nucleotide> iter;
        if(frame.onReverseStrand()){
            iter = sequence.toBuilder().reverseComplement().iterator();
          //switch uses fall through
            //so frame 2 skips first 2 bp           
            switch(frame){
                    case NEGATIVE_THREE:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                    case NEGATIVE_TWO:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                                    break;
                    default:
                                    //no-op
                            break;
            }
        }else{
            iter = sequence.iterator();
            //switch uses fall through
            //so frame 2 skips first 2 bp           
            switch(frame){
                    case THREE:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                    case TWO:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                                    break;
                    default:
                                    //no-op
                            break;
            }
        }
        return iter;
    }
}
