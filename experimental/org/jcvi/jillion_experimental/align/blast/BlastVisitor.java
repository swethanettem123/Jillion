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
package org.jcvi.jillion_experimental.align.blast;


/**
 * {@code BlastVisitor} is a visitor 
 * interface to visit {@link BlastHit}s
 * encoded in blast output.
 * 
 * @author dkatzel
 *
 *
 */
public interface BlastVisitor{

    /**
     * The File has been completely visited.
     */
    void visitEnd();

	void visitInfo(String programName, String programVersion,
			String blastDb, String queryId);
	/**
	 * Visit the next {@link BlastHit}
	 * in the blast output.
	 * @param hit the {@link BlastHit} being visited;
	 * will never be null.
	 */
	void visitHit(BlastHit hit);
}
