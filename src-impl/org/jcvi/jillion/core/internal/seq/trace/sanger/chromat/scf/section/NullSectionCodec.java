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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;

import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogramBuilder;

public class NullSectionCodec implements SectionCodec{

    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            ScfChromatogramBuilder c) throws SectionDecoderException {
        return currentOffset;
    }

    @Override
    public EncodedSection encode(ScfChromatogram c, SCFHeader header)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor c)
            throws SectionDecoderException {
        return currentOffset;
    }

}
