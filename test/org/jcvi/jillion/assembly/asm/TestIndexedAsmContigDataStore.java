package org.jcvi.jillion.assembly.asm;

import java.io.File;

import org.jcvi.jillion.assembly.asm.AsmContigDataStore;
import org.jcvi.jillion.assembly.asm.IndexedAsmContigDataStore;
import org.jcvi.jillion.trace.frg.FragmentDataStore;

public class TestIndexedAsmContigDataStore extends AbstractTestAsmContigDataStore{

	@Override
	protected AsmContigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return IndexedAsmContigDataStore.createDataStore(asmFile, frgDataStore);
	}

}