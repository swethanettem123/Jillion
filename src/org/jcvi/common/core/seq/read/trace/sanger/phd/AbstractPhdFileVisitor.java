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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code AbstractPhdFileVisitor} is a {@link PhdFileVisitor}
 * implementation that will keep track of all
 * the data associated with the current {@link Phd}
 * record being visited.  Once an entire Phd record
 * has been visited, this class with make a call to {@link #visitPhd(String, List, List, List, Properties, List)}.
 * 
 * @see #visitPhd(String, List, List, List, Properties, List)
 * @author dkatzel
 *
 *
 */
public abstract class AbstractPhdFileVisitor implements PhdFileVisitor{

    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    private NucleotideSequenceBuilder currentBases = new NucleotideSequenceBuilder();
    private QualitySequenceBuilder currentQualities = new QualitySequenceBuilder();
    private List<ShortSymbol> currentPositions = new ArrayList<ShortSymbol>();
    private List<PhdTag> tags = new ArrayList<PhdTag>();
    private Properties currentComments;
    private String currentId; 
    private String currentTag;
    private boolean inTag=false;
    private StringBuilder currentTagValueBuilder;
    
    private final DataStoreFilter filter;
    /**
     * Create a new AbstractPhdFileVisitor
     * that will visit all phd records (no filter).
     */
    public AbstractPhdFileVisitor(){
        this(AcceptingDataStoreFilter.INSTANCE);
    }
    /**
     * Create a new AbstractPhdFileVisitor with the given filter.  Any
     * phd records that are not accepted by this filter will not get
     * the {@link #visitPhd(String, List, List, List, Properties, List)}
     * method called on it.
     * @param filter the DataStoreFilter to use; can not be null.
     * @throws NullPointerException if filter is null.
     */
    public AbstractPhdFileVisitor(DataStoreFilter filter){
        if(filter ==null){
            throw new NullPointerException("filter can not be null");
        }
        this.filter= filter;
    }
    /**
     * Visit the current Phd record that is being visited.  Only phds
     * that are accepted by the DataStoreFilter (if any)
     * will get this method called on it.
     * It is up to the concrete implementation of this class
     * to handle this data as it sees fit.
     * @param id the id of this phd record.
     * @param bases the basecalls of this Phd
     * @param qualities the quality scores of this phd.
     * @param positions the positions of this phd.
     * @param comments any comments of this Phd may be emtpy but never null.
     * @param tags any tags of this phd, may be emtpy but never null.
     * @return {@code true} if should keep parsing; {@code false}
     * otherwise.
     */
    protected abstract boolean visitPhd(String id,
            NucleotideSequence bases,
            QualitySequence qualities,
            List<ShortSymbol> positions, 
            Properties comments,
            List<PhdTag> tags);
    
    @Override
    public synchronized void visitBasecall(Nucleotide base, PhredQuality quality,
            int tracePosition) {
        currentBases.append(base);
       currentQualities.append(quality);
       currentPositions.add(PEAK_FACTORY.getGlyphFor(tracePosition));            
    }

    private void resetCurrentValues(){
        currentBases= new NucleotideSequenceBuilder();
        currentQualities= new QualitySequenceBuilder();
        currentPositions= new ArrayList<ShortSymbol>();
        tags = new ArrayList<PhdTag>();
    }



    @Override
    public void visitBeginDna() {
    }



    @Override
    public synchronized void visitBeginSequence(String id) {      
    }



    @Override
    public synchronized void visitComment(Properties comments) {
        this.currentComments = comments;
    }



    @Override
    public synchronized void visitEndDna() {        
    }



    @Override
    public synchronized void visitEndSequence() {
       
    }



    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized boolean visitBeginPhd(String id) {
        this.currentId = id;
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized boolean visitEndPhd() {
        boolean keepParsing=true;
        if(filter.accept(currentId)){
            keepParsing= visitPhd(currentId, currentBases.build(), currentQualities.build(), currentPositions, currentComments,tags);
        }
        resetCurrentValues();
        return keepParsing;
    }

    @Override
    public synchronized void visitLine(String line) {
        if(inTag){
            currentTagValueBuilder.append(line);
        }
    }



    @Override
    public synchronized void visitEndOfFile() {
       
        
    }



    @Override
    public synchronized void visitFile() {
    }


    @Override
    public synchronized void visitBeginTag(String tagName) {
        currentTag =tagName;
        currentTagValueBuilder = new StringBuilder();
        inTag =true;
    }

    @Override
    public synchronized void visitEndTag() {
        if(!inTag){
            throw new IllegalStateException("invalid tag");
        }
        tags.add(new DefaultPhdTag(currentTag, currentTagValueBuilder.toString()));
        inTag = false;
    }

}
