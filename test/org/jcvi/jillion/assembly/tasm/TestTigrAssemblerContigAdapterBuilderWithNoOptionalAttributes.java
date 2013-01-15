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

package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ContigDataStore;
import org.jcvi.jillion.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.jillion.assembly.tasm.DefaultTasmFileContigDataStore;
import org.jcvi.jillion.assembly.tasm.TasmContig;
import org.jcvi.jillion.assembly.tasm.TasmContigAdapter;
import org.jcvi.jillion.assembly.tasm.TasmContigAttribute;
import org.jcvi.jillion.assembly.tasm.TasmContigDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Test;
public class TestTigrAssemblerContigAdapterBuilderWithNoOptionalAttributes {

	 private static final ResourceHelper RESOURCES = new ResourceHelper(TestTigrAssemblerContigDataStore.class);
	    
	    private static final ContigDataStore<AssembledRead, Contig<AssembledRead>> contigDataStore;
	    private static final TasmContigDataStore tasmDataStore;
	    static{
	        try {
	            contigDataStore= DefaultContigFileDataStore.create(RESOURCES.getFile("files/giv-15050.contig"));
	        } catch (Exception e) {
	            throw new IllegalStateException("could not parse contig file",e);
	        } 
	        try {
	            tasmDataStore= DefaultTasmFileContigDataStore.create(RESOURCES.getFile("files/giv-15050.tasm"));
	        } catch (Exception e) {
	            throw new IllegalStateException("could not parse contig file",e);
	        } 
	    }
	    
	    @Test
	    public void adaptPB2() throws DataStoreException{
	    	Contig<AssembledRead> contig =contigDataStore.get("15044");
	    	TasmContig tasm =tasmDataStore.get("1122071329926");
	    	
	    	TasmContigAdapter sut = new TasmContigAdapter.Builder(contig)
	    									.build();
	    	assertEquals(contig.getId(), sut.getId());
	    	assertEquals(contig.getConsensusSequence(), sut.getConsensusSequence());
	    	assertEquals(contig.getNumberOfReads(), contig.getNumberOfReads());
	    	TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);
	    	assertRequiredAttributesAreEqual(tasm, sut);
	    }
	    
	    private void assertRequiredAttributesAreEqual(TasmContig expected, TasmContigAdapter actual){
	    	//apparently pull_contig sets the asmb_id to the ca_contig_id if present which will throw off our
	    	//asmbl_id check
	    	//	assertAttributeValueEquals(TigrAssemblerContigAttribute.ASMBL_ID,expected, actual);
	    	assertAttributeValueEquals(TasmContigAttribute.UNGAPPED_CONSENSUS,expected, actual);
	    	assertAttributeValueEquals(TasmContigAttribute.GAPPED_CONSENSUS,expected, actual);
	    	assertAttributeValueEquals(TasmContigAttribute.PERCENT_N,expected, actual);
	    	assertAttributeValueEquals(TasmContigAttribute.NUMBER_OF_READS,expected, actual);
	    	assertAttributeValueEquals(TasmContigAttribute.IS_CIRCULAR,expected, actual);
	    	
	    	//avg coverage is actually computed by java common and estimated by legacy TIGR tools
	    	//so be flexible with rounding errors
	    	assertEquals(Float.parseFloat(expected.getAttributeValue(TasmContigAttribute.AVG_COVERAGE)), 
	    			Float.parseFloat(actual.getAttributeValue(TasmContigAttribute.AVG_COVERAGE)),
	    			.1F);
	    }
	    private void assertAttributeValueEquals(TasmContigAttribute attribute,TasmContig expected,TasmContig actual ){
	    	assertEquals(expected.getAttributeValue(attribute), actual.getAttributeValue(attribute));
	    }
}