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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.Caches;


/**
 * A <code>Range</code> is a pair of coordinate values which describe a
 * contiguous subset of a sequence of values.  <code>Range</code>s are
 * immutable.  Changes to a <code>Range</code> are done using various methods
 * which return different <code>Range</code> instances.
 * <p>
 * <code>Range</code>s have a start (or left) value and an end (or right)
 * value.  The start value will always be less than or equal to the end value.
 * The minimum start value of a Range is {@link Long#MIN_VALUE}  and the max end
 * value of a Range is {@link Long#MAX_VALUE}. Also due to limitations
 * to Java primitives, Ranges can not have a length > {@link Long#MAX_VALUE}.
 *  Any attempt to build Ranges beyond
 * those values will throw Exceptions.
 * <p>
 * The Range coordinates are 0-based inclusive.  Thus, a <code>Range</code>
 * of 20 to 30 has a size of 11, not 10, and a <code>Range</code> of 42 to 42
 * will have a size of 1 not 0.  This is done to conform with the overwhelming
 * majority use of inclusive ranges in Bioinformatics. The implications of this are particularly important when thinking about the
 * desire to represent no range at all.  A <code>Range</code> of 0 to 0 still
 * has a size of 1.  In order to represent a <code>Range</code> with size 0,
 * you need to explicitly use an empty range via the factory methods:
 * {@link #buildEmptyRange()} {@link #buildEmptyRange(long)} {@link #buildEmptyRange(CoordinateSystem, long)}. 
 * <p>
 * Often, Bioinformatics formats use non-0-based coordinates. Other coordinate system start and end values can be queried
 * via the {@link #getStart(CoordinateSystem)} and {@link #getEnd(CoordinateSystem)} methods.  
 * A different {@link CoordinateSystem} can be also be specified at construction time
 * via the {@link Range#buildRange(CoordinateSystem, long, long)} method.  If this method is used,
 * the input values will automatically get converted into 0-based coordinates.
 * <p/>
 * Ranges can be constructed using the various Range.buildRange(...) methods
 * which might return different Range implementations based on 
 * what the input values.  In addition, since Ranges are immutable,
 * it is not guaranteed that the Range object returned by the build methods
 * is a new object since Ranges are often cached (Flyweight pattern).  Therefore;
 * <strong> Range objects should not be used
 * for synchronization locks.</strong>  Range objects are cached and shared, synchronizing
 * on the same object as other, unrelated code can cause deadlock.
 * <pre> 
 * &#047;&#047;don't do this
 * private static Range range = Range.buildRange(0,9);
 * ...
 *   synchronized(range){ .. }
 * ...
 * </pre>
 * @author dkatzel
 * @author jsitz@jcvi.org
 * 
 * @see CoordinateSystem
 * 
 */
public abstract class Range implements Placed<Range>,Iterable<Long>
{
	/**
	 * 2^8 -1.
	 */
	private static final int UNSIGNED_BYTE_MAX = 255;
	/**
	 * 2^16 -1.
	 */
	private static final int UNSIGNED_SHORT_MAX = 65535;
	/**
	 * 2^32 -1.
	 */
	private static final long UNSIGNED_INT_MAX = 4294967295L;
	
    /**
     * {@code Comparators} is an enum of common Range
     * {@link Comparator} implementations.
     * @author dkatzel
     *
     *
     */
    public enum Comparators implements Comparator<Range>{
        /**
         * Compares a pair of {@link Range}s
         * and assigns the lower comparative value to the Range which begins earlier.
         * In the case of two ranges having identical start coordinates, the one
         * with the lower end coordinate (the shorter range) will be ranked lower.
         * 
         * @author jsitz@jcvi.org
         * @author dkatzel
         */
        ARRIVAL{
            @Override
            public int compare(Range first, Range second) 
            {
                /*
                 * We don't accept null values for comparison.
                 */
                if (first == null){
                    throw new NullPointerException("The first parameter in the comparison is null.");
                }
                if (second == null){
                    throw new NullPointerException("The second parameter in the comparison is null.");
                }

                /*
                 * Compare first by the start values, then by the end values, if the ranges start
                 * in the same place.
                 */
                final int startComparison = Long.valueOf(first.getStart()).compareTo(Long.valueOf(second.getStart()));
                if (startComparison == 0)
                {
                    return Long.valueOf(first.getEnd()).compareTo(Long.valueOf(second.getEnd()));
                }
                return startComparison;
            }
        },
        /**
         * Compares a pair of {@link Range}s
         * and assigns the lower comparative value to the Range which ends earlier.
         * In the case of two ranges having identical end coordinates, the one
         * with the lower start coordinate (the longer range) will be ranked lower.
         * 
         * @author jsitz@jcvi.org
         * @author dkatzel
         */
        DEPARTURE{
            @Override
            public int compare(Range first, Range second) 
            {
                /*
                 * We don't accept null values for comparison.
                 */
                if (first == null){
                    throw new NullPointerException("The first parameter in the comparison is null.");
                }
                if (second == null){
                    throw new NullPointerException("The second parameter in the comparison is null.");
                }
                
                /*
                 * Compare first by the end values, then by the start values, if the ranges end
                 * in the same place.
                 */
                final int endComparison = Long.valueOf(first.getEnd()).compareTo(Long.valueOf(second.getEnd()));
                if (endComparison == 0)
                {
                    return Long.valueOf(first.getStart()).compareTo(Long.valueOf(second.getStart()));
                }
                return endComparison;
            }
        },
        /**
         * Compares Ranges by length
         * and orders them longest to shortest. Ranges
         * of the same length are considered equal.
         * @author dkatzel
         */
        LONGEST_TO_SHORTEST{

            @Override
            public int compare(Range o1, Range o2) {
                return -1 * Long.valueOf(o1.getLength()).compareTo(o2.getLength());
            }
            
        },
        /**
         * Compares Ranges by length
         * and orders them shortest to longest.
         * Ranges
         * of the same length are considered equal.
         * @author dkatzel
         */
        SHORTEST_TO_LONGEST{

            @Override
            public int compare(Range o1, Range o2) {
                return Long.valueOf(o1.getLength()).compareTo(o2.getLength());
            }
            
        }
        ;
    }
    /**
     * Enumeration of available range coordinate systems.
     * <p/>
     * Different file formats or conventions use
     * different numbering systems in bioinformatics utilities.
     * All Range objects use the same internal system to be inter-operable
     * but users may want ranges to be input or output into different
     * coordinate systems to fit their needs.  CoordinateSystem implementations
     * can be used to translate to and from the various bioinformatics coordinate
     * systems to simplify working with multiple coordinate systems at the same time.
     * @see Range#getStart(CoordinateSystem)
     * @see Range#getEnd(CoordinateSystem)
     */
    public enum CoordinateSystem {
        /**
         * Zero-based coordinate systems are exactly like
         * array index offsets.  CoordinateSystem starts at 0
         * and the last element in the range has an offset
         * of {@code length() -1}.
         * <pre> 
         * coordinate system    0  1  2  3  4  5
         *                    --|--|--|--|--|--|
         * range elements       0  1  2  3  4  5
         * </pre>
         */
    	ZERO_BASED("Zero Based", "0B", 0, 0, 0, 0),
    	/**
    	 * Residue based coordinate system is a "1s based"
    	 * position system where there first element has a 
    	 * position of 1 and the last element in the range
    	 * as a position of length.
    	 *  <pre> 
         * coordinate system    1  2  3  4  5  6
         *                    --|--|--|--|--|--|
         * range elements       0  1  2  3  4  5
         * </pre>
    	 */
        RESIDUE_BASED("Residue Based", "RB", 1, 1, -1, -1),
        /**
         * Spaced based coordinate systems count the "spaces"
         * between elements.  The first element has a coordinate
         * of 0 while the last element in the range has a position 
         * of length.
         * <pre> 
         * coordinate system   0  1  2  3  4  5  6
         *                    --|--|--|--|--|--|--
         * range elements       0  1  2  3  4  5
         * </pre>
         */
        SPACE_BASED("Space Based", "SB", 0, 1, 0, -1);

