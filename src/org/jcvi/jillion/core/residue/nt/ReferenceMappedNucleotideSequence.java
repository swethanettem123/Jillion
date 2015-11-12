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
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.util.SortedMap;
/**
 * {@code ReferenceMappedNucleotideSequence} is
 * a NucleotideSequence that has been mapped
 * to another reference NucleotideSequence.
 * This sub-interface of {@link NucleotideSequence}
 * which has extra methods to get the differences between
 * the reference and this sequence and the to get the actual 
 * reference sequence used.
 * <p>
 * It is possible to reduce the memory footprint for
 * {@link ReferenceMappedNucleotideSequence}s by only
 * storing these  2 fields.  All other return values from
 * {@link NucleotideSequence} can be computed.  
 * This should keep the memory footprint
 * quite low since an underlying sequence should map to a reference 
 * with a high identity.  If the reference is the consensus,
 * the underlying sequence should map more than 90%.
 * @author dkatzel
 *
 *
 */
public interface ReferenceMappedNucleotideSequence extends NucleotideSequence{
    
    /**
     * Get a Mapping of all the offsets (as Integers) 
     * of this read compared to the reference.
     * All coordinates are 0-based gapped offset locations in the read coordinate system;
     * so if a difference is located in the first base of the read,
     * then its integer will be zero.  The Map is sorted by 
     * offset, increasing from smallest offset to largest.
     * @return a Map of all the differences between
     * this sequence and its reference; will never be null 
     * but may be empty if there are no differences.
     */
    SortedMap<Integer, Nucleotide> getDifferenceMap();
    /**
     * Get the Reference sequence that this
     * sequence is mapped to.
     * @return the {@link NucleotideSequence} of the reference.
     */
    NucleotideSequence getReferenceSequence();
}
