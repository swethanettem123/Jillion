/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code UnsignedIntArray} is a  class
 * that stores arrays of unsigned ints
 * (opposed to signed bytes like {@code int[]} does.
 * 
 * @author dkatzel
 */
public class UnsignedIntArray {

	private final int[] array;
	/**
	 * Create a new {@link UnsignedIntArray}
	 * object that is initialized to a COPY
	 * of the given byte array.
	 * All values in the array are treated as unsigned.
	 * @param array the array to copy;
	 * can not be null.
	 */
	public UnsignedIntArray(int[] array){
		if(array ==null){
			throw new NullPointerException("array can not be null");
		}
		this.array = Arrays.copyOf(array, array.length);
	}
	
	/**
	 * The number of elements in the array.
	 * @return an int always >=0.
	 */
	public int getLength(){
		return array.length;
	}
	/**
	 * Get the unsigned byte value
	 * of the given 0-based index.
	 * @param i the 0-based index; must be >=0 and < length
	 * @return the unsigned byte value;
	 * will always be >=0.
	 */
	public long get(int i){
		return IOUtil.toUnsignedInt(array[i]);
	}
	/**
	 * Set the given value to the given
	 * 0-based index in the array.
	 * @param i
	 * @param value the unsigned value to set.
	 */
	public void put(int i, long value){
		array[i] =IOUtil.toSignedInt(value);
	}


	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UnsignedIntArray)) {
			return false;
		}
		UnsignedIntArray other = (UnsignedIntArray) obj;
		if (!Arrays.equals(array, other.array)) {
			return false;
		}
		return true;
	}
	
	
	
}
