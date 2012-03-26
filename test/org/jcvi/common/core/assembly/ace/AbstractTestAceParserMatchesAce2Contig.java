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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

public abstract class  AbstractTestAceParserMatchesAce2Contig {
    List<AceContig> actualContigs;
    DefaultContigFileDataStore expectedContigDataStore;
    ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestAceParserMatchesAce2Contig.class);
    private final String pathToAceFile;
    AbstractTestAceParserMatchesAce2Contig(String aceFile, String contigFile) throws IOException{
        this.expectedContigDataStore = new DefaultContigFileDataStore(
        		RESOURCES.getFileAsStream(contigFile));
        pathToAceFile = aceFile;
        this.actualContigs = getContigList(
        		RESOURCES.getFile(aceFile));       
    }
    protected abstract List<AceContig> getContigList(File aceFile) throws IOException;
    
    @Test
    public void assertParsedAceFileMatchedParsedContigFile() throws DataStoreException{
        assertContigsParsedCorrectly(actualContigs);
    }
   
    protected File getAceFile() throws IOException{
    	return RESOURCES.getFile(pathToAceFile);
    }

    private void assertContigsParsedCorrectly(List<AceContig> actual) throws DataStoreException {
        assertEquals(expectedContigDataStore.size(), actual.size());
        for(AceContig actualAceContig : actual){
            Contig<PlacedRead> expectedContig = expectedContigDataStore.get(actualAceContig.getId());
            AceContigTestUtil.assertContigsEqual(expectedContig, actualAceContig);
        }
        
    }

  
}
