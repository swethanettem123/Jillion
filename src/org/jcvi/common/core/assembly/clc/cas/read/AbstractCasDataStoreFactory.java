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

package org.jcvi.common.core.assembly.clc.cas.read;

import java.io.File;

import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;

/**
 * {@code AbstractCasDataStoreFactory} is an abstract
 * implementation of {@link CasDataStoreFactory} that
 * takes into account the cas working directory to correctly
 * look up data files the cas file refers to.
 * <p/>
 * Cas files often use relative paths which makes parsing a cas file 
 * impossible unless you use the same working directory, this class
 * handles the relative pathing issues for you so subclasses
 * always have the references to the correct Files.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasDataStoreFactory implements CasDataStoreFactory{

    private final File workingDir;
    private final CasTrimMap trimMap;
    private final DataStoreFilter filter;
    /**
     * @param workingDir
     */
    public AbstractCasDataStoreFactory(File workingDir, CasTrimMap trimMap) {
        this(workingDir, trimMap, AcceptingDataStoreFilter.INSTANCE);
    }
    
    public AbstractCasDataStoreFactory(File workingDir, CasTrimMap trimMap, DataStoreFilter filter) {
        this.workingDir = workingDir;
        this.trimMap = trimMap;
        this.filter = filter;
    }

    @Override
    public final NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        File trimmedDataStore = getTrimmedFileFor(pathToDataStore);   
        return getNucleotideDataStoreFor(trimmedDataStore,filter);
    }

    private File getTrimmedFileFor(String pathToDataStore) {
        final File dataStoreFile;
        if(pathToDataStore.startsWith("/")){
            dataStoreFile = new File(pathToDataStore);
        }else{
            dataStoreFile = new File(workingDir, pathToDataStore);
        }
        File trimmedDataStore = trimMap.getUntrimmedFileFor(dataStoreFile);
        return trimmedDataStore;
    }

    /**
     * @param file
     * @return
     */
    protected abstract NucleotideDataStore getNucleotideDataStoreFor(File file,DataStoreFilter filter) throws CasDataStoreFactoryException;

    @Override
    public final QualityDataStore getQualityDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        File trimmedDataStore = getTrimmedFileFor(pathToDataStore);   
        return getQualityDataStoreFor(trimmedDataStore,filter);
    }
    /**
     * @param file
     * @return
     */
    protected abstract QualityDataStore getQualityDataStoreFor(File file,DataStoreFilter filter) throws CasDataStoreFactoryException;

    
}