        /** The full name used to display this coordinate system. */
        private String displayName;
        
        /** An abbreviated name to use as a printable <code>Range</code> annotation. */
        private String abbreviatedName;

        private long zeroBaseToCoordinateSystemStartAdjustmentValue;
        private long zeroBaseToCoordinateSystemEndAdjustmentValue;

        private long coordinateSystemToZeroBaseStartAdjustmentValue;
        private long coordinateSystemToZeroBaseEndAdjustmentValue;

        /**
         * Builds a <code>CoordinateSystem</code>.
         *
         * @param displayName The full name used to display this coordinate system.
         * @param abbreviatedName An abbreviated name to use as a printable <code>Range</code>
         * annotation.
         * @param zeroBaseToCoordinateSystemStartAdjustmentValue
         * @param zeroBaseToCoordinateSystemEndAdjustmentValue
         * @param coordinateSystemToZeroBaseStartAdjustmentValue
         * @param coordinateSystemToZeroBaseEndAdjustmentValue
         */
        private CoordinateSystem(String displayName,
                                 String abbreviatedName,
                                 long zeroBaseToCoordinateSystemStartAdjustmentValue,
                                 long zeroBaseToCoordinateSystemEndAdjustmentValue,
                                 long coordinateSystemToZeroBaseStartAdjustmentValue,
                                 long coordinateSystemToZeroBaseEndAdjustmentValue) {
            this.displayName = displayName;
            this.abbreviatedName = abbreviatedName;
            this.zeroBaseToCoordinateSystemStartAdjustmentValue = zeroBaseToCoordinateSystemStartAdjustmentValue;
            this.zeroBaseToCoordinateSystemEndAdjustmentValue = zeroBaseToCoordinateSystemEndAdjustmentValue;
            this.coordinateSystemToZeroBaseStartAdjustmentValue = coordinateSystemToZeroBaseStartAdjustmentValue;
            this.coordinateSystemToZeroBaseEndAdjustmentValue = coordinateSystemToZeroBaseEndAdjustmentValue;
        }

        /**
         * Get the shortened "tag" name for this <code>CoordinateSystem</code>.
         * to be used in the toString value.
         * @return A two-letter abbreviation for this <code>CoordinateSystem</code>.
         */
        public String getAbbreviatedName() 
        {
            return abbreviatedName;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public String toString() 
        {
            return displayName;
        }

        /**
         * Get the start coordinate in this system from the 
         * equivalent zero-based start coordinate.
         * @param zeroBasedStart start coordinate in 0-based
         * coordinate system.
         */
        private long getLocalStart(long zeroBasedStart) {
            return zeroBasedStart + zeroBaseToCoordinateSystemStartAdjustmentValue;
        }
        /**
         * Get the end coordinate in this system from the 
         * equivalent zero-based end coordinate.
         * @param zeroBasedEnd the end coordinate in 0-based
         * coordiante system.
         */
        private long getLocalEnd(long zeroBasedEnd) {
            return zeroBasedEnd + zeroBaseToCoordinateSystemEndAdjustmentValue;
        }

        /**
         * Get 0-base start coordinate
        * from this coordinate system start location.
         */
        private long getStart(long localStart) {
            return localStart + coordinateSystemToZeroBaseStartAdjustmentValue;
        }
        /**
         * Get 0-base end location
        * from this coordinate system  end location.
         */
        private long getEnd(long localEnd) {
            return localEnd + coordinateSystemToZeroBaseEndAdjustmentValue;
        }

    }

