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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;

import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.junit.Test;

public abstract class AbstractTestSffQualityDataStore extends AbstractTestExampleSffFile{

    private final DefaultSffFileDataStore dataStore;
    
    {
        dataStore = new DefaultSffFileDataStore();
        try {
            SffParser.parseSFF(SFF_FILE, dataStore);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse sff file");
        } 
    }
    
    protected abstract QualityDataStore createSut(File sffFile) throws Exception;
    
    @Test
    public void datastoresMatch() throws Exception{
        QualityDataStore sut = createSut(SFF_FILE);
        assertEquals(sut.size(), dataStore.size());
        Iterator<String> ids = sut.getIds();
        while(ids.hasNext()){
            String id = ids.next();
            assertEquals(
                    dataStore.get(id).getQualities().decode(),
                    sut.get(id).decode());
        }
    }
}
