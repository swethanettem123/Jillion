package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class TestIndexedPhdDataStore2 extends AbstractTestPhdDataStore{

    @Override
    protected PhdDataStore createPhdDataStore(File phdfile) throws IOException{
        return IndexedPhdDataStore2.create(phdfile, DataStoreFilters.alwaysAccept());
    }

}
