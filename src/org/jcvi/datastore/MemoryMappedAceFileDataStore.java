/*
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AbstractAceFileDataStore;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.util.MemoryMappedFileRange;

public class MemoryMappedAceFileDataStore extends AbstractAceFileDataStore implements ContigDataStore<AcePlacedRead, AceContig>{
    private final MemoryMappedFileRange memoryMappedFileRange;
    private final File file;
    private int currentStartOffset;
    private int currentLineLength;
    private int currentFileOffset;
    
    public MemoryMappedAceFileDataStore(File file, MemoryMappedFileRange memoryMappedFileRange ){
        this.memoryMappedFileRange = memoryMappedFileRange;
        this.file = file;
    }


    @Override
    public void visitLine(String line) {        
        super.visitLine(line);
        final int length = line.length();
        currentLineLength = length;
        currentFileOffset+=length;
        
    }

    @Override
    public void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        super.visitContigHeader(contigId, numberOfBases, numberOfReads,
                numberOfBaseSegments, reverseComplimented);
        currentStartOffset=currentFileOffset-currentLineLength;
    }

    @Override
    protected void visitContig(AceContig contig) {
        memoryMappedFileRange.put(contig.getId(), Range.buildRange(currentStartOffset, currentFileOffset));
        currentStartOffset=currentFileOffset+1;
    }

    @Override
    public boolean contains(String contigId) throws DataStoreException {
        return memoryMappedFileRange.contains(contigId);
    }

    @Override
    public AceContig get(String contigId) throws DataStoreException {
        Range range = memoryMappedFileRange.getRangeFor(contigId);
        
        try {
            DefaultAceFileDataStore visitor = new DefaultAceFileDataStore();
            final InputStream inputStream = MemoryMappedUtil.createInputStreamFromFile(file,range);
            AceFileParser.parseAceFile(inputStream,visitor);
            return visitor.get(contigId);
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        } 
    }

    @Override
    public Iterator<String> getIds() {
        return memoryMappedFileRange.getIds();
    }

    @Override
    public int size() {
        return memoryMappedFileRange.size();
    }

    @Override
    public void close() throws IOException {
        memoryMappedFileRange.close();
        
    }

    

    @Override
    public Iterator<AceContig> iterator() {
        return new DataStoreIterator<AceContig>(this);
    }
}
