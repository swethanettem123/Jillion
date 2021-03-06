/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestFastQFileDataStore {
    static final FastqQualityCodec QUALITY_CODEC = FastqQualityCodec.ILLUMINA;
    DataStore<FastqRecord> sut;
    String file = "files/example.fastq";
    ResourceHelper resources = new ResourceHelper(
            TestDefaultFastQFileDataStore.class);
    FastqRecord solexa_1489 = FastqRecordBuilder.create(
            "SOLEXA1:4:1:12:1489#0/1",
            new NucleotideSequenceBuilder("TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT").build(),
            QUALITY_CODEC
                    .decode("abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S"))
    		.build();

    FastqRecord solexa_1692 = FastqRecordBuilder.create(
            "SOLEXA1:4:1:12:1692#0/1",
            new NucleotideSequenceBuilder("ACGCCTGCGTTATGGTNTAACAGGCATTCCGCCCCAGACAAACTCCCCCCCTAACCATGTCTTTCGCAAAAATCAGTCAATAAATGACCTTAACTTTAGA").build(),
            QUALITY_CODEC
                    .decode("`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB"))
           // .comment("example comment")
            .build();

    protected abstract DataStore<FastqRecord> createFastQFileDataStore(File file,FastqQualityCodec qualityCodec) throws IOException;
    @Before
    public void setup() throws IOException{
        sut = createFastQFileDataStore(resources.getFile(file), QUALITY_CODEC);
    }
    @Test
    public void size() throws DataStoreException{
        assertEquals(2, sut.getNumberOfRecords());
    }
    @Test
    public void contains() throws DataStoreException{
        assertTrue(sut.contains(solexa_1489.getId()));
    }
    @Test
    public void containQueryForIdThatIsNotContainedShouldReturnFalse() throws DataStoreException{
        assertFalse(sut.contains("notInDataStore"));
    }
    
    @Test
    public void get() throws DataStoreException{
        assertFastQRecordsEqual(solexa_1489, sut.get(solexa_1489.getId()));
        assertFastQRecordsEqual(solexa_1692, sut.get(solexa_1692.getId()));
    }
    @Test
    public void shouldThrowExceptionIfTryToGetAfterClose() throws IOException, DataStoreException{
        sut.close();
        try{
            sut.get(solexa_1489.getId());
            fail("should throw exception when get called when already closed");
        }catch(IllegalStateException e){
            //pass
        }
    }
    @Test
    public void forEach() throws IOException{
        Map<String, FastqRecord> map = new HashMap<>();
        sut.forEach(map::put);
        
        assertEquals(2, map.size());
        
        assertFastQRecordsEqual(solexa_1489, map.get(solexa_1489.getId()));
        assertFastQRecordsEqual(solexa_1692, map.get(solexa_1692.getId()));
    }
    @Test
    public void idIterator() throws DataStoreException{
        List<String> expectedIds = Arrays.asList(solexa_1489.getId(),solexa_1692.getId());
        Iterator<String> iterator = sut.idIterator();
        assertTrue(iterator.hasNext());
        for(String expectedId : expectedIds){
            assertTrue(iterator.hasNext());
            assertEquals(expectedId, iterator.next());
        }
        assertFalse(iterator.hasNext());
        try{
            iterator.next();
            fail("should throw exception after !hasNext()");
        }catch(NoSuchElementException expected ){
        }
    }
    @Test
    public void closingIdIteratorEarlyShouldHaltIterating() throws DataStoreException, IOException{
        StreamingIterator<String> iter = sut.idIterator();
        iter.next();
        iter.close();
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw exception after closing");
        }catch(NoSuchElementException expected ){
        }
    }
    @Test
    public void iterator() throws DataStoreException{
        Iterator<FastqRecord> iter = sut.iterator();
        assertTrue(iter.hasNext());
        assertFastQRecordsEqual(solexa_1489, iter.next());
        assertTrue(iter.hasNext());
        assertFastQRecordsEqual(solexa_1692, iter.next());
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw exception after !hasNext()");
        }catch(NoSuchElementException expected ){
        }
    }
    
    @Test
    public void closingIteratorEarlyShouldStopIterating() throws IOException, DataStoreException{
        StreamingIterator<FastqRecord> iter = sut.iterator();
        assertTrue(iter.hasNext());
        assertFastQRecordsEqual(solexa_1489, iter.next());
        assertTrue(iter.hasNext());
        iter.close();
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw exception after !hasNext()");
        }catch(NoSuchElementException expected ){
        }
    }

    private void assertFastQRecordsEqual(FastqRecord expected,
            FastqRecord actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNucleotideSequence(), actual
                .getNucleotideSequence());
        assertEquals(expected.getQualitySequence(), actual.getQualitySequence());
        assertTrue(ObjectsUtil.nullSafeEquals(expected.getComment(), actual
                .getComment()));
    }
}
