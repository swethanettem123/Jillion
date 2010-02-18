/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;


public final class SFFUtil {
   private SFFUtil(){}
   public static final Range EMPTY_CLIP = Range.buildRange(CoordinateSystem.RESIDUE_BASED, -1, -1);
   
    public static int caclulatePaddedBytes(int bytesReadInSection){
         final int remainder = bytesReadInSection % 8;
         if(remainder ==0){
             return 0;
         }
        return 8- remainder;
    }

    public static float convertFlowgramValue(short encodedValue){
         return encodedValue / 100F;

    }
    public static List<Integer> computeCalledFlowIndexes(SFFReadData readData){
        final byte[] indexes = readData.getFlowIndexPerBase();
        List<Integer> calledIndexes = new ArrayList<Integer>();
        
        int position=-1;
        int i=0;

        while( i < indexes.length){
            if(indexes[i] != 0){
                position+=IOUtil.convertToUnsignedByte(indexes[i]);
                calledIndexes.add(Integer.valueOf(position));
            }
            i++;
        }
        return calledIndexes;
    }
    public static List<Short> computeValues(SFFReadData readData) {
        final byte[] indexes = readData.getFlowIndexPerBase();
        final short[] encodedValues =readData.getFlowgramValues();
        verifyHashEncodedValues(encodedValues);
        return computeValues(indexes, encodedValues);
    }

    private static List<Short> computeValues(final byte[] indexes,
            final short[] encodedValues) {
        List<Short> values = new ArrayList<Short>();
        // positions are 1-based so start with -1 to compensate.
        int position=-1;
        int i=0;

        while( i < indexes.length){
            if(indexes[i] != 0){
                position+=IOUtil.convertToUnsignedByte(indexes[i]);
                values.add(encodedValues[position]);
            }
            i++;
        }

        return Collections.unmodifiableList(values);
    }

    private static void verifyHashEncodedValues(final short[] encodedValues) {
        if(encodedValues ==null || encodedValues.length==0){
            throw new IllegalArgumentException("read data must contain Flowgram values");
        }
    }
    public static int getReadDataLength(int numberOfFlows, int numberOfBases) {
        return numberOfFlows * 2 + 3*numberOfBases;
        
    }
    public static int getReadDataLengthIncludingPadding(int numberOfFlows, int numberOfBases) {
        int lengthWithoutPadding = getReadDataLength(numberOfFlows, numberOfBases);
        int padding= SFFUtil.caclulatePaddedBytes(lengthWithoutPadding);
        return lengthWithoutPadding+padding;
    }
    
    public static int numberOfIntensities(List<NucleotideGlyph> glyphs){
        int count=0;
        NucleotideGlyph currentBase= null;
        for(NucleotideGlyph glyph : glyphs){
            if(currentBase != glyph){
                currentBase =glyph;
                count++;
            }
        }
        return count;
        
    }
    
    public static Range getTrimRangeFor(Flowgram flowgram){
        Range qualityClip = flowgram.getQualitiesClip();
        Range adapterClip = flowgram.getAdapterClip();
        long numberOfBases = flowgram.getBasecalls().getLength();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getLocalStart(), 
                                adapterClip.getLocalStart()));
        long lastBaseOfInsert = Math.min(
                qualityClip.getLocalEnd()==0?numberOfBases:qualityClip.getLocalEnd(), 
                        adapterClip.getLocalEnd()==0?numberOfBases:adapterClip.getLocalEnd());
        
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
    public static Range getTrimRangeFor(SFFReadHeader readHeader){
        Range qualityClip = readHeader.getQualityClip();
        Range adapterClip = readHeader.getAdapterClip();
        long numberOfBases = readHeader.getNumberOfBases();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getLocalStart(), 
                                adapterClip.getLocalStart()));
        long lastBaseOfInsert = Math.min(
                qualityClip.getLocalEnd()==0?numberOfBases:qualityClip.getLocalEnd(), 
                        adapterClip.getLocalEnd()==0?numberOfBases:adapterClip.getLocalEnd());
        
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
}