    /**
     * Regular expression in the form (left) .. (right).
     */
    private static Pattern DOT_PATTERN = Pattern.compile("(\\d+)\\s*\\.\\.\\s*(\\d+)");
    /**
     * Regular expression in the form (left) - (right).
     */
    private static Pattern DASH_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
    /**
     * Regular expression in the form (left) , (right).
     */
    private static Pattern COMMA_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    /**
     * Cache of previously built ranges.  
     * This cache uses  {@link SoftReference}s
     * so memory can be reclaimed if needed.
     */
    private static final Map<String, Range> CACHE;
    /**
     * Initialize cache with a soft reference cache that will grow as needed.
     */
    static{
         CACHE = Caches.<String, Range>createSoftReferencedValueCache();
    }
    /**
     * Factory method to get a {@link Range} object in
     * the {@link CoordinateSystem#ZERO_BASED} coordinate system.
     * If end == start -1 then this method will return an {@link EmptyRange}.
     * This method is not guaranteed to return new instances and may return
     * a cached instance instead (flyweight pattern).
     * @param start start coordinate inclusive.
     * @param end end coordinate inclusive.
     * @return a {@link Range}; never null but might 
     * not be a new instance.
     * @throws IllegalArgumentException if {@code end < start -1} 
     * or if the resulting range length > {@link Long#MAX_VALUE}.
     */
    public static Range buildRange(long start, long end){
        return buildRange(CoordinateSystem.ZERO_BASED,start,end);
    }
    /**
     * Factory method to build a {@link Range} object.
     * of length 1 with the given coordinate in 
     * the {@link CoordinateSystem#ZERO_BASED} coordinate system.
     * @param singleCoordinate only coordinate in this range.
     * @return a {@link Range}; never null but might 
     * not be a new instance.
     */
    public static Range buildRange(long singleCoordinate){
        return buildRange(CoordinateSystem.ZERO_BASED,singleCoordinate);
    }
    
    /**
     * Factory method to build a {@link Range} object.
     * of length 1 with the given coordinate in 
     * the given coordinate system.
     * @param coordinateSystem the {@link CoordinateSystem} to use.
     * @param singleCoordinate only coordinate in this range.
     * @return a {@link Range}; never null but might 
     * not be a new instance.
     * @throws NullPointerException if the coordinateSystem is null.
     */
    public static Range buildRange(CoordinateSystem coordinateSystem, long singleCoordinate){
        return buildRangeOfLength(coordinateSystem,singleCoordinate,1);
    }
    /**
     * Factory method to build a {@link Range} object
     * with the given coordinates
     * specified in the given coordinate system. If after converting 
     * the coordinates into 0-based coordinate,
     * {@code end = start -1}, then
     * the returned range is equivalent to an empty range
     * at the start coordinate.
     * @param coordinateSystem the {@link CoordinateSystem} to use.
     * @param localStart the start coordinate in the given coordinateSystem.
     * @param localEnd the end coordinate in the given coordinateSystem.
     * @return a {@link Range}; never null but might 
     * not be a new instance.
     * @throws NullPointerException if the coordinateSystem is null.
     * @throws IllegalArgumentException if end < begin -1
     * or if the resulting range length > {@link Long#MAX_VALUE}.
     */
    public static synchronized Range buildRange(CoordinateSystem coordinateSystem,long localStart, long localEnd){
        if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot build null coordinate system range");
        }

        long zeroBasedStart = coordinateSystem.getStart(localStart);
        long zeroBasedEnd = coordinateSystem.getEnd(localEnd);
        final Range range;
        if(zeroBasedEnd >= zeroBasedStart) {
            range= buildNewRange(zeroBasedStart,zeroBasedEnd);            
        } else if (zeroBasedEnd == zeroBasedStart-1) {
            range = buildNewEmptyRange(zeroBasedStart);
        } else {
            throw new IllegalArgumentException("Range coordinates" + localStart + "," + localEnd
                + " are not valid " + coordinateSystem + " coordinates");
        }
        return getFromCache(range);
    }
    /**
     * Builds a new Range instance whose implementation depends
     * on the input start and end coordinates.  The implementation
     * that can take up the fewest number of bytes is chosen.
     * @param zeroBasedStart
     * @param zeroBasedEnd
     * @return a new Range instance.
     */
    private static Range buildNewRange(long zeroBasedStart, long zeroBasedEnd){
    	
    	
    	if(zeroBasedStart >=0){
    		//can use unsigned
    		long length = zeroBasedEnd - zeroBasedStart+1;
    		return buildNewUnsignedRange(zeroBasedStart, zeroBasedEnd,length);
    	}
    	
    	return buildNewSignedRange(zeroBasedStart, zeroBasedEnd);
    }
    /**
     * Create a new Range instance that requires signed values
     * (probably because the range has negative coordinates).
     *  The implementation
     * that can take up the fewest number of bytes is chosen.
     * @param zeroBasedStart
     * @param zeroBasedEnd
     * @return a new Range instance.
     */
	private static Range buildNewSignedRange(long zeroBasedStart,
			long zeroBasedEnd) {

    	if(canFitInSignedByte(zeroBasedStart, zeroBasedEnd)){
    		return new ByteRange((byte)zeroBasedStart, (byte)zeroBasedEnd);
    	}else if(canFitInSignedShort(zeroBasedStart,zeroBasedEnd)){
    		return new ShortRange((short)zeroBasedStart, (short)zeroBasedEnd);
    	}else if(canFitInSignedInt(zeroBasedStart,zeroBasedEnd)){
    		return new IntRange((int)zeroBasedStart, (int)zeroBasedEnd);
    	}    	
    	return new LongRange(zeroBasedStart, zeroBasedEnd);
	}
	
