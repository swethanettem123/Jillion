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
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdFileDataStoreBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdWholeReadItem;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestFakePhdReadParser {

    private static final String PHD_FILE = "files/fake.phd";
    
    private static ResourceHelper RESOURCES = new ResourceHelper(TestFakePhdReadParser.class);

    @Test
    public void parseFakeReads() throws IOException, DataStoreException{
        PhdDataStore dataStore =  new PhdFileDataStoreBuilder(RESOURCES.getFile(PHD_FILE)).build();
        Phd fakePhd = dataStore.get("HA");
        assertIsFake(fakePhd);
        assertEquals(1738, fakePhd.getNucleotideSequence().getLength());
        
        assertIsFake(dataStore.get("contig00001"));
        
        Phd realPhd = dataStore.get("FTF2AAH02G7TE3.6-91");
        assertEquals(
                "TCAGCGCGTAGTCGACGCAGCTGTCGTGTGCAGCAAAAGCAGGTAGATATTGAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCGTCCCGTCAGGCCCCCTCCAAGACCGCGATCGCGCAGAGACTTGTAAGAATGTGTTTGTCAGGGAAAAACGAAACCGACTCTTGTAGGCGGCTCATGGAAGTAGGGTCCGTAAAAGAACAAGAACCAACTCCTCGTTCACCTCCTGACTAAGGGGTAAGTTTTAGGTTAGTTTGTTGGTTCTACGCTCACCGTCGCCACGTGAGCGAGGACGTGCGACGCGTAGGTAACGGCCGTTTGTTCCGAAAACTAAGCCCGTTAACTTAGGGAAGTAGGGGTAGGTCCAACCAACATGGACGAGAGCGGTCGAACTACGTACAACGAAGGACTTAAAAGGGTAAAAGTAAACAATTACCTACTAGGGGCGGAAAAGAAGGTGGCGACCTACCTAGTTAAGTTTACTAACCTAGGTTGGCACTTAGTCACGCTGCGACTGGGTCCGTCCTATGTTACAACAGGAGTAGGGACGGTGTGACCACTGAGTAGGCGATTGGTCCCGAACGACGGACAGCGTGCGTACG" 
        		, 
        		realPhd.getNucleotideSequence().toString());
    }

    private void assertIsFake(Phd fakePhd) {
        boolean isFake=false;
       for(PhdWholeReadItem tag :fakePhd.getWholeReadItems()){
    	   List<String> lines = tag.getLines();
		for(String line : lines){
               if(line.contains("type: fake")){
                   isFake=true;
                   break;
               }
    	   }
       }
       assertTrue(isFake);
    }
}
