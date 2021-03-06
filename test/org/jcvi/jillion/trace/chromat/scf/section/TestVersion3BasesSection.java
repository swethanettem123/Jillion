/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
/*
 * Created on Sep 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.section.AbstractBasesSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version3BasesSectionCodec;


public class TestVersion3BasesSection extends AbstractTestBasesSection{


    @Override
    protected ByteBuffer createRequiredExpectedEncodedBases(){
        ByteBuffer result = ByteBuffer.wrap(new byte[(int)encodedBases.getLength()*12]);
        bulkPutAsInts(result, peaks);
        result.put(aConfidence);
        result.put(cConfidence);
        result.put(gConfidence);
        result.put(tConfidence);
        result.put(DECODED_BASES.getBytes());
        return result;
    }


    @Override
    protected InputStream createValidMockInputStreamWithoutOptionalConfidence(long skipDistance)
    throws IOException {
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read()).andReturn(1);
         expect(mockInputStream.skip(skipDistance-1)).andReturn(skipDistance-1);
         //do peaks
         expectPeakReads(mockInputStream);
         expectRequiredConfidenceReads(mockInputStream);
         expectBasesRead(mockInputStream);
         //empty substitution, insert, and deletion confidences
         expectEmptyConfidenceData(mockInputStream);
         expectEmptyConfidenceData(mockInputStream);
         expectEmptyConfidenceData(mockInputStream);
        return mockInputStream;
    }
    @Override
    protected InputStream createValidMockInputStreamWithOptionalConfidence(long skipDistance)
    throws IOException {
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read()).andReturn(1);
         expect(mockInputStream.skip(skipDistance-1)).andReturn(skipDistance-1);
         //do peaks
         expectPeakReads(mockInputStream);
         expectRequiredConfidenceReads(mockInputStream);
         expectBasesRead(mockInputStream);
         expectFullConfidenceRead(mockInputStream, subsitutionConfidence);
         expectFullConfidenceRead(mockInputStream, insertionConfidence);
         expectFullConfidenceRead(mockInputStream, deletionConfidence);

        return mockInputStream;
    }
    private void expectEmptyConfidenceData(InputStream mockInputStream) throws IOException {
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)encodedBases.getLength()))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(EMPTY_CONFIDENCE));

    }
    private void expectBasesRead(InputStream mockInputStream) throws IOException {
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)encodedBases.getLength()))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(DECODED_BASES.getBytes()));

    }
    private void expectFullConfidenceRead(InputStream mockInputStream,byte[] confidence) throws IOException{

        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(confidence.length))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(confidence));
    }
    private void expectRequiredConfidenceReads(InputStream mockInputStream) throws IOException {
        expectFullConfidenceRead(mockInputStream, aConfidence);
        expectFullConfidenceRead(mockInputStream, cConfidence);
        expectFullConfidenceRead(mockInputStream, gConfidence);
        expectFullConfidenceRead(mockInputStream, tConfidence);


    }

    @Override
    protected ByteBuffer createEncodedBasesWithAllOptionalData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(subsitutionConfidence);
        expectedRequiredExpectedEncodedBases.put(insertionConfidence);
        expectedRequiredExpectedEncodedBases.put(deletionConfidence);
        return expectedRequiredExpectedEncodedBases;
    }
    @Override
    protected ByteBuffer createEncodedBasesWithoutSubstutionData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(EMPTY_CONFIDENCE);
        expectedRequiredExpectedEncodedBases.put(insertionConfidence);
        expectedRequiredExpectedEncodedBases.put(deletionConfidence);
        return expectedRequiredExpectedEncodedBases;
    }
    @Override
    protected ByteBuffer createEncodedBasesWithoutDeletionData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(subsitutionConfidence);
        expectedRequiredExpectedEncodedBases.put(insertionConfidence);
        expectedRequiredExpectedEncodedBases.put(EMPTY_CONFIDENCE);
        return expectedRequiredExpectedEncodedBases;
    }
    @Override
    protected ByteBuffer createEncodedBasesWithoutInsertionData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(subsitutionConfidence);
        expectedRequiredExpectedEncodedBases.put(EMPTY_CONFIDENCE);
        expectedRequiredExpectedEncodedBases.put(deletionConfidence);
        return expectedRequiredExpectedEncodedBases;
    }


    @Override
    protected AbstractBasesSectionCodec createAbstractBasesSectionHandler() {
        return new Version3BasesSectionCodec();
    }
}
