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

package org.jcvi.common.core.assembly.clc.cas;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.assembly.cas.Cas2Consed3;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.ace.AceAssembledRead;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileContigDataStore;
import org.jcvi.common.core.assembly.ace.AceFileDataStoreBuilder;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStoreUtil;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.DirectoryFileServer;
import org.jcvi.common.io.fileServer.DirectoryFileServer.TemporaryDirectoryFileServer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
public class TestCas2Consed3 {

	 private final ResourceFileServer RESOURCES = new ResourceFileServer(TestCas2Consed3.class); 
	 private ContigDataStore<AssembledRead, Contig<AssembledRead>> expectedDataStore;
	   private String prefix = "cas2consed3";
	 
	 TemporaryDirectoryFileServer tempDir;
	 @Before
	    public void setup() throws IOException{
	        expectedDataStore = DefaultContigFileDataStore.create(RESOURCES.getFile("files/expected.contig"));
	        tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer();
	   	    
	 }
	    
	    @Test
	    public void parseCas() throws IOException, DataStoreException{
	        File casFile = RESOURCES.getFile("files/flu.cas");
	      Cas2Consed3 cas2consed3 = new Cas2Consed3(casFile, tempDir,prefix,true,false);
	      cas2consed3.convert(TrimPointsDataStoreUtil.createEmptyTrimPointsDataStore(),new UnTrimmedExtensionTrimMap(),FastqQualityCodec.ILLUMINA, false,false,false);
	      
	      File aceFile = tempDir.getFile("edit_dir/"+prefix+".ace.1");
	      AceFileContigDataStore dataStore =new AceFileDataStoreBuilder(aceFile)
												.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED)
												.build();
	        assertEquals("# contigs", expectedDataStore.getNumberOfRecords(), dataStore.getNumberOfRecords());
	        StreamingIterator<AceContig> iter = dataStore.iterator();
	        try{
		        while(iter.hasNext()){
		        	AceContig contig = iter.next();
		    	  Contig<AssembledRead> expectedContig= getExpectedContig(contig.getId());
		    	  assertEquals("consensus", expectedContig.getConsensusSequence(),
		    			  contig.getConsensusSequence());
		    	  assertEquals("# reads", expectedContig.getNumberOfReads(), contig.getNumberOfReads());
		    	  
		    	  assertReadsCorrectlyPlaced(contig, expectedContig);
		      }
	        }finally{
	        	IOUtil.closeAndIgnoreErrors(iter);
	        }
	    }

		private void assertReadsCorrectlyPlaced(AceContig contig,
				Contig<AssembledRead> expectedContig) {
			StreamingIterator<AceAssembledRead> iter = null;
			try{
				iter = contig.getReadIterator();
				while(iter.hasNext()){
					AceAssembledRead actualRead = iter.next();
				String readId = actualRead.getId();
				AssembledRead expectedRead = expectedContig.getRead(readId);
				assertEquals("read basecalls", expectedRead
						.getNucleotideSequence(), actualRead
						.getNucleotideSequence());
				assertEquals("read offset", expectedRead.getGappedStartOffset(),
						actualRead.getGappedStartOffset());

				}
			}finally{
				IOUtil.closeAndIgnoreErrors(iter);
			}
		}
	    /**
	     * cas2Consed now appends coordinates to the end of the contig
	     * if they don't get full reference length, stip that out 
	     * to get the corresponding expected flap assembly which
	     * doesn't do that.
	     */
	    private Contig<AssembledRead> getExpectedContig(String actualContigId) throws DataStoreException{
	        String IdWithoutCoordinates = actualContigId.replaceAll("_.+", "");
	        return expectedDataStore.get(IdWithoutCoordinates);
	    }
}
