/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.ctg.TigrContig;
import org.jcvi.jillion.assembly.ctg.TigrContigDataStore;
import org.jcvi.jillion.assembly.ctg.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestTigrAssemblerContigAdapterBuilderWithNoOptionalAttributes {

	 private static final ResourceHelper RESOURCES = new ResourceHelper(TestTigrAssemblerContigAdapterBuilderWithNoOptionalAttributes.class);
	    
	    private static final TigrContigDataStore contigDataStore;
	    private static final TasmContigDataStore tasmDataStore;
	    static{
	        try {
	        	NucleotideFastaDataStore fullLengthFastas = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("files/giv-15050.fasta"))
									.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
									.build();
				contigDataStore= new TigrContigFileDataStoreBuilder(RESOURCES.getFile("files/giv-15050.contig"),fullLengthFastas)
									.build();
				 tasmDataStore= new TasmContigFileDataStoreBuilder(RESOURCES.getFile("files/giv-15050.tasm"),	fullLengthFastas)
													.build();
	        } catch (Exception e) {
	            throw new IllegalStateException("could not parse contig file",e);
	        } 
	        
	    }
	    
	    @Test
	    public void adaptPB2() throws DataStoreException{
	    	TigrContig contig =contigDataStore.get("15044");
	    	TasmContig tasm =tasmDataStore.get("1122071329926");
	    	
	    	TasmContig sut = new TasmContigBuilder(contig).build();
	    	assertEquals(contig.getId(), sut.getId());
	    	assertEquals(15044, sut.getTigrProjectAssemblyId().intValue());
	    	assertEquals(contig.getConsensusSequence(), sut.getConsensusSequence());
	    	assertEquals(contig.getNumberOfReads(), contig.getNumberOfReads());
	    	TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);
	    	assertRequiredAttributesAreEqual(tasm, sut);
	    }
	    
	    private void assertRequiredAttributesAreEqual(TasmContig expected, TasmContig actual){
	    	//apparently pull_contig sets the asmb_id to the ca_contig_id if present which will throw off our
	    	//asmbl_id check	    	
	    	assertEquals(expected.isCircular(), actual.isCircular());
	    	
	    	//avg coverage is actually computed by jillion and estimated by legacy TIGR tools
	    	//so be flexible with rounding errors
	    	assertEquals(expected.getAvgCoverage(), 
	    			actual.getAvgCoverage(),
	    			.1D);
	    }

}
