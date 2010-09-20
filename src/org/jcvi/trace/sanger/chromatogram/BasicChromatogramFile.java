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

package org.jcvi.trace.sanger.chromatogram;
import java.util.Map;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;

/**
 * @author dkatzel
 *
 *
 */
public class BasicChromatogramFile implements Chromatogram, ChromatogramFileVisitor{

    private Chromatogram delegate;
    private BasicChromatogramBuilder builder;
    public BasicChromatogramFile(){
        builder = new BasicChromatogramBuilder();
    }
   
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        delegate = builder.build();
        builder =null;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBasecalls(String basecalls) {
        builder.basecalls(basecalls);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitPeaks(short[] peaks) {
        builder.peaks(peaks);
        
    }

   

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitComments(Map<String,String> comments) {
        builder.properties(comments);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAPositions(short[] positions) {
        builder.aPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCPositions(short[] positions) {
       builder.cPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGPositions(short[] positions) {
        builder.gPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTPositions(short[] positions) {
        builder.tPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public ChannelGroup getChannelGroup() {        
        return delegate.getChannelGroup();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Map<String,String> getProperties() {
        return delegate.getProperties();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Peaks getPeaks() {
        return delegate.getPeaks();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfTracePositions() {
        return delegate.getNumberOfTracePositions();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return delegate.getBasecalls();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return delegate.getQualities();
    }

  
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAConfidence(byte[] confidence) {
        builder.aConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCConfidence(byte[] confidence) {
        builder.cConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGConfidence(byte[] confidence) {
        builder.gConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTConfidence(byte[] confidence) {
        builder.tConfidence(confidence);
        
    }
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }
}
