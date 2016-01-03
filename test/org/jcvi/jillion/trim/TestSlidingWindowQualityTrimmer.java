package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestSlidingWindowQualityTrimmer {
    private final SlidingWindowQualityTrimmer sut = new SlidingWindowQualityTrimmer(4, PhredQuality.valueOf(15));
    
    
    private QualitySequenceBuilder quals;
    private Range expectedTrimRange;
    
    @Parameters(name = "{0}")
    public static List<Object[]> data(){
        List<Object[]> list = new ArrayList<Object[]>();
        //
        list.add( new Object[]{"zeroLengthSequenceShouldReturnEmpty", new byte[0], Range.ofLength(0)});
        list.add( new Object[]{"wholeSequenceAboveMinThresholdShouldReturnWholeLength", new byte[]{20,20,20,20,20,20,20,20}, Range.ofLength(8)});
        list.add( new Object[]{"wholeSequenceBelowMinThresholdShouldReturnEmptyRange", new byte[]{12,12,12,12,12,12,12,12,12,12}, Range.ofLength(0)});
        list.add( new Object[]{"startsOffGoodThenGoesBadShouldReturnGoodRange", new byte[]{20,20,20,20,20,20,20,20,12,12,12,12}, Range.ofLength(8)});
        list.add( new Object[]{"lastWindowNotAllBad", new byte[]{20,20,20,20,20,20,20,20,20,12,12,12}, Range.ofLength(9)});
        list.add( new Object[]{"lastWindowNotAllBad2", new byte[]{20,20,20,20,20,20,20,20,20,20,12,12}, Range.ofLength(10)});
        list.add( new Object[]{"lastWindowHasInnerBadBase", new byte[]{20,20,20,20,20,20,20,20,20,20,9,15}, Range.ofLength(12)});
        
        list.add( new Object[]{"innerBasesDipBelowThresholdStillOK", new byte[]{20,20,14,20,20,20,20,20,20,12,12,12}, Range.ofLength(9)});
        
        
        
        return list;
    }
    
    
    
    public TestSlidingWindowQualityTrimmer(String message, byte[] quals, Range expectedTrimRange) {
        this.quals = quals.length==0? new QualitySequenceBuilder() : new QualitySequenceBuilder(quals);
        this.expectedTrimRange = expectedTrimRange;
    }

    @Test
    public void asQualitySequence(){
        Range actual = sut.trim(quals.build());
        assertEquals(expectedTrimRange, actual);
    }
    
    @Test
    public void asQualityBuilder(){
        Range actual = sut.trim(quals);
        assertEquals(expectedTrimRange, actual);
    }

  
}
