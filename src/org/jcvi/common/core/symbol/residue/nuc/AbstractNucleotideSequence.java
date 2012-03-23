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
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Iterator;

import org.jcvi.common.core.symbol.residue.AbstractResidueSequence;

public abstract class AbstractNucleotideSequence extends AbstractResidueSequence<Nucleotide> implements NucleotideSequence{

    /**
     * Iterator that doesn't need to decode
     * the entire sequence to get the iterator.
     * @author dkatzel
     */
    private class NucleotideSequenceIterator implements Iterator<Nucleotide>{
        private int i=0;

        @Override
        public boolean hasNext() {
            return i< getLength();
        }
        @Override
        public Nucleotide next() {
            Nucleotide next = get(i);
            i++;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove nucleotides");
            
        }
        
    }
}
