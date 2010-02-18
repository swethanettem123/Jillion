/*
 * Created on Jun 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.apache.log4j.Logger;
import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

/**
 * Some 454 flu mapping assemblies with very deep coverage are causing 
 * assembly errors where some reads are getting shifted.  If one of these
 * shifted reads happens to be at the end of the contig, it will go 
 * beyond the length of the consensus.  this tests
 * check to make sure AceContigBuilder ignores these reads and logs it.
 * 
 * @author dkatzel
 *
 *
 */
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestAceContigBuilderInvalidRead {

    private Logger mockLogger;
    private final String consensus = "ACGT";
    private final String contigId = "id";
    private DefaultAceContig.Builder sut;
    @Before
    public void setup(){
        mockLogger = createMock(Logger.class);
        sut = new DefaultAceContig.Builder(contigId, consensus);
        sut.logger(mockLogger);
    }
    
    @Test
    public void invalidReadShouldBeLoggedAndIgnored(){
        String readId = "readId";
        int offset =1;
        String validBases = consensus;
        
        Range clearRange = Range.buildRangeOfLength(0, validBases.length());
        PhdInfo phdInfo = createMock(PhdInfo.class);
        mockLogger.error(eq("could not add read "+readId), isA(ArrayIndexOutOfBoundsException.class));
        replay(mockLogger);
        sut.addRead(readId, validBases, offset, SequenceDirection.FORWARD, clearRange, phdInfo);
        assertEquals(sut.numberOfReads(),0);
        verify(mockLogger);
    }
}
