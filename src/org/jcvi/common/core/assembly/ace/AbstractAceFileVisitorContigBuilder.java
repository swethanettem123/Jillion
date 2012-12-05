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
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code AbstractAceContigBuilder} is an abstract
 * implementation of {@link AceFileVisitor}
 * that does all the computations required
 * to populate instances of {@link AceContigBuilder}.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAceFileVisitorContigBuilder extends AbstractAceFileVisitor {
   
   
    private AceContigBuilder contigBuilder;
    /**
     * Visit the given fully constructed AceContig. 
     * @param contig the fully constructed AceContig
     * that was built from an Ace File.
     */
    protected abstract void  visitContig(AceContig contig);
    /**
     * Override this method if any modifications
     * need to be made to the builder before the contig has been built.
     * This method will be called after all the reads have been
     * visited but before {@link #visitContig(AceContig)}. 
     * By default, this method does nothing.
     * @param contigBuilder
     */
    protected void postProcess(final AceContigBuilder contigBuilder){
        //no-op
    }
    
    @Override
    public EndContigReturnCode visitEndOfContig() {
        if(contigBuilder !=null){
            postProcess(contigBuilder);
            visitContig(contigBuilder.build());
            contigBuilder=null;
        }
        return EndContigReturnCode.KEEP_PARSING;
    }
    
    
    @Override
    protected void visitNewContig(String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complemented) {
        contigBuilder= new AceContigBuilder(contigId, consensus);
        contigBuilder.setComplemented(complemented);
        
    }
    @Override
    protected void visitAceRead(String readId, NucleotideSequence validBasecalls, int offset, Direction dir, Range validRange, PhdInfo phdInfo,
            int ungappedFullLength){
        contigBuilder.addRead(readId, validBasecalls ,offset, dir, 
                validRange ,phdInfo,ungappedFullLength);
    }

    

}
