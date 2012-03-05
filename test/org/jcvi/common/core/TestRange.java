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
 * Created on Jul 18, 2007
 *
 * @author dkatzel
 */
package org.jcvi.common.core;



import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.junit.Test;

public class TestRange{
    private Range range = Range.buildRange(1,10);
    private Range emptyRange = Range.buildRange(0, -1);
    @Test
    public void testEquals_null_notEqual(){
        assertFalse(range.equals(null));

    }
    @Test
    public void testEquals_sameRef_notEqual(){
        assertEquals(range,range);
        assertEquals(range.hashCode(),range.hashCode());
    }
    @Test public void testEquals_diffObj_notEqual(){
        final Object object = new Object();
        assertFalse(range.equals(object));
        assertFalse(range.hashCode()==object.hashCode());
    }

    @Test public void testEquals_sameLeftSameRightDiffSystem_notEqual(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,range.getStart(),range.getEnd());
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftSameRight_notEqual(){
        final Range range2 = Range.buildRange(range.getStart()-1,range.getEnd());
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRightDiffSystem_equal(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,range.getStart()+1,range.getEnd()+1);
        assertEquals(range,range2);
        assertEquals(range.hashCode(),range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRightDiffSystem_notEqual(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,range.getStart()+1,range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRight_notEqual(){
        final Range range2 = Range.buildRange(range.getStart(),range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRightDiffSystem_equal(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,range.getStart(),range.getEnd()+1);
        assertEquals(range,range2);
        assertEquals(range.hashCode(),range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRightDiffSystem_notEqual(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,range.getStart(),range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRight_notEqual(){
        final Range range2 = Range.buildRange(range.getStart()+1,range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }


    @Test public void testConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(left,right);
        assertEquals(left,sut.getStart());
        assertEquals(right, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLength(){
        int left = 10;
        int length = 10;

        Range sut = Range.buildRangeOfLength(left,length);
        assertEquals(left,sut.getStart());
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLength_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLength(left,length);
        assertEquals(left,sut.getStart());
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLengthSpaceBasedRange_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLength(Range.CoordinateSystem.SPACE_BASED,left,length);
        assertEquals(left,sut.getStart());
        assertEquals(left+length, sut.getEnd(CoordinateSystem.SPACE_BASED));
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLengthResidueBasedRange_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLength(Range.CoordinateSystem.RESIDUE_BASED,left,length);
        assertEquals(left-1,sut.getStart());
        assertEquals(left,sut.getStart(CoordinateSystem.RESIDUE_BASED));
        assertEquals(left+length-1-1, sut.getEnd());
        assertEquals(left+length-1, sut.getEnd(CoordinateSystem.RESIDUE_BASED));
    }

    
    @Test(expected=IllegalArgumentException.class)
    public void testBuildRangeOfLength_negativeRange(){
        int left = 0;
        int length =-1;

        Range.buildRangeOfLength(left,length);
    }

    @Test
    public void testBuildRangeOfLengthFromEndCoordinate(){
        int right = 19;
        int length = 10;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLengthFromEndCoordinate_emptyRange(){
        int right = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right, sut.getEnd());
    }
    
    @Test
    public void testBuildRangeOfLengthFromEndCoordinateSpaceBased_emptyRange(){
        int right = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(Range.CoordinateSystem.SPACE_BASED,right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right-1, sut.getEnd());
        assertEquals(right, sut.getEnd(CoordinateSystem.RESIDUE_BASED));
    }

    @Test
    public void testBuildRangeOfLengthFromEndCoordinateResidueBased(){
        int right = 19;
        int length = 10;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(Range.CoordinateSystem.RESIDUE_BASED,right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right-1, sut.getEnd());
        assertEquals(right, sut.getEnd(CoordinateSystem.RESIDUE_BASED));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuildRangeOfLengthFromEndCoordinate_negativeRange(){
        int right = 0;
        int length =-1;

        Range.buildRangeOfLengthFromEndCoordinate(right,length);
    }

    @Test public void testZeroBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,left,right);
        assertEquals(left,sut.getStart(CoordinateSystem.ZERO_BASED));
        assertEquals(right, sut.getEnd(CoordinateSystem.ZERO_BASED));
        assertEquals(left,sut.getStart());
        assertEquals(right, sut.getEnd());
    }

    @Test public void testSpaceBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,left,right);
        assertEquals(left,sut.getStart(CoordinateSystem.SPACE_BASED));
        assertEquals(right, sut.getEnd(CoordinateSystem.SPACE_BASED));
        assertEquals(left,sut.getStart());
        assertEquals(right, sut.getEnd()+1);
    }

    @Test public void testResidueBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,left,right);
        assertEquals(left,sut.getStart(CoordinateSystem.RESIDUE_BASED));
        assertEquals(right, sut.getEnd(CoordinateSystem.RESIDUE_BASED));
        assertEquals(left,sut.getStart()+1);
        assertEquals(right, sut.getEnd()+1);
    }

    @Test public void testResidueBaseEmptyRangeConstruction(){
        int left = 1;
        int right =0;

        Range sut = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,left,right);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testSpaceBaseEmptyRangeConstruction(){
        int left = 0;
        int right =0;

        Range sut = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,left,right);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
        assertEquals(sut.hashCode(),emptyRange.hashCode());
    }

    @Test public void testDefaultBuildEmptyRangeConstruction(){

        Range sut = Range.buildEmptyRange();
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testDefaultCoordinateSpecificBuildEmptyRangeConstruction(){
        Range sut = Range.buildEmptyRange(0);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testResidueCoordinateSpecificBuildEmptyRangeConstruction(){
        Range sut = Range.buildEmptyRange(Range.CoordinateSystem.RESIDUE_BASED,1);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testSpaceCoordinateSpecificEmptyRangeConstruction(){
        Range sut = Range.buildEmptyRange(Range.CoordinateSystem.SPACE_BASED,0);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testNonZeroCoordinateSpecificEmptyRangeConstruction(){
        int zeroRangeLocation = 7;
        Range sut = Range.buildEmptyRange(Range.CoordinateSystem.SPACE_BASED,zeroRangeLocation);
        assertTrue(sut.isEmpty());
        assertEquals(sut.getStart(CoordinateSystem.SPACE_BASED),zeroRangeLocation);
        assertEquals(sut.getEnd(CoordinateSystem.SPACE_BASED),zeroRangeLocation);
        assertFalse(sut.equals(emptyRange));
    }

   

    @Test(expected=IllegalArgumentException.class) public void testInvalidRangeConstruction(){
        int left = 0;
        int right =-1;

        Range.buildRange(Range.CoordinateSystem.SPACE_BASED,left,right);
    }

    @Test(expected=IllegalArgumentException.class) public void testConstructor_leftGreaterThanRight_shouldThrowIllegalArgumentException(){
        Range.buildRange(10,0);
    }

    

    @Test public void testzeroToSpaceCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        assertEquals(range.getStart(CoordinateSystem.SPACE_BASED),rangeStart);
        assertEquals(range.getEnd(CoordinateSystem.SPACE_BASED),rangeEnd+1);
    }

    @Test public void testzeroToResidueCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        assertEquals(range.getStart(CoordinateSystem.RESIDUE_BASED),rangeStart+1);
        assertEquals(range.getEnd(CoordinateSystem.RESIDUE_BASED),rangeEnd+1);
    }

   

    @Test public void testSubRangeOf_nullRange_isNotSubRange(){
        assertFalse(range.isSubRangeOf(null));
    }

    @Test public void testSubRangeOf_leftIsSameRightIsLess_isSubRange(){
        Range subRange = Range.buildRange(range.getStart(),range.getEnd()-1);
        assertTrue(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsSameRightIsMore_isNotSubRange(){
        Range subRange = Range.buildRange(range.getStart(),range.getEnd()+1);
        assertFalse(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsLessRightIsMore_isNotSubRange(){
        Range notSubRange = Range.buildRange(range.getStart()-1,range.getEnd()+1);
        assertFalse(notSubRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsLessRightIsLess_isNotSubRange(){
        Range notSubRange = Range.buildRange(range.getStart()-1,range.getEnd()-1);
        assertFalse(notSubRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsLessRightIsSame_isNotSubRange(){
        Range notSubRange = Range.buildRange(range.getStart()-1,range.getEnd());
        assertFalse(notSubRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsMoreRightIsSame_isSubRange(){
        Range subRange = Range.buildRange(range.getStart()+1,range.getEnd());
        assertTrue(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsMoreRightIsLess_isSubRange(){
        Range subRange = Range.buildRange(range.getStart()+1,range.getEnd()-1);

        assertTrue(subRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsMoreRightIsMore_isNotSubRange(){
        Range subRange = Range.buildRange(range.getStart()+1,range.getEnd()+1);

        assertFalse(subRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsSameRightIsSame_isSubRange()
    {
        assertTrue(range.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_emptyRange()
    {
        assertFalse(range.isSubRangeOf(emptyRange));
    }

    @Test public void testSubRangeOf_realRangeVsEmptyRange_isSubRange()
    {
        assertFalse(emptyRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_emptyRangeVsEmptyRange_isSubRange()
    {
        assertTrue(emptyRange.isSubRangeOf(emptyRange));
    }

    @Test public void testSize(){
        long expectedLength = range.getEnd()-range.getStart()+1;
        long actualLength = range.size();
        assertEquals(expectedLength,actualLength);
    }

    @Test public void testSize_sameLeftAndRight_sizeIsOne(){
        Range oneRange = Range.buildRange(5,5);
        assertEquals(1, oneRange.size());
    }

    @Test public void testSize_leftAndRightAreZero_sizeIsOne(){
        Range zeroRange = Range.buildRange(0,0);
        assertEquals(1, zeroRange.size());
    }
    
    @Test
    public void intersectsSingleCoordinate(){
        assertTrue(range.intersects(5));
    }
    @Test
    public void intersectsSingleCoordinateBeforeRangeShouldNotIntersect(){
        assertFalse(range.intersects(0));
    }
    @Test
    public void intersectsSingleCoordinateAfterRangeShouldNotIntersect(){
        assertFalse(range.intersects(range.getEnd()+1));
    }
    @Test public void testIntersects()
    {
        Range target = Range.buildRange(5, 15);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_doesntReallyIntersect()
    {
        Range target = Range.buildRange(15,25);
        assertFalse(this.range.intersects(target));
    }

    @Test public void testIntersects_barelyIntersectsStart()
    {
        Range target = Range.buildRange(-10, 1);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_barelyIntersectsEnd()
    {
        Range target = Range.buildRange(10, 12);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_subRange()
    {
        Range target = Range.buildRange(5, 7);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_sameRange()
    {
        assertTrue(this.range.intersects(this.range));
    }
    @Test public void testInstersects_emptyRange_shouldReturnFalse(){
        assertFalse(this.range.intersects(emptyRange));
    }
    @Test public void testEndsBefore_emptyRange_shouldReturnFalse(){
        assertFalse(this.range.endsBefore(emptyRange));
    }
    @Test public void testStartsBefore_emptyRange_shouldReturnFalse(){
        assertFalse(this.range.startsBefore(emptyRange));
    }

    @Test public void testIntersects_null()
    {
        try
        {
            this.range.intersects(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

  
    @Test public void testIntersection_normal()
    {
        Range target = Range.buildRange(5,15);
        assertEquals(Range.buildRange(5, 10), this.range.intersection(target));
    }

    @Test public void testIntersection_subrange()
    {
        Range target = Range.buildRange(5,7);
        assertEquals(target, this.range.intersection(target));
    }

    @Test public void testIntersection_superrange()
    {
        Range target = Range.buildRange(-4, 20);
        assertEquals(this.range, this.range.intersection(target));
    }

    @Test public void testIntersection_onePointIntersectStart()
    {
        Range target = Range.buildRange(-4, 1);
        assertEquals(Range.buildRange(1, 1), this.range.intersection(target));
    }

    @Test public void testIntersection_onePointIntersectEnd()
    {
        Range target = Range.buildRange(10, 12);
        assertEquals(Range.buildRange(10, 10), this.range.intersection(target));
    }

    @Test public void testIntersection_empty()
    {
        assertEquals(emptyRange, this.range.intersection(emptyRange));
    }

    @Test public void testIntersection_nointersection()
    {
        Range target = Range.buildRange(15,25);
        assertEquals(emptyRange, this.range.intersection(target));
    }
    @Test public void testIntersection_self()
    {
        assertEquals(this.range, this.range.intersection(this.range));
    }

    @Test public void testIntersection_null()
    {
        try
        {
            this.range.intersection(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

    @Test public void testStartsBefore()
    {
        Range target = Range.buildRange(15,25);
        assertTrue(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_false()
    {
        Range target = Range.buildRange(-5, 10);
        assertFalse(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_sameStart()
    {
        Range target = Range.buildRange(1, 15);
        assertFalse(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_sameRange()
    {
        assertFalse(this.range.startsBefore(this.range));
    }

    @Test public void testStartsBefore_null()
    {
        try
        {
            this.range.startsBefore(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

    @Test public void testEndsBefore()
    {
        Range target = Range.buildRange(12,20);
        assertTrue(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_false()
    {
        Range target = Range.buildRange(-5, 8);
        assertFalse(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_sameEnd()
    {
        Range target = Range.buildRange(5, 10);
        assertFalse(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_sameRange()
    {
        assertFalse(this.range.endsBefore(this.range));
    }

    @Test public void testEndsBefore_null()
    {
        try
        {
            this.range.endsBefore(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

   

    @Test public void testToString()
    {
        assertEquals("[ 1 - 10 ]/0B", this.range.toString());
    }
    @Test public void testToStringResidueBasedCoordinate()
    {
        assertEquals("[ 2 - 11 ]/RB", this.range.toString(CoordinateSystem.RESIDUE_BASED));
    }
    @Test public void testToStringSpacedBasedCoordinate()
    {
        assertEquals("[ 1 - 11 ]/SB", this.range.toString(CoordinateSystem.SPACE_BASED));
    }
    @Test public void testToStringZeroBased()
    {
        assertEquals("[ 1 - 10 ]/0B", this.range.toString(CoordinateSystem.ZERO_BASED));
    }
    private String convertIntoString(Object left, Object right, String seperator){
        StringBuilder result = new StringBuilder();
        result.append(left);
        result.append(seperator);
        result.append(right);
        return result.toString();
    }
    @Test
    public void validDotParse(){
        validParse("\t..  ");
    }
    @Test
    public void invalidDotParseShouldThrowIllegalArgumentException(){
        invalidParseShouldFail("..");
    }
    @Test
    public void validDashParse(){
        validParse("\t-  ");
    }
    @Test
    public void invalidDashParseShouldThrowIllegalArgumentException(){
        invalidParseShouldFail("-");
    }
    @Test
    public void validCommaParse(){
        validParse("\t,  ");
    }
    @Test
    public void invalidCommaParseShouldThrowIllegalArgumentException(){
        invalidParseShouldFail(",");
    }
    
    @Test
    public void invalidParseNotARangeAtAll(){
        assertParseShouldFail("notARange");
    }
    private void validParse(String sep) {
        long start = 15;
        long end = 45;
        final Range expected = Range.buildRange(start, end);
        assertEquals(expected,Range.parseRange(convertIntoString(start,end,sep)));
        assertEquals(expected,Range.parseRange(convertIntoString(start,end," "+sep+"\t")));
    }
    
  
    private void invalidParseShouldFail(final String sep) {
        long start = 15;
        assertParseShouldFail(convertIntoString(start,"notANumber",sep));
        assertParseShouldFail(convertIntoString(start,"notANumber"," "+sep+"\t"));
    }
    private void assertParseShouldFail(String asString) {
        try{            
            Range.parseRange(asString);
            fail("shouldthrow IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("can not parse "+ asString +" into a Range", e.getMessage());
        }
    }
    @Test
    public void buildRange(){
        assertEquals(range, Range.buildRange(range.getStart(), range.getEnd()));
    }
    @Test
    public void buildRangeWithCoordinateSystem(){
        assertEquals(range, Range.buildRange(CoordinateSystem.RESIDUE_BASED,range.getStart()+1, range.getEnd()+1));
    }
    @Test(expected = NullPointerException.class)
    public void buildRangeWithNullCoordinateSystemShouldThrowNPE(){
        Range.buildRange(null,range.getStart()+1, range.getEnd()+1);
    }
    @Test(expected = NullPointerException.class)
    public void buildEmptyRangeWithNullCoordinateSystemShouldThrowNPE(){
        Range.buildEmptyRange(null,range.getStart()+1);
    }
    @Test
    public void buildEmptyRange(){
        Range emptyRange = Range.buildRange(10, 9);
        assertEquals(10, emptyRange.getStart());
        assertEquals(9, emptyRange.getEnd());
        assertTrue(emptyRange.isEmpty());
    }
    
    @Test
    public void buildInclusiveRangesEmptyListShouldReturnEmptyRange(){
        Range emptyRange = Range.buildInclusiveRange(Collections.<Range>emptyList());
        assertEquals(0, emptyRange.getStart());
        assertEquals(-1, emptyRange.getEnd());
        assertTrue(emptyRange.isEmpty());
    }
    
    @Test
    public void buildInclusiveRange(){
        List<Range> ranges = Arrays.asList(
                    Range.buildRange(10, 20),
                    Range.buildRange( 50, 100),
                    Range.buildRange( -5, 3)
                    );
        Range expected = Range.buildRange(-5,100);
        assertEquals(expected, Range.buildInclusiveRange(ranges));
    }
    
    @Test
    public void shiftRight(){
        int units = 5;
        Range shifted = range.shiftRight(units);
        assertEquals(range.getStart()+units, shifted.getStart());
        assertEquals(range.getEnd()+units, shifted.getEnd());
        assertEquals(range.size(), shifted.size());
        
    }
    @Test
    public void shiftLeft(){
        int units = 5;
        Range shifted = range.shiftLeft(units);
        assertEquals(range.getStart()-units, shifted.getStart());
        assertEquals(range.getEnd()-units, shifted.getEnd());
        assertEquals(range.size(), shifted.size());
        
    }

    @Test
    public void mergeEmpty(){
        assertTrue(Range.mergeRanges(Collections.<Range>emptyList()).isEmpty());
    }
    
    @Test
    public void mergeOneRange(){
        
        final List<Range> oneRange = Arrays.asList(range);
        assertEquals(
                oneRange,
                Range.mergeRanges(oneRange));
    }
    
    @Test
    public void mergeTwoRangesNoOverlapShouldReturnTwoRanges(){
        Range nonOverlappingRange = Range.buildRange(12, 20);
        List<Range> nonOverlappingRanges = Arrays.asList(range,nonOverlappingRange);
        assertEquals(
                nonOverlappingRanges,
                Range.mergeRanges(nonOverlappingRanges));
        
    }
    
    @Test
    public void mergeTwoRanges(){
        Range overlappingRange = Range.buildRange(5, 20);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeTwoAdjacentButNotOverlappingRangesShouldMergeIntoOne(){
        Range adjacentRange = Range.buildRange(11, 20);
        List<Range> rangesToMerge = Arrays.asList(range,adjacentRange);
        List<Range> expectedRanges = Arrays.asList(
                Range.buildRange(range.getStart(), adjacentRange.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(rangesToMerge));
        
    }
    @Test
    public void mergeThreeRanges(){
        Range overlappingRange_1 = Range.buildRange(5, 20);
        Range overlappingRange_2 = Range.buildRange(15, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange_1,overlappingRange_2);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange_2.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeThreeRangesThirdRangeConnectsTwoRangeIslands(){
        Range overlappingRange_2 = Range.buildRange(5, 20);
        Range overlappingRange_1 = Range.buildRange(15, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange_1,overlappingRange_2);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange_1.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeThreeRangesOnlyTwoMerge(){
        Range overlappingRange = Range.buildRange(5, 20);
        Range nonOverlappingRange = Range.buildRange(22, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange,nonOverlappingRange);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange.getEnd()),nonOverlappingRange);
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeThreeConsecutiveRanges(){
        List<Range> consecutiveRanges = Arrays.asList(range,range,range);
        List<Range> expectedRanges = Arrays.asList(range);
        assertEquals(
                expectedRanges,
                Range.mergeRanges(consecutiveRanges));
    }
    private Range createRangeSeparatedFrom(Range range, int distance){
        return Range.buildRangeOfLength(range.getEnd()+ distance, range.size());
    }
    @Test
    public void mergeRightClusteredRanges(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(range.getStart(), clusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeClusteredRangesAbutmentShouldStillMerge(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,clusterDistance+1);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(range.getStart(), clusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeClusteredRangesBeyondClusterShouldNotMerge(){
        int clusterDistance=30;
        Range unclusterableRange = createRangeSeparatedFrom(range,clusterDistance+2);
        List<Range> clusteredRanges = Arrays.asList(range, unclusterableRange);
        assertEquals(
                clusteredRanges,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeLeftClusteredRanges(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,-clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(clusterableRange.getStart(), range.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeThreeClusteredRanges(){
        int clusterDistance=30;
        Range leftClusterableRange = createRangeSeparatedFrom(range,-clusterDistance);
        Range rightClusterableRange = createRangeSeparatedFrom(range,clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, leftClusterableRange,rightClusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(leftClusterableRange.getStart(), rightClusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    
    @Test
    public void mergeWithNegativeClusterDistanceShouldThrowIllegalArgumentException(){
        try{
            Range.mergeRanges(Arrays.asList(range), -1);
            fail("should catch illegal argumentException when cluster distance is -1");
        }
        catch(IllegalArgumentException e){
            assertEquals("cluster distance can not be negative",e.getMessage());
        }
    }
    
    @Test
    public void growRight(){
        Range expected = Range.buildRange(1, 15);
        assertEquals(expected, range.grow(0, 5));
    }
    @Test
    public void growLeft(){
        Range expected = Range.buildRange(-4, 10);
        assertEquals(expected, range.grow(5, 0));
    }
    @Test
    public void grow(){
        Range expected = Range.buildRange(-4, 15);
        assertEquals(expected, range.grow(5, 5));
    }
    
    @Test
    public void shrinkLeft(){
        Range expected = Range.buildRange(6, 10);
        assertEquals(expected, range.shrink(5, 0));
    }
    @Test
    public void shrinkRight(){
        Range expected = Range.buildRange(1, 5);
        assertEquals(expected, range.shrink(0, 5));
    }
    @Test
    public void shrink(){
        Range expected = Range.buildRange(6, 5);
        assertEquals(expected, range.shrink(5, 5));
    }
  
    
    @Test
    public void iterator(){
        Iterator<Long> iter = range.iterator();
        assertTrue(iter.hasNext());
        for(long l = range.getStart(); l<= range.getEnd(); l++){
            assertEquals(Long.valueOf(l), iter.next());
        }
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void complimentNoIntersectionShouldReturnOriginalRange(){
        Range noOverlapRange = range.shiftRight(1000);
        assertEquals(Arrays.asList(range),range.compliment(noOverlapRange));
    }
    
    @Test
    public void complimentOfSubRangeShouldReturn2DisjointRanges(){
        Range subrange = range.shrink(2, 2);
        assertEquals(Arrays.asList(Range.buildRange(range.getStart(),2), Range.buildRange(range.getEnd()-1, range.getEnd())),
                range.compliment(subrange));
    }
    
    @Test
    public void complimentOfSuperRangeShouldReturnEmptyList(){
        Range superRange = range.grow(2, 2);
        assertEquals(Collections.emptyList(), range.compliment(superRange));
    }
    @Test
    public void complimentOfLeftSideShouldReturnArrayOfOneElementContainingRightSide(){
        Range left = range.shrink(0, 2);
        assertEquals(Arrays.asList(Range.buildRange(range.getEnd()-1, range.getEnd())),
                range.compliment(left));
    }
    
    @Test
    public void complimentOfRightSideShouldReturnArrayOfOneElementContainingLeftSide(){
        Range right = range.shrink(2, 0);
        assertEquals(Arrays.asList(Range.buildRange(range.getStart(),2)),
                range.compliment(right));
    }
    
    @Test
    public void splitUnderMaxSplitLengthShouldReturnListContainingSameRange(){
        assertEquals(Arrays.asList(range), range.split(range.getLength()+1));
    }
    @Test
    public void splitInto2Ranges(){
        List<Range> expected = Arrays.asList(
                Range.buildRangeOfLength(range.getStart(), range.getLength()/2),
                Range.buildRange(range.getLength()/2+1, range.getEnd())
        );
        
        assertEquals(expected, range.split(range.getLength()/2));
    }
    @Test
    public void splitInto4Ranges(){
        //range is [1-10]
        List<Range> expected = Arrays.asList(
                Range.buildRange(1,3),
                Range.buildRange(4,6),
                Range.buildRange(7,9),
                Range.buildRange(10,10)
        );
        
        assertEquals(expected, range.split(3));
    }
    
    @Test
    public void mergeIntoClustersEmptyListShouldReturnEmptyList(){
        assertTrue(
                Range.mergeRangesIntoClusters(Collections.<Range>emptyList(), 100)
                .isEmpty());
    }
    
    @Test
    public void mergeIntoClustersOneRangeShouldReturnSameRange(){
        final List<Range> inputList = Arrays.asList(range);
        assertEquals(inputList, Range.mergeRangesIntoClusters(inputList, 100));
    }
    @Test
    public void mergeIntoClusters2RangesFartherAwayThanMaxClusterDistanceSame2Ranges(){
        int maxClusterDistance=100;
        Range farAwayRange = range.shiftRight(maxClusterDistance+1);
        final List<Range> inputList = Arrays.asList(range,farAwayRange);
        assertEquals(inputList, Range.mergeRangesIntoClusters(inputList, maxClusterDistance));
    }
    @Test
    public void mergeIntoClusters2OverLappingRanges(){
        int maxClusterDistance=100;
        Range overlappingRange = range.shiftRight(5);
        final List<Range> inputList = Arrays.asList(range,overlappingRange);
        final List<Range> expectedList = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange.getEnd()));
        assertEquals(expectedList, Range.mergeRangesIntoClusters(inputList, maxClusterDistance));
    }
    @Test
    public void mergeIntoClusters3OverLappingRanges(){
        int maxClusterDistance=100;
        Range overlappingRange = range.shiftRight(5);
        Range overlappingRange2 = overlappingRange.shiftRight(10);
        final List<Range> inputList = Arrays.asList(range,overlappingRange,overlappingRange2);
        final List<Range> expectedList = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange2.getEnd()));
        assertEquals(expectedList, Range.mergeRangesIntoClusters(inputList, maxClusterDistance));
    }
    
    @Test
    public void mergeIntoClustersWhenRangeIsLongerThanClusterDistanceShouldSplit(){
        int maxClusterDistance=100;
        Range range = Range.buildRange(0,10);
        //range [10,110] -> [10, 109][110-110]
        Range hugeRange = Range.buildRange(10,110);
       
        final List<Range> inputList = Arrays.asList(range,hugeRange);
        final List<Range> expectedList = Arrays.asList(
                Range.buildRange(0,99),
                Range.buildRange(100,110)
            );
        assertEquals(expectedList, Range.mergeRangesIntoClusters(inputList, maxClusterDistance));
    }
    
    @Test
    public void mergeIntoClustersReSplitHugeRangeToMakeMoreEfficentClusters(){
        int maxClusterDistance=100;
        Range range = Range.buildRange(0,10);
        Range hugeRange = Range.buildRange(10,110);
        Range range2 = Range.buildRange(10,20);
        Range range3 = Range.buildRange(108,120);
        final List<Range> inputList = Arrays.asList(range,range2,range3,hugeRange);
        final List<Range> expectedList = Arrays.asList(
                Range.buildRange(0,99),
                Range.buildRange(100,120)
            );
        assertEquals(expectedList, Range.mergeRangesIntoClusters(inputList, maxClusterDistance));
    }
   
    @Test
    public void byteRangeWithByteLength(){
    	Range r = Range.buildRange(12,123);
    	assertEquals(12, r.getStart());
    	assertEquals(123, r.getEnd());
    	assertEquals(112, r.getLength());
    }
    @Test
    public void byteRangeWithUnsignedByteLength(){
    	Range r = Range.buildRange(12,223);
    	assertEquals(12, r.getStart());
    	assertEquals(223, r.getEnd());
    	assertEquals(212, r.getLength());
    }
    @Test
    public void byteRangeWithShortLength(){
    	Range r = Range.buildRange(0,499);
    	assertEquals(0, r.getStart());
    	assertEquals(499, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    @Test
    public void byteRangeWithUnsignedShortLength(){
    	Range r = Range.buildRange(0,59999);
    	assertEquals(0, r.getStart());
    	assertEquals(59999, r.getEnd());
    	assertEquals(60000, r.getLength());
    }
    
    @Test
    public void byteRangeWithIntLength(){
    	Range r = Range.buildRange(0,99999);
    	assertEquals(0, r.getStart());
    	assertEquals(99999, r.getEnd());
    	assertEquals(100000, r.getLength());
    }
    @Test
    public void byteRangeWithUnsignedIntLength(){
    	long end = Integer.MAX_VALUE+1L;
    	Range r = Range.buildRange(0,end);
    	assertEquals(0, r.getStart());
    	assertEquals(end, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+2L, r.getLength());
    }
    @Test
    public void byteRangeWithLongLength(){
    	long end = Long.MAX_VALUE-1L;
    	Range r = Range.buildRange(0,end);
    	assertEquals(0, r.getStart());
    	assertEquals(end, r.getEnd());
    	assertEquals(Long.MAX_VALUE, r.getLength());
    }
    
    @Test
    public void unsignedByteWithShortLength(){
    	Range r = Range.buildRangeOfLength(Byte.MAX_VALUE+1,500);
    	assertEquals(Byte.MAX_VALUE+1, r.getStart());
    	assertEquals(Byte.MAX_VALUE+500, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void unsignedByteWithUnsignedShortLength(){
    	Range r = Range.buildRangeOfLength(Byte.MAX_VALUE+1,Short.MAX_VALUE+1);
    	assertEquals(Byte.MAX_VALUE+1, r.getStart());
    	assertEquals(Byte.MAX_VALUE+Short.MAX_VALUE+1, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void unsignedByteWithIntLength(){
    	Range r = Range.buildRangeOfLength(Byte.MAX_VALUE+1,Integer.MAX_VALUE);
    	assertEquals(Byte.MAX_VALUE+1, r.getStart());
    	assertEquals(Byte.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void unsignedByteWithUnsignedIntLength(){
    	Range r = Range.buildRangeOfLength(Byte.MAX_VALUE+1,Integer.MAX_VALUE+1L);
    	assertEquals(Byte.MAX_VALUE+1, r.getStart());
    	assertEquals(Byte.MAX_VALUE+(long)Integer.MAX_VALUE+1L, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void unsignedByteWithLongLength(){
    	Range r = Range.buildRangeOfLength(Byte.MAX_VALUE+1,0x100000000L);
    	assertEquals(Byte.MAX_VALUE+1, r.getStart());
    	assertEquals(4294967423L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    //////////////////////////
    @Test
    public void shortWithShortLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE,500);
    	assertEquals(Short.MAX_VALUE, r.getStart());
    	assertEquals(Short.MAX_VALUE+499, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void shortWithUnsignedShortLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE,Short.MAX_VALUE+1);
    	assertEquals(Short.MAX_VALUE, r.getStart());
    	assertEquals(Short.MAX_VALUE+Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void shortWithIntLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE,Integer.MAX_VALUE);
    	assertEquals(Short.MAX_VALUE, r.getStart());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE-1, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void shortWithUnsignedIntLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE,Integer.MAX_VALUE+1L);
    	assertEquals(Short.MAX_VALUE, r.getStart());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void shortWithLongLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE,0x100000000L);
    	assertEquals(Short.MAX_VALUE, r.getStart());
    	assertEquals(4295000062L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    /////////////////////////////////
    @Test
    public void intWithShortLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE,500);
    	assertEquals(Integer.MAX_VALUE, r.getStart());
    	assertEquals(Integer.MAX_VALUE+499L, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void intWithUnsignedShortLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE,Short.MAX_VALUE+1);
    	assertEquals(Integer.MAX_VALUE, r.getStart());
    	assertEquals(Integer.MAX_VALUE+(long)Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void intWithIntLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE,Integer.MAX_VALUE);
    	assertEquals(Integer.MAX_VALUE, r.getStart());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE-1, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void intWithUnsignedIntLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE,Integer.MAX_VALUE+1L);
    	assertEquals(Integer.MAX_VALUE, r.getStart());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void intWithLongLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE,0x100000000L);
    	assertEquals(Integer.MAX_VALUE, r.getStart());
    	assertEquals(6442450942L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    ////////////////////////////////
    @Test
    public void unsignedShortWithShortLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE+1,500);
    	assertEquals(Short.MAX_VALUE+1, r.getStart());
    	assertEquals(Short.MAX_VALUE+1+499, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void unsignedShortWithUnsignedShortLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE+1,Short.MAX_VALUE+1);
    	assertEquals(Short.MAX_VALUE+1, r.getStart());
    	assertEquals(Short.MAX_VALUE+1+Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void unsignedShortWithIntLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE+1,Integer.MAX_VALUE);
    	assertEquals(Short.MAX_VALUE+1, r.getStart());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void unsignedShortWithUnsignedIntLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE+1,Integer.MAX_VALUE+1L);
    	assertEquals(Short.MAX_VALUE+1, r.getStart());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE+1, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void unsignedShortWithLongLength(){
    	Range r = Range.buildRangeOfLength(Short.MAX_VALUE+1,0x100000000L);
    	assertEquals(Short.MAX_VALUE+1, r.getStart());
    	assertEquals(4295000063L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    ///////////////////////
    @Test
    public void unsignedIntWithShortLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE+1L,500);
    	assertEquals(Integer.MAX_VALUE+1L, r.getStart());
    	assertEquals(Integer.MAX_VALUE+1L+499L, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void unsignedIntWithUnsignedShortLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE+1L,Short.MAX_VALUE+1);
    	assertEquals(Integer.MAX_VALUE+1L, r.getStart());
    	assertEquals(Integer.MAX_VALUE+1L+(long)Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void unsignedIntWithIntLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE+1L,Integer.MAX_VALUE);
    	assertEquals(Integer.MAX_VALUE+1L, r.getStart());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void unsignedIntWithUnsignedIntLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE+1L,Integer.MAX_VALUE+1L);
    	assertEquals(Integer.MAX_VALUE+1L, r.getStart());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE+1L, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }   
    @Test
    public void unsignedIntWithLongLength(){
    	Range r = Range.buildRangeOfLength(Integer.MAX_VALUE+1L,0x100000000L);
    	assertEquals(Integer.MAX_VALUE+1L, r.getStart());
    	assertEquals(6442450943L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    
    /////////////////////////////////
    
	@Test
	public void longWithShortLength() {
		Range r = Range.buildRangeOfLength(0x100000000L, 500);
		assertEquals(0x100000000L, r.getStart());
		assertEquals(4294967795L, r.getEnd());
		assertEquals(500, r.getLength());
	}

	@Test
	public void longWithUnsignedShortLength() {
		Range r = Range.buildRangeOfLength(0x100000000L, Short.MAX_VALUE + 1);
		assertEquals(0x100000000L, r.getStart());
		assertEquals(4295000063L, r.getEnd());
		assertEquals(Short.MAX_VALUE + 1, r.getLength());
	}

	@Test
	public void longWithIntLength() {
		Range r = Range
				.buildRangeOfLength(0x100000000L, Integer.MAX_VALUE);
		assertEquals(0x100000000L, r.getStart());
		assertEquals(6442450942L, r.getEnd());
		assertEquals(Integer.MAX_VALUE, r.getLength());
	}

	@Test
	public void longWithUnsignedIntLength() {
		Range r = Range.buildRangeOfLength(0x100000000L,
				Integer.MAX_VALUE + 1L);
		assertEquals(0x100000000L, r.getStart());
		assertEquals(6442450943L, r.getEnd());
		assertEquals(Integer.MAX_VALUE + 1L, r.getLength());
	}

    @Test
    public void emptyRangeWithNegativeCoordinate(){
    	Range r = Range.buildEmptyRange(-1);
    	assertEquals(-1, r.getStart());
    	assertTrue(r.isEmpty());
    	assertEquals(-2, r.getEnd());
    }
    @Test
    public void emptyRangeWithNegativeShortValueCoordinate(){
    	Range r = Range.buildEmptyRange(Short.MIN_VALUE);
    	assertEquals(Short.MIN_VALUE, r.getStart());
    	assertTrue(r.isEmpty());
    	assertEquals(Short.MIN_VALUE -1, r.getEnd());
    }
    @Test
    public void emptyRangeWithNegativeIntValueCoordinate(){
    	Range r = Range.buildEmptyRange(Integer.MIN_VALUE);
    	assertEquals(Integer.MIN_VALUE, r.getStart());
    	assertTrue(r.isEmpty());
    	assertEquals(Integer.MIN_VALUE -1L, r.getEnd());
    }
    @Test
    public void emptyRangeWithShortValueCoordinate(){
    	Range r = Range.buildEmptyRange(Short.MAX_VALUE);
    	assertEquals(Short.MAX_VALUE, r.getStart());
    	assertTrue(r.isEmpty());
    	assertEquals(Short.MAX_VALUE -1, r.getEnd());
    }
    @Test
    public void emptyRangeWithIntValueCoordinate(){
    	Range r = Range.buildEmptyRange(Integer.MAX_VALUE);
    	assertEquals(Integer.MAX_VALUE, r.getStart());
    	assertTrue(r.isEmpty());
    	assertEquals(Integer.MAX_VALUE -1L, r.getEnd());
    }
    @Test
    public void emptyRangeWithLongValueCoordinate(){
    	Range r = Range.buildEmptyRange(Long.MAX_VALUE);
    	assertEquals(Long.MAX_VALUE, r.getStart());
    	assertTrue(r.isEmpty());
    	assertEquals(Long.MAX_VALUE -1L, r.getEnd());
    }
    
}
