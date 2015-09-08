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
package org.jcvi.jillion.core.residue;


/**
 * @author dkatzel
 *
 *
 */
public interface Residue{

	byte getOrdinalAsByte();
	
	 /**
     * Return the single Character equivalent of this
     * {@link Residue}.  
     * @return the Character equivalent of this.
     */
    public Character getCharacter();
    /**
     * Is this Residue a gap?
     * @return {@code true} if it is a gap;
     * {@code false} otherwise.
     */
    boolean isGap();
}
