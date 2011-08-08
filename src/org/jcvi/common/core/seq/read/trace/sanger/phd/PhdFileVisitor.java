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
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.util.Properties;

import org.jcvi.common.core.io.TextFileVisitor;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

public interface PhdFileVisitor extends TextFileVisitor{

    void visitBeginSequence(String id);
    
    void visitEndSequence();
    
    void visitComment(Properties comments);
    
    void visitBeginDna();
    
    void visitEndDna();
    
    void visitBasecall(Nucleotide base, PhredQuality quality, int tracePosition);

    void visitBeginTag(String tagName);
    
    void visitEndTag();
    
    boolean visitBeginPhd(String id);
    
    boolean visitEndPhd();
    
}