	private static boolean canFitInSignedByte(long start, long end){
		return start <= Byte.MAX_VALUE && start >=Byte.MIN_VALUE
    			&& end <= Byte.MAX_VALUE && end >=Byte.MIN_VALUE;
	}
	private static boolean canFitInSignedShort(long start, long end){
		return start <= Short.MAX_VALUE && start >=Short.MIN_VALUE
    			&& end <= Short.MAX_VALUE && end >=Short.MIN_VALUE;
	}
	private static boolean canFitInSignedInt(long start, long end){
		return start <= Integer.MAX_VALUE && start >=Integer.MIN_VALUE
    			&& end <= Integer.MAX_VALUE && end >=Integer.MIN_VALUE;
	}
	/**
	 * Create a new Range instance which can use unsigned
	 * values to save memory.  The implementation
     * that can take up the fewest number of bytes is chosen.
	 * @param zeroBasedStart
	 * @param zeroBasedEnd
	 * @param length
	 * @return
	 */
	private static Range buildNewUnsignedRange(long zeroBasedStart,
			long zeroBasedEnd, long length) {
		
		//JVM spec of computing size of objects
		//in heap includes padding
		//to keep objects a multiple of 8 bytes.
		//This means that not all byte-short-int-long combinations
		//actually affect the object size.
		if(zeroBasedStart <= UNSIGNED_BYTE_MAX){			
			if(length <= UNSIGNED_SHORT_MAX){
				return new UnsignedByteStartShortLengthRange((short) zeroBasedStart, (int)length);
			}
			if(length <= UNSIGNED_INT_MAX){
				return new UnsignedByteStartIntLengthRange((short) zeroBasedStart, length);
			}
			return new UnsignedByteStartLongLengthRange((short) zeroBasedStart, length);
		}
		
		if(zeroBasedStart <= UNSIGNED_SHORT_MAX){
			if(length <= UNSIGNED_SHORT_MAX){
				return new UnsignedShortStartShortLengthRange((int) zeroBasedStart, (int)length);
			}
			if(length <= UNSIGNED_INT_MAX){
				return new UnsignedShortStartIntLengthRange((int) zeroBasedStart, length);
			}
			return new UnsignedShortStartLongLengthRange((int) zeroBasedStart, length);
		}
		if(zeroBasedStart <= UNSIGNED_INT_MAX){
			if(length <= UNSIGNED_INT_MAX){
				return new UnsignedIntStartIntLengthRange(zeroBasedStart, length);
			}
			return new UnsignedIntStartLongLengthRange(zeroBasedStart, length);
		}
		if(length <= UNSIGNED_INT_MAX){
			return new LongStartIntLengthRange(zeroBasedStart, length);
		}
		return new LongRange(zeroBasedStart, zeroBasedEnd);

	}
	private static Range buildNewEmptyRange(long zeroBasedStart) {
		if(zeroBasedStart >=0){
			if(zeroBasedStart <=Byte.MAX_VALUE){
				return new EmptyByteRange((byte)zeroBasedStart);
			}else if(zeroBasedStart <=Short.MAX_VALUE){
				return new EmptyShortRange((short)zeroBasedStart);
			}else if(zeroBasedStart <=Integer.MAX_VALUE){
				return new EmptyIntRange((int)zeroBasedStart);
			}
		}
		//anything negative or > unsigned int should be stored as a long
		return new EmptyLongRange(zeroBasedStart);
	}
    private static synchronized Range getFromCache(Range range) {
       if(range==null){
    	   throw new NullPointerException("can not add null range to cache");
       }
        String hashcode = createCacheKeyFor(range);
       
        //contains() followed by get() is not atomic;
        //we could gc in between - so only do a get
        //and check if null.
        Range cachedRange= CACHE.get(hashcode);
        if(cachedRange !=null){
        	return cachedRange;
        }
        //not in cache so put it in
        CACHE.put(hashcode,range);
        return range;

    }
    private static String createCacheKeyFor(Range r){
        //Range's toString() should be fine
        //to ensure uniqueness in our cache.
        return r.toString();
    }
   
    /**
     * Build and empty range in the zero-based coordinate system
     * at coordinate 0.
     * @return a new Empty Range.
     */
    public static Range buildEmptyRange(){
        return buildEmptyRange(0);
    }
    /**
     * Build and empty range in the zero-based coordinate system
     * at the given coordinate.
     * @param coordinate the coordinate to set this empty range to.
     * @return a new Empty Range.
     */
    public static Range buildEmptyRange(long coordinate){
        return buildEmptyRange(Range.CoordinateSystem.ZERO_BASED,coordinate);
    }
    /**
     * Build and empty range in the given coordinate system
     * at the given coordinate.
     * @param coordinate the coordinate to set this empty range to.
     * @return a new Empty Range.
     * @throws NullPointerException if the coordinateSystem is null.
     */
    public static Range buildEmptyRange(CoordinateSystem coordinateSystem,long coordinate){
        if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot build null coordinate system range");
        }
        
        long zeroBasedStart = coordinateSystem.getStart(coordinate);

