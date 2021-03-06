/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.examples.sam;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.sam.SamFileDataStore;
import org.jcvi.jillion.sam.SamFileDataStoreBuilder;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFlag;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqWriter;
import org.jcvi.jillion.trace.fastq.FastqWriterBuilder;

public class WriteOutAsSortedFastq {

    public static void main(String[] args) throws IOException {
        File bam = new File("/path/to/bamOrSam");

        File r1 = new File("/path/to/r1.fastq");
        File r2 = new File("/path/to/r2.fastq");
        Comparator<FastqRecord> byName = Comparator.comparing(FastqRecord::getId);
        
       try(SamFileDataStore datastore = new SamFileDataStoreBuilder(bam).build();
               
               ThrowingStream<SamRecord> stream = datastore.records();
               
               FastqWriter r1Writer = new FastqWriterBuilder(r1)
                                                   .sort(byName)
                                                   .build();
               
               FastqWriter r2Writer = new FastqWriterBuilder(r2)
                       .sort(byName)
                       .build();
               ){
           
           stream.throwingForEach(record ->{
               SamRecordFlags flags = record.getFlags();

               if(flags.contains(SamRecordFlag.FIRST_MATE_OF_PAIR)){
                   r1Writer.write(record.getQueryName(), record.getSequence(), record.getQualities());
               }else if(flags.contains(SamRecordFlag.SECOND_MATE_OF_PAIR)){
                   r2Writer.write(record.getQueryName(), record.getSequence(), record.getQualities());
               }
           });
       }
    }

}
