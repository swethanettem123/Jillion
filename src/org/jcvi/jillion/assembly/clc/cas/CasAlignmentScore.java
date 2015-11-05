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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

public interface CasAlignmentScore {

    int getFirstInsertionCost();
    
    int getInsertionExtensionCost();
    
    int getFirstDeletionCost();
    
    int getDeletionExtensionCost();
    
    int getMatchScore();
    /**
     * Get the score for transitions from
     * {@code A <=> G} or {@code C <=> T}.
     * @return the transition score.
     */
    int getTransitionScore();
    /**
     * Get the score of other differences that aren't transitions.
     * @return the transversion score.
     */
    int getTransversionScore();
    /**
     * Get the score for unknown alignments.
     * @return the unknown score.
     */
    int getUnknownScore();
}
