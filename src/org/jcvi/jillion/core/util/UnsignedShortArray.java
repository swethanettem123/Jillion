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
package org.jcvi.jillion.core.util;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code UnsignedShortArrayv} is a  class
 * that stores arrays of unsigned shorts
 * (opposed to signed bytes like {@code short[]} does.
 * 
 * @author dkatzel
 */
public class UnsignedShortArray {

	private final short[] array;
	/**
	 * Create a new {@link UnsignedShortArray}
	 * object that is initialized to a COPY
	 * of the given byte array.
	 * All values in the array are treated as unsigned.
	 * @param array the array to copy;
	 * can not be null.
	 */
	public UnsignedShortArray(short[] array){
		if(array ==null){
			throw new NullPointerException("array can not be null");
		}
		this.array = Arrays.copyOf(array, array.length);
	}
	
	/**
	 * The number of elements in the array.
	 * @return an int always &ge; 0.
	 */
	public int getLength(){
		return array.length;
	}
	/**
	 * Get the unsigned short value
	 * of the given 0-based index.
	 * @param i the 0-based index; must be &ge; 0 and &lt; length
	 * @return the unsigned short value;
	 * will always be &ge; 0.
	 */
	public int get(int i){
		return IOUtil.toUnsignedShort(array[i]);
	}
	/**
	 * Set the given value to the given
	 * 0-based index in the array.
	 * @param i the offset to put.
	 * 
	 * @param value the unsigned value to set.
	 */
	public void put(int i, int value){
		array[i] =IOUtil.toSignedShort(value);
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
		if (!(obj instanceof UnsignedShortArray)) {
			return false;
		}
		UnsignedShortArray other = (UnsignedShortArray) obj;
		if (!Arrays.equals(array, other.array)) {
			return false;
		}
		return true;
	}
	
	
	
}
