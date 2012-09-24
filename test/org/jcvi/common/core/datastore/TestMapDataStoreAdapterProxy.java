package org.jcvi.common.core.datastore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestMapDataStoreAdapterProxy {

	Map<String, NucleotideSequence> map = new HashMap<String, NucleotideSequence>();
	
	NucleotideSequenceDataStore sut;
	public TestMapDataStoreAdapterProxy(){
		map.put("read1", new NucleotideSequenceBuilder("ACGTACGT")
								.build());
		map.put("read2", new NucleotideSequenceBuilder("AAAACCCCGGGGTTT")
							.build());
		
		
	}
	@Before
	public void createSut(){
		sut = MapDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, map);
	}
	@Test
	public void instanceOf(){
		assertTrue(sut instanceof NucleotideSequenceDataStore);
	}
	
	@Test
	public void get() throws DataStoreException{
		assertEquals(map.get("read1"), sut.get("read1"));
	}
	
	@Test
	public void size() throws DataStoreException{
		assertEquals(map.size(), sut.getNumberOfRecords());
	}
	
	@Test
	public void close() throws IOException{
		assertFalse(sut.isClosed());
		sut.close();
		assertTrue(sut.isClosed());
	}
}