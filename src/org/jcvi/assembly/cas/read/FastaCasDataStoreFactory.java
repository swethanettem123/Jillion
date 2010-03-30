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
/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;
import java.util.Map;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fasta.LargeNucleotideFastaFileDataStore;
import org.jcvi.fasta.LargeQualityFastaFileDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.QualityDataStoreAdapter;

public class FastaCasDataStoreFactory implements
        CasDataStoreFactory {

    private final int cacheSize;
    private final Map<String,String> trimToUntrimmedMap;
    public FastaCasDataStoreFactory(Map<String,String> trimToUntrimmedMap,int cacheSize){
        this.trimToUntrimmedMap = trimToUntrimmedMap;
        this.cacheSize = cacheSize;
    }
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException {
        File actualDataStore = getUntrimmedFileFor(pathToDataStore);   
        System.out.println("getting nucleotide datastore for "+ actualDataStore.getName());
        return CachedDataStore.createCachedDataStore(NucleotideDataStore.class, 
                     new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeNucleotideFastaFileDataStore(actualDataStore))),
                     cacheSize);            
    }
    @Override
    public QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        File actualDataStore = getUntrimmedFileFor(pathToDataStore);   
        System.out.println("getting quality datastore for "+ actualDataStore.getName());
        return CachedDataStore.createCachedDataStore(QualityDataStore.class, 
                new QualityDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(actualDataStore))),
                cacheSize);  
        
    }

    private File getUntrimmedFileFor(String pathtoTrimmedDataStore){
        final File file = new File(pathtoTrimmedDataStore);
        final File filetoParse;
        String key=null;
        System.out.println(file.getAbsolutePath());
        
        for(String path : trimToUntrimmedMap.keySet()){
            File f = new File(path);
            System.out.println("\t"+f.getAbsolutePath());
            if(f.getAbsolutePath().equals(file.getAbsolutePath())){
                key=path;
            }
        }
        if(key !=null){
            filetoParse = new File(trimToUntrimmedMap.get(key));
        }else{
            filetoParse = file;
        }
        return filetoParse;
    }
}
