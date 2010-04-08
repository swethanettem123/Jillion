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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.util.AbstractLargeIdIterator;
/**
 * {@code LargeNucleotideFastaFileDataStore} is an implementation
 * of {@link AbstractNucleotideFastaFileDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 *
 *
 */
public class LargeNucleotideFastaFileDataStore extends AbstractNucleotideFastaFileDataStore{
    private static final Pattern NEXT_ID_PATTERN = Pattern.compile("^>(\\S+)");
    private final File fastaFile;

    private Integer size;
    private boolean closed;
    
    private synchronized void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    /**
     * Construct a {@link LargeNucleotideFastaFileDataStore}
     * for the given Fasta file and the given {@link NucleotideFastaRecordFactory}.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
    public LargeNucleotideFastaFileDataStore(File fastaFile,
            NucleotideFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    /**
     * Convenience constructor using the {@link DefaultNucleotideFastaRecordFactory}.
     * This call is the same as {@link #LargeNucleotideFastaFileDataStore(File,NucleotideFastaRecordFactory)
     * new LargeNucleotideFastaFileDataStore(fastaFile,DefaultNucleotideFastaRecordFactory.getInstance());}
     * @see LargeNucleotideFastaFileDataStore#LargeQualityFastaFileDataStore(File, NucleotideFastaRecordFactory)
     */
    public LargeNucleotideFastaFileDataStore(File fastaFile) {
        super();
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    
    @Override
    public void visitRecord(String id, String comment, String entireBody) {        
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        try {
            return getRecordFor(id)!=null;
        } catch (FileNotFoundException e) {
           throw new DataStoreException("could not get record for "+id,e);
        }
    }

    @Override
    public synchronized NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> get(String id)
            throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        try {
            in = getRecordFor(id);
        
        if(in ==null){
            return null;
        }
        final DefaultNucleotideFastaFileDataStore datastore = new DefaultNucleotideFastaFileDataStore(getFastaRecordFactory());
        FastaParser.parseFasta(in, datastore);
        
        return datastore.get(id);
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get record for "+id, e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }

    @Override
    public synchronized Iterator<String> getIds() throws DataStoreException {
        checkNotYetClosed();
        try {
            return new LargeFastaIdIterator();
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get id iterator",e);
        }
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
                Scanner scanner = new Scanner(fastaFile);
                int counter =0;
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    Matcher matcher = NEXT_ID_PATTERN.matcher(line);
                    if(matcher.find()){
                        counter++;
                    }
                }
                size= counter;            
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("could not get record count");
            }
        }   
        return size;

    }

    @Override
    public synchronized void close() throws IOException {
        closed =true;
        
    }

    @Override
    public synchronized Iterator<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> iterator() {
        checkNotYetClosed();
        return new FastaIterator();
    }

    private InputStream getRecordFor(String id) throws FileNotFoundException{
        Scanner scanner = new Scanner(fastaFile);
        String expectedHeader = String.format(">%s", id);
        String line = scanner.nextLine();
        boolean done=false;
        //we have to do this while loop to make sure we find
        //the actual read instead of a different read which is happens
        //to include our id as a prefix (for example a TIGR "B" read
        while(!done){
            if(line.startsWith(expectedHeader)){
                String currentId= SequenceFastaRecordUtil.parseIdentifierFromIdLine(line);
                if(id.equals(currentId)){
                    done=true;
                    //done
                    continue;
                }
            }
            if(!scanner.hasNextLine()){
                done=true;
                continue;
            }
            line = scanner.nextLine(); 
        }
        
        if(!scanner.hasNextLine()){
            return null;
        }
        StringBuilder record = new StringBuilder(line).append("\n");
        line =scanner.nextLine();
        while(!line.startsWith(">") && scanner.hasNextLine()){
            record.append(line).append("\n");
            line = scanner.nextLine();
        }
        //add final line if needed
        if(!scanner.hasNextLine()){
            record.append(line).append("\n");
        }
        return new ByteArrayInputStream(record.toString().getBytes());
    }


    private class LargeFastaIdIterator extends AbstractLargeIdIterator{
        
        protected LargeFastaIdIterator() throws FileNotFoundException {
            super(fastaFile);
        }

        @Override
        protected void advanceToNextId(Scanner scanner) {
            //no-op
            
        }

        @Override
        protected Object getNextId(Scanner scanner) {
            
            String block= scanner.findWithinHorizon(NEXT_ID_PATTERN, 0);
            if(block !=null){
                Matcher matcher = NEXT_ID_PATTERN.matcher(block);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
            return getEndOfIterating();
        }
        
    }
    
    private class FastaIterator implements Iterator<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>>{
        private final Iterator<String> identifierIterator;

        private FastaIterator(){
            try {
                identifierIterator = getIds();
            } catch (DataStoreException e) {
               throw new IllegalStateException("could not get id iterator",e);
            }
        }
         @Override
         public boolean hasNext() {
             return identifierIterator.hasNext();
         }
     
         @Override
         public NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> next() {
             try {
                return get(identifierIterator.next());
            } catch (DataStoreException e) {
               throw new IllegalStateException("could not get next fasta record",e);
            }
         }
     
         @Override
         public void remove() {
             throw new UnsupportedOperationException("can not remove from iterator");
             
         }

    }
}
