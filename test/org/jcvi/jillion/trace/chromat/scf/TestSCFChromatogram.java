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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.internal.trace.chromat.scf.ScfChromatogramImpl;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestSCFChromatogram {


    ChannelGroup mockChannelGroup = createMock(ChannelGroup.class);
    PositionSequence mockPeaks= createMock(PositionSequence.class);
    NucleotideSequence basecalls = createMock(NucleotideSequence.class);
    QualitySequence qualities = createMock(QualitySequence.class);
    Map<String,String> expectedProperties = new HashMap<String, String>();
    QualitySequence mockInsertionConfidence= createMock(QualitySequence.class);
    QualitySequence mockDeletionConfidence= createMock(QualitySequence.class);
    QualitySequence mockSubstitutionConfidence= createMock(QualitySequence.class);
    PrivateData mockPrivateData = createMock(PrivateData.class);

    BasicChromatogram basicChromatogram = new BasicChromatogram("id",basecalls, qualities,mockPeaks, mockChannelGroup,
            expectedProperties);

    ScfChromatogramImpl sut = new ScfChromatogramImpl(basicChromatogram,
            mockSubstitutionConfidence,
            mockInsertionConfidence,
            mockDeletionConfidence,
            mockPrivateData);


    @Test
    public void constructor(){
        assertEquals(basecalls, sut.getNucleotideSequence());
        assertEquals(mockPeaks, sut.getPeakSequence());
        assertEquals(mockChannelGroup, sut.getChannelGroup());
        assertEquals(expectedProperties, sut.getComments());
        assertEquals(mockInsertionConfidence,sut.getInsertionConfidence());
        assertEquals(mockDeletionConfidence, sut.getDeletionConfidence());
        assertEquals(mockSubstitutionConfidence, sut.getSubstitutionConfidence());
        assertEquals(mockPrivateData, sut.getPrivateData());
        assertEquals(qualities, sut.getQualitySequence());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a chromatogram"));
    }

    @Test
    public void notEqualsBasicChromatogram(){
        assertFalse(sut.equals(basicChromatogram));
        assertTrue(sut.hashCode() != basicChromatogram.hashCode());
    }

    @Test
    public void equalsSameValues(){
        ScfChromatogramImpl sameValues = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsNullSubstitution(){
        ScfChromatogramImpl hasNullSubstitution = new ScfChromatogramImpl(basicChromatogram,
                null,
                mockInsertionConfidence,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullSubstitution);
    }

    @Test
    public void notEqualsDifferentSubstitution(){
    	QualitySequence differentSub = createMock(QualitySequence.class);
        ScfChromatogramImpl hasDifferentSubstitution = new ScfChromatogramImpl(basicChromatogram,
                differentSub,
                mockInsertionConfidence,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentSubstitution);
    }

    @Test
    public void notEqualsNullInsertion(){
        ScfChromatogramImpl hasNullInsertion = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                null,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullInsertion);
    }

    @Test
    public void notEqualsDifferentInsertion(){
    	QualitySequence differentInsertion = createMock(QualitySequence.class);
        ScfChromatogramImpl hasDifferentInsertion = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                differentInsertion,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentInsertion);
    }

    @Test
    public void notEqualsNullDeletion(){
        ScfChromatogramImpl hasNullDeletion = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                null,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullDeletion);
    }

    @Test
    public void notEqualsDifferentDeletion(){
    	QualitySequence differentDeletion = createMock(QualitySequence.class);
        ScfChromatogramImpl hasDifferentDeletion = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                differentDeletion,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentDeletion);
    }

    @Test
    public void notEqualsNullPrivateData(){
        ScfChromatogramImpl hasNullPrivateData = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                mockDeletionConfidence,
                null);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullPrivateData);
    }

    @Test
    public void notEqualsDifferentPrivateData(){
    	PrivateData differentPrivateData = createMock(PrivateData.class);
        ScfChromatogramImpl hasDifferentPrivateData = new ScfChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                mockDeletionConfidence,
                differentPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentPrivateData);
    }

}
