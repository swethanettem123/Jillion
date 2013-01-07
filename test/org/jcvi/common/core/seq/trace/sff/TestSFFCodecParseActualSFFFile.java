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
 * Created on Apr 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.sff.DefaultSffFileDataStore;
import org.jcvi.common.core.seq.trace.sff.SffDecoderException;

public class TestSFFCodecParseActualSFFFile extends AbstractTestSffFileDataStore{

    @Override
    protected FlowgramDataStore parseDataStore(File file) throws SffDecoderException{
        
        InputStream in=null;
        try {
            
            return DefaultSffFileDataStore.create(file);
        } catch (IOException e) {
            throw new RuntimeException("could not open file ",e);
         }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
   
}