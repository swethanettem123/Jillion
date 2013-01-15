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
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public abstract class  AbstractContigFileVisitor implements ContigFileVisitor{
    private boolean initialized;
    private String currentContigId;
    private boolean readingConsensus=true;
    private NucleotideSequenceBuilder currentBasecalls = new NucleotideSequenceBuilder();
    
    private int currentReadOffset;
    private String currentReadId ;
    private Direction currentReadSequenceDirection;
    private Range currentReadValidRange;


    private void throwExceptionIfInitialized() {
        if(isInitialized()){
            throw new IllegalStateException("DataStore already initialized");
        }
    }

    @Override
    public void visitFile() {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusBasecallsLine(String lineOfBasecalls) {
        throwExceptionIfInitialized();
        currentBasecalls.append(lineOfBasecalls);
    }

    @Override
    public void visitReadBasecallsLine(String lineOfBasecalls) {
        throwExceptionIfInitialized();
        currentBasecalls.append(lineOfBasecalls);
        
    }

    @Override
    public void visitLine(String line) {
        throwExceptionIfInitialized();
    }

    @Override
    public void visitNewContig(String contigId) {
        throwExceptionIfInitialized();
        if(!readingConsensus){ 
            visitCurrentRead(); 
            visitEndOfContig();
        }
        currentContigId = contigId;
        currentBasecalls = new NucleotideSequenceBuilder();
        readingConsensus=true;
    }

    protected abstract void visitRead(String readId, int offset, Range validRange, NucleotideSequence basecalls, Direction dir);
    protected abstract void visitEndOfContig();
    protected abstract void visitBeginContig(String contigId, NucleotideSequence consensus);
    
    @Override
    public void visitNewRead(String seqId, int offset, Range validRange,
            Direction dir) {
        throwExceptionIfInitialized();
        if(readingConsensus){  
            visitBeginContig(currentContigId, currentBasecalls.build());
            
            readingConsensus =false;
        }
        else{
            //done previous sequence
            visitCurrentRead();               
        }
        this.currentReadId = seqId;
        this.currentReadOffset = offset;
        this.currentReadSequenceDirection = dir;
        this.currentReadValidRange= validRange;
        currentBasecalls = new NucleotideSequenceBuilder();
        
    }

    private void visitCurrentRead() {
        visitRead(currentReadId, currentReadOffset, currentReadValidRange, currentBasecalls.build(), currentReadSequenceDirection);
    }

    
    public boolean isInitialized() {
        return initialized;
    }

    
    @Override
    public void visitEndOfFile() {
        throwExceptionIfInitialized();
        visitNewContig(null);
        initialized = true;
        this.currentReadId = null;
        this.currentReadValidRange= null;
        currentBasecalls = null;
    }

   
}