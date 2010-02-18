/*
 * Created on Mar 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.tasm;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyTestUtil;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultPlacedRead;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultReferencedEncodedNucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTigrTasmFileParser {

    private static final String TASM_FILE = "files/23253-NP.tasm";
    TigrTasmFileParser sut = new TigrTasmFileParser();
    NucleotideEncodedGlyphs expectedConsensus = 
        new DefaultNucleotideEncodedGlyphs(
        NucleotideGlyph.getGlyphsFor(
            "GTGCCAACAAAAGAACTGAAAATCAAAATGTCCAACATGGATATTGACGGTATCAACACTGGGACAATTGACAAAGCACCGGAAGAAATAACTTCTGGAACCAGTGGGACAACCAGACCAATCATCAGACCAGCAACCCTTGCCCCACCAAGTAACAAACGAACCCGGAACCCATCCCCGGAAAGAGCAACCACAATCGGTGAAGCTGATGTCGGAAGGAAAACCCAAAAGAAACAGACCCCGACAGAGATAAAGAAAAGCGTCTACAATATGGTAGTGAAACTGGGTGAATTCTATAACCAGATGATGGTCAAAGCTGGACTTAACGATGACATGGAGAGAAACCTAA-TTCAAAATGCGCATGCTGTGGAAAGAATTCTATTGGCTGCCACTGATGACAAGAAAACTGAATTCCAAAAGAAAAAGAATGCCAGAGATGTCAAAGAAGGGAAAGAAGAAATAGATCACAACAAAACAGGGGGCACCTTTTACAAGATGGTAAGAGATGATAAAACCATCTACTTCAGCCCTATAAGAGTCACCTTTTTAAAAGAAGAGGTAAAAACAATGTACAAAACCACCATGGGGAGTGATGGCTTCAGCGGACTAAA-TCACATAATGATTGGG-CATTCACAGATGAACGATGTCTGTTTCCAAAGATCAAAGGCACTAAAAAGAGTTGGACTTGACCCTTCATTAATCAGTACCTTTGCAGGAAGCACACTCCCCAGAAGATCAGGTGCAACTGGTGTTGCGATCAAAGGAGGTGGAACTCTAGTGGCTGAAGCCATTCGATTTATAGGAAGAGCAATGGCAGACAGAGGGCTATTGAGAGACATCAAAGCCAAGACTGCGTATGAAAAGATTCTTCTGAATCTAAAAAACAAATGCTCTGCGCCCCAACAAAAGGCTCTAGTTGATCAAGTGATCGGAAGTAGAAATCCAGGGATTGCAGACATTGAAGACCTAACCCTGCTTGCTCGTAGTATGGTCGTTGTTAGGCCCTCTGTGGCGAGCAAAGTAGTGCTTCCAATAAGCATTTACGCCAAAATACCTCAACTAGGGTTCAACGTTGAAGAGTACTCTATGGTTGGGTATGAAGCCATGGCTCTTTACAATATGGCAACACCTGTTTCCATATTAAGAGTGGGAGATGATGCAAAGGACAAATCACAATTATTCTTCATGTCTTGCTTCGGAGCTGCCTATGAAGACCTGAGAGTTTTGTCTGCATTAACAGGCACAGAATTCAAGCCTAGATCAGCATTAAAATGCAAGGGTTTCCATGTTCCAGCAAAGGAACAGGTGGAAGGAATGGGGGCAGCTCTGATGTCCATCAAGCTCCAGTTTTGGGCTCCAATGACCAGATCTGGGGGAAATGAAGTAGGTGGAGACGGGGGGTCTGGCCAAATAAGTTGCAGCCCAGTGTTTGCAGTAGAAAGACCTATTGCTCTAAGCAAGCAAGCTGTAAGAAGAATGCTGTCAATGAATATTGAGGGACGTGATGCAGATGTCAAAGGAAATCTACTCAAGATGATGAATGACTCAATGGCTAAGAAAACCAATGGAAATGCTTTCATTGGGAAGAAAATGTTTCAAATATCAGACAAAAACAAAACCAATCCCGTTGAAATTCCAATTAAGCAAACCATCCCCAATTTCTTCTTTGGGAGGGACACAGCAGAGGATTATGATGACCTCGATTATTAAAGCAGCAAAATAGACACTATGACTGTGATTGTTTCAATACGTTTGGAATGTGGGTGTTT"));
    
    PlacedRead JCNGB05T28E09NP670R = new DefaultPlacedRead(
            new DefaultRead("JCNGB05T28E09NP670R",
                 new DefaultReferencedEncodedNucleotideGlyph(expectedConsensus,
                         "GTGCCAACAAAAGAACTGAAAATCAAAATGTCCAACATGGATATTGACGGTATCAACACTGGGACAATTGACAAAGCACCGGAAGAAATAACTTCTGGAACCAGTGGGACAACCAGACCAATCATCAGACCAGCAACCCTTGCCCCACCAAGTAACAAACGAACCCGGAACCCATCCCCGGAAAGAGCAACCACAATCGGTGAAGCTGATGTCGGAAGGAAAACCCAAAAGAAACAGACCCCGACAGAGATAAAGAAAAGCGTCTACAATATGGTAGTGAAACTGGGTGAATTCTATAACCAGATGATGGTCAAAGCTGGACTTAACGATGACATGGAGAGAAACCTAA-TTCAAAATGCGCATGCTGTGGAAAGAATTCTATTGGCTGCCACTGATGACAAGAAAACTGAATTCCAAAAGAAAAAGAATGCCAGAGATGTCAAAGAAGGGAAAGAAGAAATAGATCACAACAAAACAGGGGGCACCTTTTACAAGATGGTAAGAGATGATAAAACCATCTACTTCAGCCCTATAAGAGTCACCTTTTTAAAAGAAGAGGTAAAAACAATGTACAAAACCACCATGGGGAGTGA"
                         , 0,Range.buildRange(31,623)))
            , 0, SequenceDirection.REVERSE);
    
    PlacedRead JCNGB05T28E10NP706R = new DefaultPlacedRead(
            new DefaultRead("JCNGB05T28E10NP706R",
                 new DefaultReferencedEncodedNucleotideGlyph(expectedConsensus,
                         "GTGCCAACAAAAGAACTGAAAATCAAAATGTCCAACATGGATATTGACGGTATCAACACTGGGACAATTGACAAAGCACCGGAAGAAATAACTTCTGGAACCAGTGGGACAACCAGACCAATCATCAGACCAGCAACCCTTGCCCCACCAAGTAACAAACGAACCCGGAACCCATCCCCGGAAAGAGCAACCACAATCGGTGAAGCTGATGTCGGAAGGAAAACCCAAAAGAAACAGACCCCGACAGAGATAAAGAAAAGCGTCTACAATATGGTAGTGAAACTGGGTGAATTCTATAACCAGATGATGGTCAAAGCTGGACTTAACGATGACATGGAGAGAAACCTAA-TTCAAAATGCGCATGCTGTGGAAAGAATTCTATTGGCTGCCACTGATGACAAGAAAACTGAATTCCAAAAGAAAAAGAATGCCAGAGATGTCAAAGAAGGGAAAGAAGAAATAGATCACAACAAAACAGGGGGCACCTTTTACAAGATGGTAAGAGATGATAAAACCATCTACTTCAGCCCTATAAGAGTCACCTTTTTAAAAGAAGAGGTAAAAACAATGTACAAAACCACCATGGGGAGTGATGGCTTCAGCGGACTAAA-TCACATAATGATTGGG-CAT-CACAGATGAACGA-GTCCGT-CCCAAAGA"
                         , 0,Range.buildRange(2,658)))
            , 0, SequenceDirection.REVERSE);
    
    PlacedRead JCNGB05T28F04NP1144F = new DefaultPlacedRead(
            new DefaultRead("JCNGB05T28F04NP1144F",
                 new DefaultReferencedEncodedNucleotideGlyph(expectedConsensus,
                         "ATGGCACCCCCTGTTTC-ATATTAAGAGTGGGAGATGATGCAAAGGACAAATCACAATTATTCTTCATGTCTTGCTTCGGAGCTGCCTATGAAGACCTGAGAGTTTTGTCTGCATTAACAGGCACAGAATTCAAGCCTAGATCAGCATTAAAATGCAAGGGTTTCCATGTTCCAGCAAAGGAACAGGTGGAAGGAATGGGGGCAGCTCTGATGTCCATCAAGCTCCAGTTTTGGGCTCCAATGACCAGATCTGGGGGAAATGAAGTAGGTGGAGACGGGGGGTCTGGCCAAATAAGTTGCAGCCCAGTGTTTGCAGTAGAAAGACCTATTGCTCTAAGCAAGCAAGCTGTAAGAAGAATGCTGTCAATGAATATTGAGGGACGTGATGCAGATGTCAAAGGAAATCTACTCAAGATGATGAATGACTCAATGGCTAAGAAAACCAATGGAAATGCTTTCATTGGGAAGAAAATGTTTCAAATATCAGACAAAAACAAAACCAATCCCGTTGAAATTCCAATTAAGCAAACCATCCCCAATTTCTTCTTTGGGAGGGACACAGCAGAGGATTATGATGACCTCGATTATTAAAGCAGCAAAATAGACACTATGACTGTGATTGTTTCAATACGTTTGGAATGTGGGTGTTT"
                         , 1122,Range.buildRange(4,652)))
            , 1122, SequenceDirection.FORWARD);
    PlacedRead JCNGB05T28F05NP1850BR = new DefaultPlacedRead(
            new DefaultRead("JCNGB05T28F05NP1850BR",
                 new DefaultReferencedEncodedNucleotideGlyph(expectedConsensus,
                         "ATTAAAATGCAAGGGTTTCCATGTTCCAGCAAAGGAACAGGTGGAAGGAATGGGGGCAGCTCTGATGTCCATCAAGCTCCAGTTTTGGGCTCCAATGACCAGATCTGGGGGAAATGAAGTAGGTGGAGACGGGGGGTCTGGCCAAATAAGTTGCAGCCCAGTGTTTGCAGTAGAAAGACCTATTGCTCTAAGCAAGCAAGCTGTAAGAAGAATGCTGTCAATGAATATTGAGGGACGTGATGCAGATGTCAAAGGAAATCTACTCAAGATGATGAATGACTCAATGGCTAAGAAAACCAATGGAAATGCTTTCATTGGGAAGAAAATGTTTCAAATATCAGACAAAAACAAAACCAATCCCGTTGAAATTCCAATTAAGCAAACCATCCCCAATTTCTTCTTTGGGAGGGACACAGCAGAGGATTATGATGACCTCGATTATTAAAGCAGCAAAATAGACACTATGACTGTGATTGTTTCAATACGTTTGGAATGTGGGTGTTT"
                         , 1268,Range.buildRange(33,536)))
            , 1268, SequenceDirection.REVERSE);
    
    @Test
    public void parseValid(){
        Contig<PlacedRead> actual =sut.parseContigFrom(TestTigrTasmFileParser.class.getResourceAsStream(TASM_FILE));
        assertEquals(22, actual.getNumberOfReads());
        assertEquals("1118615726666", actual.getId());
        assertEquals(expectedConsensus.decode(), actual.getConsensus().decode());
        AssemblyTestUtil.assertPlacedReadCorrect(JCNGB05T28E09NP670R, actual.getPlacedReadById("JCNGB05T28E09NP670R"));
        AssemblyTestUtil.assertPlacedReadCorrect(JCNGB05T28E10NP706R, actual.getPlacedReadById("JCNGB05T28E10NP706R"));
        AssemblyTestUtil.assertPlacedReadCorrect(JCNGB05T28F04NP1144F, actual.getPlacedReadById("JCNGB05T28F04NP1144F"));
        AssemblyTestUtil.assertPlacedReadCorrect(JCNGB05T28F05NP1850BR, actual.getPlacedReadById("JCNGB05T28F05NP1850BR"));
    }
}
