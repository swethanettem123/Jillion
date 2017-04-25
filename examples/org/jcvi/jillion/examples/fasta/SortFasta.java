package org.jcvi.jillion.examples.fasta;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileReader;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;

public class SortFasta {

    public static void main(String[] args) throws IOException{
        File inputFasta = new File("path/to/input.fasta");
        File sortedOutputFasta = new File("path/to/sorted/output.fasta");
        
       // sort_3_0(inputFasta, sortedOutputFasta);
        sort_5_3(inputFasta, sortedOutputFasta);
    }

    private static void sort_3_0(File inputFasta, File sortedOutputFasta) throws IOException{
        NucleotideFastaDataStore dataStore = new NucleotideFastaFileDataStoreBuilder(inputFasta)
                        .hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
                        .build();
        
        SortedSet<String> sortedIds = new TreeSet<String>();
        StreamingIterator<String> iter = null;
        try {
            iter = dataStore.idIterator();
            while (iter.hasNext()) {
                sortedIds.add(iter.next());
            }
        } finally {
            IOUtil.closeAndIgnoreErrors(iter);
        }
        NucleotideFastaWriter out = new NucleotideFastaWriterBuilder(
                sortedOutputFasta).build();
        try {
            for (String id : sortedIds) {
                out.write(dataStore.get(id));
            }
        } finally {
            IOUtil.closeAndIgnoreErrors(out, dataStore);
        }
    }
    
    private static void sort_5_3(File inputFasta, File sortedOutputFasta) throws IOException{
        
        
        try(ThrowingStream<NucleotideFastaRecord> stream = NucleotideFastaFileReader.records(inputFasta);
                
            NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(sortedOutputFasta)
                                                    .sort(Comparator.comparing(NucleotideFastaRecord::getId))
                                                    .build();
                ){
            
            stream.throwingForEach(writer::write);
            
        }
    }
}