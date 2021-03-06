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
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

public class PhaseChangeCasAlignmentRegion implements CasAlignmentRegion{
    private final byte phaseChange;
    public PhaseChangeCasAlignmentRegion(byte phaseChange){
        this.phaseChange = phaseChange;
    }
    @Override
    public long getLength() {
        //length is always 0?
        return 0L;
    }

    @Override
    public CasAlignmentRegionType getType() {
        return CasAlignmentRegionType.PHASE_CHANGE;
    }
    
    public byte getPhaseChange() {
        return phaseChange;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + phaseChange;
        return result;
    }
    /**
    * {@inheritDoc}
    */
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
        PhaseChangeCasAlignmentRegion other = (PhaseChangeCasAlignmentRegion) obj;
        if (phaseChange != other.phaseChange){
            return false;
        }
        return true;
    }

    
}
