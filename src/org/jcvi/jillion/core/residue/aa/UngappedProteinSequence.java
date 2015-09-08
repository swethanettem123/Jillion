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
package org.jcvi.jillion.core.residue.aa;

import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
/**
 * {@code UngappedProteinSequence} is a {@link ProteinSequence}
 * which contains no gaps.  This allows us to short circuit many 
 * of the gap to ungap computations for improved
 * performance.
 * @author dkatzel
 *
 */
class UngappedProteinSequence extends CompactProteinSequence{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3481921186285369074L;

	public UngappedProteinSequence(AminoAcid[] aas) {
		super(aas);
	}

	@Override
	public List<Integer> getGapOffsets() {
		return Collections.emptyList();
	}

	@Override
	public int getNumberOfGaps() {
		return 0;
	}

	@Override
	public boolean isGap(int gappedOffset) {
		return false;
	}

	@Override
	public long getUngappedLength() {
		return getLength();
	}

	@Override
	public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
		return 0;
	}


	@Override
	public int getUngappedOffsetFor(int gappedIndex) {
		return gappedIndex;
	}

	@Override
	public int getGappedOffsetFor(int ungappedIndex) {
		return ungappedIndex;
	}

	private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
		throw new java.io.InvalidObjectException("Proxy required");
	}
}
