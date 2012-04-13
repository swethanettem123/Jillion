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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fastq.DefaultFastqFileDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.symbol.qual.DefaultEncodedPhredGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFormattingFastqRecords {
    private static final String file = "files/example.fastq";
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestFormattingFastqRecords.class);
   
    FastqQualityCodec qualityCodec = FastqQualityCodec.ILLUMINA;
    FastqDataStore expectedDataStore;
    @Before
    public void setup() throws FileNotFoundException, IOException{
        expectedDataStore = DefaultFastqFileDataStore.create(RESOURCES.getFile(file),qualityCodec);
    }
    @After
    public void tearDown() throws IOException{
        expectedDataStore.close();
    }
    @Test
    public void encodedDataCanBeReParsed() throws DataStoreException, IOException{
        FastqRecord fastq = expectedDataStore.get("SOLEXA1:4:1:12:1489#0/1");
        String encodedFastQ = fastq.toFormattedString(qualityCodec);
        ByteArrayInputStream in = new ByteArrayInputStream(encodedFastQ.getBytes());
        try{
        	FastqDataStore actualDataStore = DefaultFastqFileDataStore.create(in, qualityCodec);        
        	assertEquals(fastq, actualDataStore.get("SOLEXA1:4:1:12:1489#0/1"));
        }finally{
        	IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Test
    public void withQualityLineDuplicated() throws DataStoreException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+SOLEXA1:4:1:12:1489#0/1\n"+
                "abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S\n";
        FastqRecord fastq = expectedDataStore.get("SOLEXA1:4:1:12:1489#0/1");
        String actual = fastq.toFormattedString(qualityCodec,true);
        assertEquals(expected, actual);
    }
    @Test
    public void withoutQualityLineDuplicated() throws DataStoreException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+\n"+
                "abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S\n";
        FastqRecord fastq = expectedDataStore.get("SOLEXA1:4:1:12:1489#0/1");
        String actual = fastq.toFormattedString(qualityCodec);
        assertEquals(expected, actual);
    }
    @Test
    public void defaultCodecIsSanger() throws DataStoreException{
    	FastqRecord fastq = expectedDataStore.get("SOLEXA1:4:1:12:1489#0/1");
    	String sanger = fastq.toFormattedString(FastqQualityCodec.SANGER);
    	String defaultEncoding = fastq.toFormattedString();
    	
    	assertEquals(defaultEncoding, sanger);
    	
    }
    
}
