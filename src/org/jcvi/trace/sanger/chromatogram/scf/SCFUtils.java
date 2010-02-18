/*
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

import org.jcvi.trace.sanger.chromatogram.scf.section.Section;

/**
 * <code>SCFUtils</code> is an Utility class
 * that contains helper methods for SCF Parsing
 * and Encoding.
 * @author dkatzel
 *
 *
 */
public final class SCFUtils {

    /**
     * private constructor so no one can create it.
     */
    private SCFUtils(){}
    /**
     * This is the size in bytes of the SCF Header
     * as defined in the SCF File Format Specification.
     */
    public static final int HEADER_SIZE = 128;
    /**
     * This is the order the {@link Section}s
     * should be encoded in a SCF File.
     */
    public static final List<Section> ORDER_OF_SECTIONS =
                                                Arrays.asList(Section.SAMPLES,
                                                Section.BASES,
                                                Section.COMMENTS,
                                                Section.PRIVATE_DATA);



    /**
     * my own implementation of SCF's delta delta algorithm.
     * <br/>psuedo code:
     * <pre>position[i]+= 2*position[i-1]-position[i-2]</pre>
     * I think mine is more clear than the "fast" version provided
     * in IO_Lib's misc_scf.c which is:
     * <pre>
        uint_2 p_sample1, p_sample2;
        p_sample1 = p_sample2 = 0;
        for (i = 0; i < num_samples; i++) {
            &nbsp;&nbsp;&nbsp;p_sample1  = p_sample1 + samples[i];
            &nbsp;&nbsp;&nbsp;samples[i] = p_sample1 + p_sample2;
            &nbsp;&nbsp;&nbsp;p_sample2  = samples[i];
        }
        </pre>
     * @param positions
     */
    public static void deltaDeltaDecode(short[] positions) {
        //special cases when i<2;
        // i=0 is not changed
        //i=1 can only take into account the previous
        //position since there is no index =-1
        positions[1]+= 2*positions[0];
        //now handle where i>=2
        for(int i=2; i<positions.length; i++){
            positions[i] += (short)(2*positions[i-1]-positions[i-2]);
        }
    }

    public static ShortBuffer deltaDeltaEncode(ShortBuffer original){
        ShortBuffer buffer=copy(original);
        for(int i=buffer.limit()-1; i>1; i--){
            final short deltaDelta = (short)(buffer.get(i) - 2*buffer.get(i-1)+buffer.get(i-2));
            buffer.put(i, deltaDelta);
        }
        //special case i=1
        buffer.put(1, (short)(buffer.get(1) - 2*buffer.get(0)));
        //leave i=0 as is
        //reset position to beginning.
         return (ShortBuffer)buffer.rewind();
    }

    public static short[] deltaDeltaEncode(short[] original){
        return deltaDeltaEncode(ShortBuffer.wrap(original)).array();
    }

    public static ShortBuffer copy(ShortBuffer original) {
        ShortBuffer aCopy=ShortBuffer.allocate(original.remaining());
        aCopy.put(original);
        return (ShortBuffer)aCopy.rewind();
    }

}