        return buildNewEmptyRange(zeroBasedStart);
    }

    /**
     * Build a new Range object of in the Zero based coordinate
     * system starting at 0 and with the given length.
     * @param length the length of this range.
     * @return a new Range.
     */
    public static Range buildRangeOfLength(long length){
        return buildRangeOfLength(0,length);
    }
    /**
     * Build a new Range object of in the Zero based coordinate
     * system at the given start offset with the given length.
     * @param start the start coordinate of this new range.
     * @param length the length of this range.
     * @return a new Range.
     * @throws IllegalArgumentException if length is negative
     * @throws IndexOutOfBoundsException if the combination 
     * of start and length values would cause the Range to extend
     * beyond {@link Long#MAX_VALUE}.
     */
    public static Range buildRangeOfLength(long start, long length){
    	if(length < 0){
    		throw new IllegalArgumentException("length can not be negative");
    	}
    	if(start >0){
    		long maxLength = Long.MAX_VALUE - start;
    		if(maxLength < length){
    			throw new IndexOutOfBoundsException(
    					String.format("given length %d would make range [%d - ? ] beyond max allowed end offset",
    							length, start));
    		}
    	}
        return buildRangeOfLength(CoordinateSystem.ZERO_BASED, start, length);
    }
    /**
     * Build a new Range object of in the given coordinate
     * system at the given start offset with the given length.
     * @param coordinateSystem the coordinate system to use.
     * @param localStart the start coordinate of this new range.
     * @param length the length of this range.
     * @return a new Range.
     * @throws NullPointerException if coordinateSystem is null.
     */
    public static Range buildRangeOfLength(CoordinateSystem coordinateSystem,long localStart, long length){
    	if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot build null coordinate system range");
        }
    	long zeroBasedStart = coordinateSystem.getStart(localStart);
        return buildRange(CoordinateSystem.ZERO_BASED,zeroBasedStart,zeroBasedStart+length-1);
    }
    public static Range buildRangeOfLengthFromEndCoordinate(long end, long rangeSize){
        return buildRangeOfLengthFromEndCoordinate(CoordinateSystem.ZERO_BASED,end,rangeSize);
    }
    public static Range buildRangeOfLengthFromEndCoordinate(CoordinateSystem system,long end, long rangeSize){
        long zeroBasedEnd = system.getEnd(end);
        return buildRange(CoordinateSystem.ZERO_BASED,zeroBasedEnd-rangeSize+1,zeroBasedEnd);
    }
    /**
     * Return a single
     * Range that covers the entire span
     * of the given Ranges.
     * <p>
     * For example: passing in 2 Ranges [0,10] and [20,30]
     * will return [0,30]
     * @param ranges varargs of Ranges
     * @return a new Range that covers the entire span of
     * input ranges.
     */
    public static Range buildInclusiveRange(Range... ranges){
        return buildInclusiveRange(Arrays.asList(ranges));
    }
    /**
     * Return a single
     * Range that covers the entire span
     * of the given Ranges.
     * <p>
     * For example: passing in 2 Ranges [0,10] and [20,30]
     * will return [0,30]
     * @param ranges a collection of ranges
     * @return a new Range that covers the entire span of
     * input ranges.
     */
    public static Range buildInclusiveRange(Collection<Range> ranges){
        if(ranges.isEmpty()){
            return buildEmptyRange();
        }
        Iterator<Range> iter =ranges.iterator();
        Range firstRange =iter.next();
        long currentLeft = firstRange.getStart();
        long currentRight = firstRange.getEnd();
        while(iter.hasNext()){
            Range range = iter.next();
            if(range.getStart() < currentLeft){
                currentLeft = range.getStart();
            }
            if(range.getEnd() > currentRight){
                currentRight = range.getEnd();
            }
        }
        return buildRange(currentLeft, currentRight);
    }
    /**
     * Parses a string in the format &lt;left&gt;[.. | - ]&lt;right&gt;. 
     * Any whitespace between the left and right parameters is ignored.
     * <br>
     * Examples:
     * <ul>
     * <li>24 .. 35</li>
     * <li>24-35</li>
     * <li>24,35</li>
     * </ul>
     * 
     * @param rangeAsString
     * @return a {@link Range}.
     * @throws IllegalArgumentException if the given String does not
     * match the correct format.
     */
    public static Range parseRange(String rangeAsString, CoordinateSystem coordinateSystem){
        Matcher dotMatcher =DOT_PATTERN.matcher(rangeAsString);
        if(dotMatcher.find()){
            return convertIntoRange(dotMatcher,coordinateSystem);
        }
        Matcher dashMatcher = DASH_PATTERN.matcher(rangeAsString);
        if(dashMatcher.find()){
            return convertIntoRange(dashMatcher,coordinateSystem);
        }
        Matcher commaMatcher = COMMA_PATTERN.matcher(rangeAsString);
        if(commaMatcher.find()){
            return convertIntoRange(commaMatcher,coordinateSystem);
        }
        throw new IllegalArgumentException("can not parse "+ rangeAsString +" into a Range");
    }
    /**
     * Parses a string in the format &lt;left&gt;[.. | - ]&lt;right&gt;. 
     * Any whitespace between the left and right parameters is ignored.
     * <br>
     * Examples:
     * <ul>
     * <li>24 .. 35</li>
     * <li>24-35</li>
     * <li>24,35</li>
     * </ul>
     * 
     * @param rangeAsString
     * @return a {@link Range}.
     * @throws IllegalArgumentException if the given String does not
     * match the correct format.
     */
    public static Range parseRange(String rangeAsString){
        return parseRange(rangeAsString, CoordinateSystem.ZERO_BASED);
    }
    
    private static Range convertIntoRange(Matcher dashMatcher, CoordinateSystem coordinateSystem) {
        return Range.buildRange(coordinateSystem,Long.parseLong(dashMatcher.group(1)), 
                Long.parseLong(dashMatcher.group(2))
                );
    }


    private Range(){
    	//can not instantiate
    }

    /**
     * Fetch the left (start) coordinate This is the same as 
     * {@link #getStart(CoordinateSystem)
     * getStart(ZERO_BASED)}.
     *
     * @return The left-hand (starting) coordinate.
     * 
     */
    @Override
    public abstract long getStart();
    /**
     * Fetch the left (start) coordinate using the given 
     * {@link CoordinateSystem}.  
     *
     * @return The left-hand (starting) coordinate.
     * @throws NullPointerException if the given {@link CoordinateSystem} is null.
     */
    public long getStart(CoordinateSystem coordinateSystem) {
    	if(coordinateSystem==null){
    		throw new NullPointerException("CoordinateSystem can not be null");
    	}
        return coordinateSystem.getLocalStart(getStart());
    }
    /**
     * Fetch the 0-based right (end) coordinate.
     * This is the same as {@link #getEnd(CoordinateSystem)
     * getEnd(ZERO_BASED)}.
     *
     * @return The right-hand (ending) coordinate.
     */
    @Override
    public abstract long getEnd();
    /**
     * Fetch the right (end) coordinate using the given 
     * {@link CoordinateSystem}.
     *
     * @return The right-hand (ending) coordinate.
     * @throws NullPointerException if the given {@link CoordinateSystem} is null.
     */
    public long getEnd(CoordinateSystem coordinateSystem) {
    	if(coordinateSystem==null){
    		throw new NullPointerException("CoordinateSystem can not be null");
    	}
        return coordinateSystem.getLocalEnd(getEnd());
    }

    /**
     * Calculate the size of the <code>Range</code>.  All <code>Range</code>s
     * are inclusive.
     *
     * @return The inclusive count of values between the left and right
     * coordinates.
     */
    public long size() {
        return getEnd() - getStart() + 1;
    }
    /**
     * Create a new Range of the same size
     * but shifted to the left the specified number of units.
     * @param units number of units to shift
     * @return a new Range (not null)
     */
    public Range shiftLeft(long units){
        return Range.buildRangeOfLength(this.getStart()-units, this.size());
    }
    /**
     * Create a new Range of the same size
     * but shifted to the right the specified number of units.
     * @param units number of units to shift
     * @return a new Range (not null)
     */
    public Range shiftRight(long units){
        return Range.buildRangeOfLength(this.getStart()+units, this.size());
    }
    /**
     * Checks if this range is empty.
     *
     * @return <code>true</code> if the range is empty, <code>false</code>
     * otherwise.
     */
    public boolean isEmpty(){
    	return false;
    }
    /**
     * Checks to see if the given target <code>Range</code> is contained within
     * this <code>Range</code>.  This does not require this <code>Range</code>
     * to be a strict subset of the target.  More precisely: a
     * <code>Range</code> is always a sub-range of itself.
     *
     * @param range The <code>Range</code> to compare to.
     * @return <code>true</code> if every value in this <code>Range</code> is
     * found in the given comparison <code>Range</code>.
     */
    public boolean isSubRangeOf(Range range) {
        if(range==null){
            return false;
        }
        
        /* We are always a subrange of ourselves */
        if (this.equals(range))
        {
            return true;
        }
        return isCompletelyInsideOf(range);
       
    }
    private boolean isCompletelyInsideOf(Range range) {
    	long start = getStart();
    	long end = getEnd();
    	long otherStart = range.getStart();
    	long otherEnd = range.getEnd();
    	
        return (start>otherStart && end<otherEnd) ||
           (start==otherStart && end<otherEnd) ||
           (start>otherStart && end==otherEnd);
    }

    /**
     * Checks to see if the given {@link Range} intersects this one.
     *
     * @param target The {@link Range} to check.
     * @return <code>true</code> if the coordinates of the two ranges overlap
     * each other in at least one point.
     */
    public boolean intersects(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in intersection operation.");
        }
        if(isEmpty()){
            return false;
        }
        if (target.isEmpty())
        {
            /*
             * Instead of defining empty set semantics here, we do it in the
             * EmptyRange class
             * -jsitz
             */
            return target.intersects(this);
        }

        return !(this.getStart() > target.getEnd() || this.getEnd() < target.getStart());
    }
    public boolean intersects(long coordinate){
        return coordinate >= this.getStart() && coordinate <=this.getEnd();
    }
    /**
     * Calculates the intersection of this {@link Range} and a second one.
     * <p>
     * The intersection of an empty list with any other list is always the
     * empty list.  The intersection of
     *
     * @param target The second {@link Range} to compare
     * @return A {@link Range} object spanning only the range of values covered
     * by both {@link Range}s.
     */
    public Range intersection(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in intersection operation.");
        }
        if(isEmpty()){
            return this;
        }
        if (target.isEmpty())
        {
            return target.intersection(this);
        }

        try{
            long intersectionStart = Math.max(target.getStart(), this.getStart());
			long intersectionEnd = Math.min(target.getEnd(), this.getEnd());
			return  Range.buildRange(intersectionStart,
                            intersectionEnd);
        }
        catch(NullPointerException npe){
        	throw npe;
        }
        catch(IllegalArgumentException e){
            return buildEmptyRange();
        }

    }
    /**
     * Get the List of Ranges that represents the 
     * {@code this - other}.  This is similar to the 
     * Set of all coordinates that don't intersect.
     * @param other the range to compliment with.
     * @return
     */
    public List<Range> compliment(Range other){
        //this - other
        //anything in this that doesn't intersect with other
        Range intersection = intersection(other);
        if(intersection.isEmpty()){
            return Arrays.asList(this);
        }
        
        Range beforeOther = Range.buildRange(getStart(), intersection.getStart()-1);
        Range afterOther = Range.buildRange(intersection.getEnd()+1, getEnd());
        List<Range> complimentedRanges = new ArrayList<Range>();
        if(!beforeOther.isEmpty()){
            complimentedRanges.add(beforeOther);
        }
        if(!afterOther.isEmpty()){
            complimentedRanges.add(afterOther);
        }
        return Ranges.merge(complimentedRanges);
    }
    
    public List<Range> complimentFrom(Collection<Range> ranges){
        List<Range> universe = Ranges.merge(new ArrayList<Range>(ranges));
        List<Range> compliments = new ArrayList<Range>(universe.size());
        for(Range range : universe){
            compliments.addAll(range.compliment(this));
        }
        return Ranges.merge(compliments);
    }

    /**
     * Checks to see if this <code>Range</code> starts before the given
     * comparison <code>Range</code>.
     *
     * @param target The <code>Range</code> to compare to.
     * @return <code>true</code> if the left-hand coordinate of this
     * <code>Range</code> is less than the left-hand coordinate of the
     * comparison <code>Range</code>.
     */
    public boolean startsBefore(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in range comparison operation.");
        }

        return this.getStart() < target.getStart();
    }

    /**
     * Checks to see if this <code>Range</code> ends before the given target.
     *
     * @param target The target <code>Range</code> to check against.
     * @return <code>true</code> if this <code>Range</code> has an end value
     * which occurs before (and not at the same point as) the target
     * <code>Range</code>.
     */
    public boolean endsBefore(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in range comparison operation.");
        }
        if (isEmpty() || target.isEmpty())
        {
            return false;
        }
        
        return this.getEnd() < target.getStart();
    }

    
    
    /**
     * Modifies the extent of a range by simultaneously adjusting its coordinates by specified
     * amounts.  This method is primarily intended to increase the size of the
     * <code>Range</code>, and as such, positive values will result in a <code>Range</code>
     * which is longer than the current <code>Range</code>.  Negative values may also be used,
     * with appropriately opposite results, and positive and negative deltas may be mixed to 
     * produce a traslation/scaling effect.
     * 
     * @param fromStart The number of positions to extend the start of the range.
     * @param fromEnd The number of positions to extend the end of the range.
     * @return A new <code>Range</code> in the same {@link RangeCoordinateSystem}, with modified
     * coordinates.
     */
    public Range grow(long fromStart, long fromEnd)
    {
        return Range.buildRange(this.getStart() - fromStart, this.getEnd() + fromEnd);
    }
    
    /**
     * Modifies the extend of a <code>Range</code> by adjusting its coordinates.  This is 
     * directly related to the {@link #grow(long, long)} method.  It simply passes the 
     * numerical negation of the values given here.
     * <p>
     * This is done as a convenience to make code easier to read.  Usually this method will be
     * called with variables in the parameters and it will not be immediately obvious that the
     * end result is intended to be a smaller <code>Range</code>.  This method should be used to
     * make this situation more clear.
     * 
     * @param fromStart The number of positions to extend the start of the range.
     * @param fromEnd The number of positions to extend the end of the range.
     * @return A new <code>Range</code> in the same {@link RangeCoordinateSystem}, with modified
     * coordinates.
     */
    public Range shrink(long fromStart, long fromEnd)
    {
        return this.grow(-fromStart, -fromEnd);
    }
    
   
    /**
     * Convenience method that delegates to
     * {@link #toString(CoordinateSystem)} using {@link CoordinateSystem#ZERO_BASED}.
     * 
     * @see #toString(CoordinateSystem)
     * 
     */
    @Override
    public String toString()
    {
        return toString(CoordinateSystem.ZERO_BASED);
    }
    /**
     * Returns a String representation of this Range in given coordinate system.
     * The actual format is {@code [localStart - localEnd]/systemAbbreviatedName}
     * @throws NullPointerException if coordinateSystem is null.
     */
    public String toString(CoordinateSystem coordinateSystem)
    {
    	if(coordinateSystem ==null){
    		throw new NullPointerException("coordinateSystem can not be null");
    	}
        return String.format("[ %d - %d ]/%s", 
        		coordinateSystem.getLocalStart(getStart()) ,
        		coordinateSystem.getLocalEnd(getEnd()),
                coordinateSystem.getAbbreviatedName());
    }
   

    @Override
    public Iterator<Long> iterator() {
        return new RangeIterator(this);
    }
    
   
    /**
     * Splits a Range into a List of possibly several adjacent Range objects
     * where each of the returned ranges has a max length specified.
     * @param maxSplitLength the max length any of the returned split ranges can be.
     * @return a List of split Ranges; never null or empty but may
     * just be a single element if this Range is smaller than the max length
     * specified.
     */
    public List<Range> split(long maxSplitLength){
        if(size()<maxSplitLength){
            return Collections.singletonList(this);
        }
        long currentStart=getStart();
        List<Range> list = new ArrayList<Range>();
        while(currentStart<=getEnd()){
            long endCoordinate = Math.min(getEnd(), currentStart+maxSplitLength-1);
            list.add(Range.buildRange(currentStart, endCoordinate));
            currentStart = currentStart+maxSplitLength;
        }
        return list;
    }
   
    /**
     * Compares two Ranges using the {@link Comparators#ARRIVAL}
     * Comparator.
     * This is the same as {@code Comparators.ARRIVAL.compare(this,that);
     */
    @Override
    public int compareTo(Range that) 
    {
        return Comparators.ARRIVAL.compare(this, that);
    }

    @Override
    public long getLength() {
        return size();
    }
    /**
    * {@inheritDoc} 
    * <p/>
    * Returns this since it is already a Range.
    * @return this.
    */
    @Override
    public Range asRange() {
        return this;
    }
    
    private static class RangeIterator implements Iterator<Long>{
        private final long from;
        private final long to;
        private long index;
        
        public RangeIterator(Range range){
            from = range.getStart();
            to = range.getEnd();
            index = from;
        }
        @Override
        public boolean hasNext() {
            return index<=to;
        }

        @Override
        public Long next() {
            return index++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove from Range");
            
        }
        
    }
    /**
     * Range implementation that stores the 
     * start and end coordinates as longs.
     * @author dkatzel
     *
     */
    private static final class LongRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final long start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long end;
        
    	private LongRange(long start, long end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (start ^ (start >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj == null){
				return false;
			}
			if (getClass() != obj.getClass()){
				return false;
			}
			LongRange other = (LongRange) obj;
			if (end != other.end){
				return false;
			}
			if (start != other.start){
				return false;
			}
			return true;
		}
    	
    	
    }
    /**
     * Range implementation that stores the 
     * start and end coordinates as ints.
     * @author dkatzel
     *
     */
    private static final class IntRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final int start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int end;
        
    	private IntRange(int start, int end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj == null){
				return false;
			}
			if (getClass() != obj.getClass()){
				return false;
			}
			IntRange other = (IntRange) obj;
			if (end != other.end){
				return false;
			}
			if (start != other.start){
				return false;
			}
			return true;
		}
    }
    
   
    /**
     * Range implementation that stores the 
     * start and end coordinates as shorts.
     * @author dkatzel
     *
     */
    private static final class ShortRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  short end;
        
    	private ShortRange(short start, short end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ShortRange other = (ShortRange) obj;
			if (end != other.end) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
   
    /**
     * Range implementation that stores the 
     * start and end coordinates as bytes.
     * @author dkatzel
     *
     */
    private static final class ByteRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  byte end;
        
    	private ByteRange(byte start, byte end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ByteRange other = (ByteRange) obj;
			if (end != other.end) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    	

    }
    
    
    
    /**
     * Range implementation that stores the 
     * start coordinates as an unsigned byte
     * and the length as an unsigned short.
     * This is commonly used for next-gen length
     * valid ranges or next-gen reads placed in the beginning
     * of contigs/scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedByteStartShortLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  short length;
        
    	private UnsignedByteStartShortLengthRange(short start, int length){
    		this.start = IOUtil.toSignedByte(start);
	        this.length = IOUtil.toSignedShort(length);
	       
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedByte(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getLength() {
            return IOUtil.toUnsignedShort(length);
        }

    	@Override
    	public long getEnd(){
    		return getStart() + getLength() -1;
    	}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedByteStartShortLengthRange other = (UnsignedByteStartShortLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
   
    
    /**
     * Range implementation that stores the 
     * start coordinates as an unsigned byte
     * and the length as an unsigned int.
     * This is commonly used for contigs
     * placed in the beginning
     * of scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedByteStartIntLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
    	private UnsignedByteStartIntLengthRange(short start, long length){
    		this.start = IOUtil.toSignedByte(start);
	        this.length = IOUtil.toSignedInt(length);
	       
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedByte(start);
        }

    	@Override
        public long getLength() {
            return IOUtil.toUnsignedInt(length);
        }

    	@Override
    	public long getEnd(){
    		return getStart() + getLength() -1;
    	}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedByteStartIntLengthRange other = (UnsignedByteStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start coordinates as an unsigned byte
     * and the length as a long.
     * This is commonly used for large contig
     * or scaffold ranges.
     * @author dkatzel
     *
     */
    private static final class UnsignedByteStartLongLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long length;
        
    	private UnsignedByteStartLongLengthRange(short start, long length){
    		this.start = IOUtil.toSignedByte(start);
	        this.length = length;
	       
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedByte(start);
        }

    	@Override
        public long getLength() {
            return length;
        }

    	@Override
    	public long getEnd(){
    		return getStart() + getLength() -1;
    	}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedByteStartLongLengthRange other = (UnsignedByteStartLongLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
		
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned shorts
     * and the length as an unsigned byte.
     * This is probably the most common read valid
     * range for next-gen sequencing.
     * @author dkatzel
     *
     */
    private static final class UnsignedShortStartShortLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  short length;
        
    	private UnsignedShortStartShortLengthRange(int start, int length){
    		 this.start = IOUtil.toSignedShort(start);
	        this.length = IOUtil.toSignedShort(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedShort(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedShort(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getStart()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedShortStartShortLengthRange other = (UnsignedShortStartShortLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned shorts
     * and the length as an unsigned byte.
     * This is probably the most common read valid
     * range for next-gen sequencing.
     * @author dkatzel
     *
     */
    private static final class UnsignedShortStartIntLengthRange extends Range{
    

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;
        
    	private UnsignedShortStartIntLengthRange(int start, long length){
    		 this.start = IOUtil.toSignedShort(start);
	        this.length = IOUtil.toSignedInt(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedInt(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedShort(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getStart()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedShortStartIntLengthRange other = (UnsignedShortStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned shorts
     * and the length as an long.
     * This is often used to placed
     * contigs in scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedShortStartLongLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long length;
        
    	private UnsignedShortStartLongLengthRange(int start, long length){
    		 this.start = IOUtil.toSignedShort(start);
	        this.length = length;
    	}
    	
    	@Override
		public long getLength() {
			return length;
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedShort(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getStart()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedShortStartLongLengthRange other = (UnsignedShortStartLongLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned int
     * and the length as an unsigned byte.
     * This is often used for placing contigs
     * at the middle/ends of scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedIntStartIntLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final int start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
    	private UnsignedIntStartIntLengthRange(long start, long length){
    		 this.start = IOUtil.toSignedInt(start);
	        this.length = IOUtil.toSignedInt(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedInt(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedInt(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getStart()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedIntStartIntLengthRange other = (UnsignedIntStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as an unsigned int
     * and the length as an long.
     * This is often used to placed
     * contigs in scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedIntStartLongLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final int start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long length;
        
    	private UnsignedIntStartLongLengthRange(long start, long length){
    		 this.start = IOUtil.toSignedInt(start);
	        this.length = length;
    	}
    	
    	@Override
		public long getLength() {
			return length;
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return IOUtil.toUnsignedInt(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getStart()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedIntStartLongLengthRange other = (UnsignedIntStartLongLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    /**
     * Range implementation that stores the 
     * start as signed long
     * and the length as an unsigned byte.
     * This is often used for placing contigs
     * at the middle/ends of scaffolds.
     * @author dkatzel
     *
     */
    private static final class LongStartIntLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final long start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
    	private LongStartIntLengthRange(long start, long length){
    		 this.start = start;
	        this.length = IOUtil.toSignedInt(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedInt(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getStart(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getStart() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getStart()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + (int) (start ^ (start >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			LongStartIntLengthRange other = (LongStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}

		
    }
  
    
    
    private static final class EmptyByteRange extends Range{
    	private final byte coordinate;
    	
    	EmptyByteRange(byte coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getStart() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coordinate;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyByteRange other = (EmptyByteRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
    	
    }
    
    private static final class EmptyShortRange extends Range{
    	private final short coordinate;
    	
    	EmptyShortRange(short coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getStart() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coordinate;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyShortRange other = (EmptyShortRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
    }
    
    private static final class EmptyIntRange extends Range{
    	private final int coordinate;
    	
    	EmptyIntRange(int coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getStart() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coordinate;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyIntRange other = (EmptyIntRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
    }
    
    private static final class EmptyLongRange extends Range{
    	private final long coordinate;
    	
    	EmptyLongRange(long coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getStart() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (coordinate ^ (coordinate >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyLongRange other = (EmptyLongRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
		
    }
    
}
