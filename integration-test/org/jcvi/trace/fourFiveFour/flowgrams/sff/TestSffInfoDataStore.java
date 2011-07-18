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

package org.jcvi.trace.fourFiveFour.flowgrams.sff;

import java.io.File;

import org.jcvi.common.core.seq.read.trace.pyro.sff.SffDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffInfoDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.AbstractTestSffFileDataStore;

/**
 * @author dkatzel
 *
 *
 */
public class TestSffInfoDataStore extends AbstractTestSffFileDataStore{

    /**
    * {@inheritDoc}
    */
    @Override
    protected SffDataStore parseDataStore(File f) throws Exception {
        return new SffInfoDataStore(f);
    }

}
