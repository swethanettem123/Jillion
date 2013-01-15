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

package org.jcvi.jillion.assembly.clc.cas.var;

import org.jcvi.jillion.core.io.TextFileVisitor;

/**
 * {@code VariationLogFileVisitor} is a {@link TextFileVisitor}
 * for visiting CLC variation log files produced by the
 * {@code find_variations} program.
 * 
 * @author dkatzel
 *
 *
 */
public interface VariationLogFileVisitor extends TextFileVisitor{
	/**
	 * Visit a new reference contained in the CLC .cas file.
	 * @param id the reference id in the input to the assembly.
	 * @return {@code true} if the variations for this
	 * reference should be read; {@code false}
	 * otherwise.
	 */
    boolean visitReference(String id);
    /**
     * Visit a single {@link Variation} entry
     * that was found for the current reference 
     * verses the assembled data.
     * @param variation the variation found; will never be null.
     */
    void visitVariation(Variation variation);
}