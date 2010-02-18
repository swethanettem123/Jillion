/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestSequenceFastaMap {

    protected static final String FASTA_FILE_PATH = "files/19150.fasta";
   
    DefaultEncodedNucleotideFastaRecord contig_1 = new DefaultEncodedNucleotideFastaRecord(1, "47 2313 bases, 00000000 checksum.",
            "AACCATTTGAATGGATGTCAATCCGACTTTACTTTTCTTGAAAGTTCCAGYGCAAAATGC"+
            "CATAAGCACCACATTCCCATACACTGGAGATCCTCCATACAGCCATGGAACGGGAACAGG"+
            "ATACACCATGGACACAGTCAACAGAACACATCAATATTCAGAAAAGGGGAAATGGACAAC"+
            "AAACACAGARACYGGAGCACCACAACTTAACCCAATTGATGGACCATTACCTGAGGATAA"+
            "TGAGCCAAGTGGATATGCACAAACAGATTGTGTCCTGGAAGCAATGGCTTTCCTTGAAGA"+
            "GTCCCACCCAGGAATCTTTGAAAACTCGTGTCTCGAAACGATGGAAGTTGTTCAGCAAAC"+
            "AAGAGTGGACAAGCTGACTCAAGGTCGCCAGACCTATGATTGGACATTGAACAGGAATCA"+
            "GCCGGCTGCAACTGCATTAGCTAATACTATAGAGGTTTTCAGATCGAACGGTCTAACGGC"+
            "CAATGAATCAGGAAGGCTGATAGACTTCCTCAAGGATGTGATGGAATCAATGGACAAAGA"+
            "AGACATGGAAATAACAACGCACTTCCAAAGAAAGAGAAGAGTAAGGGACAACATGACCAA"+
            "AAAAATGGTCACACAAAGAACAATAGGAAAGAAGAAGCAGAGATTAAACAAGAGAAGTTA"+
            "CTTAATAAGGGCATTGACACTGAACACAATGACAAAAGATGCTGAAAGAGGCAAGTTAAA"+
            "RAGAAGAGCAATTGCGACACCCGGAATGCAAATCAGAGGATTTGTGTATTTTGTTGAAAC"+
            "ATTGGCGAGAAGCATCTGTGAGAAGCTTGAACAGTCTGGGCTCCCAGTCGGAGGCAATGA"+
            "AAAGAAGGCTAAACTGGCAAATGTCGTGAGGAAAATGATGACTAACTCACAGGACACAGA"+
            "GCTTTCTTTCACAATCACTGGAGACAACACCAAATGGAATGAAAATCAGAACCCTAGAAT"+
            "GTTTCTGGCAATGATAACATACATAACAAGAAATCAACCTGAATGGTTCAGGAATGTCTT"+
            "GAGCATCGCACCTATAATGTTCTCGAATAAAATGGCAAGGCTRGGGAAAGGATACATGTT"+
            "TGAAAGCAAGAGCATGAAGCTTCGAACACAGGTATCAGCAGAAATGCTAGCAAATATTGA"+
            "CCTGAAATATTTCAATGAGTCAACAAAAAAGAAAATAGAGAAGATAAGGCCTCTTTTAAT"+
            "AGAGGGCACAGCCTCATTGAGTCCCGGAATGATGATGGGCATGTTCAACATGCTAAGCAC"+
            "AGTTTTAGGAGTTTCAATCCTAAATCTGGGACAAAAGAGATACACCAAAACAACGTATTG"+
            "GTGGGACGGACTCCARTCCTCCGATGACTTTGCTCTCATAGTGAATGCACCGAATCATGA"+
            "GGGAATACAAGCAGGAGTAGATAGATTCTATAGGACTTGCAAACTAGTCGGAATCAATAT"+
            "GAGCAAAAAGAAGTCCTACATAAACAGGACAGGAACGTTTGAATTCACAAGCTTTTTCTA"+
            "TCGCTATGGGTTCGTAGCCAATTTCAGCATGGAACTGCCCAGCTTTGGAGTGTCTGGGAT"+
            "CAATGAATCAGCTGACATGAGCATTGGGGTAACAGTGATAAAGAACAACATGATAAACAA"+
            "TGACCTTGGGCCAGCAACGGCCCAAATGGCTCTCCAGCTGTTCATCAAGGATTACAGATA"+
            "TACATACCGGTGCCACAGAGGGGACACACAAATCCAGACAAGGAGATCATTCGAACTGAA"+
            "GAAATTATGGGAACAAACCCGATCAAAGGCAGGGCTGCTGGTTTCCGATGGGGGACCAAA"+
            "CCTGTACAATATCCGAAATCTCCACATCCCGGAGGTCTGCCTGAAATGGGAGCTGATGGA"+
            "CGAAGAATATCAGGGAAGGCTTTGTAATCCCTTGAACCCATTTGTCAGCCATAAGGAGAT"+
            "AGAGTCTGTGAACAGTGCAGTGGTGATGCCAGCTCACGGCCCAGCCAAAAGCATGGAATA"+
            "TGATGCTGTTGCTACTACGCACTCCTGGATCCCCAAGAGGAATCGCTCCATTCTTAACAC"+
            "GAGTCAAAGGGGAATCCTCGAAGATGAACAGATGTATCAAAAGTGCTGCAATCTATTCGA"+
            "AAAGTTCTTCCCTAGCAGTTCGTACAGAAGACCGGTCGGGATTTCTAGCATGGGGGAGGC"+
            "CATGGTGTCCAGGGCCCGAATTGATGCTCGAATTGACTTCGAATCTGGACGGATTAAGAA"+
            "AGAGGAGTTTGCTGAGATCATGAAGATCTGTTCCACCATTGAAGAACTCAGACGGCAGAA"+
            "ATAGTGAATTTAGCTTGTCCTTCATGAAA"
    );
    
    DefaultEncodedNucleotideFastaRecord contig_5 = new DefaultEncodedNucleotideFastaRecord(5, "19 995 bases, 00000000 checksum.",
            "ATGTTTAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCATCCCATC" +
            "AGGCCCCCTCAAAGCCGAGATCGCGCAGAGACTTGAAGATGTTTTTGCAGGGAAGAACAC" +
            "AGATCTTGAGGCACTCATGGAATGGCTAAAGACAAGACCAATCCTGTCACCTCTGACTAA" +
            "GGGGATTTTAGGATTTGTGTTCACGCTCACCGTGCCCAGTGAGCGAGGACTGCAGCGTAG" +
            "ACGCTTTGTCCAAAATGCTCTTAATGGGAATGGAGATCCAAATAACATGGACAGGGCAGT" +
            "CAAACTGTACAGGAAATTAAAAAGGGAAATTACATTCCATGGGGCCAAAGAGGTAGCACT" +
            "CAGTTATTCCACTGGTGCACTTGCCAGTTGCATGGGCCTTATATACAACAGAATGGGAAC" +
            "TGTGACCACTGAAGGGGCATTTGGCCTGGTGTGCGCCACGTGTGAACAGATTGCTGACTC" +
            "CCAGCATCGGTCCCACAGACAGATGGTGACAACAACCAACCCACTGATCAAACATGAAAA" +
            "CAGAATGGTACTGGCTAGTACTACAGCTAAAGCCATGGAACAGGTGGCAGGGTCAAGTGA" +
            "ACAGGCAGCAGAGGCTATGGAGGTTGCCAGTCAGGCTAGGCAGATGGTGCAGGCGATGAG" +
            "GACCATTGGGACTCATCCTAGCTCCAGTGCCGGTCTAAGAGATGATCTTCTTGAAAATTT" +
            "GCAGGCCTATCAGAAAAGGATGGGAGTGCAATTGCAGCGATTCAAGTGATCCTCTCGTCA" +
            "TTGCCGCAAGTATCATTGGAATCTTGCACTTGATATTGTGGATTCTTGATCGCCTTTTTT" +
            "TCAAATGCATTCATCGTCGCCTTAAATACGGGTTGAAACGAGGGCCTTCTACGGAAGGAG" +
            "TGCCTAAGTCTATGAGGGAGGAATATCGGCAGGAACAGCAGAGCGCTGTGGATGTTGACG" +
            "ATGGTCATTTTGTCAACATAGAGCTGGAGTAAA"
    );
    DefaultEncodedNucleotideFastaRecord contig_9 = new DefaultEncodedNucleotideFastaRecord(9, "48 2311 bases, 00000000 checksum.",
            "AATATATTCAATATGGAGAGAATAAAAGAACTGAGAGATCTAATGTCACAGTCTCGCACC" +
            "CGCGAGATACTMACCAAAACCACTGTGGACCACATGGCCATAATCAAAAAATACACATCA" +
            "GGAAGGCAAGAGAAGAACCCCGCACTTAGAATGAAGTGGATGATGGCAATGAAATATCCA" +
            "ATTACAGCAGATAAGAGAATAATGGAAATGATTCCTGAAAGGAATGAACAAGGACAAACT" +
            "CTCTGGAGCAAAACAAACGATGCCGGCTCAGACCGAGTGATGGTATCACCTCTGGCTGTT" +
            "ACATGGTGGAATAGGAATGGACCAACAACAAGTACAGTTCATTACCCAAAGATATATAAG" +
            "ACCTATTTCGAAAAAGTCGAAAGGTTGAAACACGGGACCTTTGGCCCTGTTCACTTCAGA" +
            "AATCAAGTTAAAATAAGACGGAGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCC" +
            "AAAGAGGCACAGGATGTAATCATGGAAGTTGTTTTCCCTAATGAAGTGGGAGCGAGAATA" +
            "CTAACATCAGAATCGCAACTGACGATAACAAAAGAGAAGAAAGAGGAACTGCAGGACTGC" +
            "AAAATTGCCCCTCTGATGGTTGCATACATGCTGGAAAGAGAGTTGGTCCGCAAAACGAGA" +
            "TTTCTCCCAGTGGCTGGTGGAACAAGCAGTGTCTATATTGAAGTGCTGCATTTAACCCAG" +
            "GGGACATGCTGGGAGCAGATGTACACCCCAGGAGGGGARGTGAGAAATGATGATATTGAC" +
            "CAAAGCTTGATTATCGCTGCAAGGAACATAGTAAGAAGAGCAACAGTATCAGCAGACCCA" +
            "CTAGCATCTCTATTGGAGATGTGCCACAGCACACAGATCGGGGGGGTAAGGATGGTAGAC" +
            "ATTCTTCGGCAAAATCCAACAGAGGAACAAGCCGTGGACATATGCAAGGCAGCATTGGGC" +
            "TTAAGGATTAGCTCGTCTTTTAGCTTTGGTGGATTCACTTTCAAAAGAACAAGCGGATCG" +
            "TCAGTTGGGAGAGAAGAAGAAGTGCTTACGGGCAACCTTCAAACATTGAAAATAAGAGTA" +
            "CATGAGGGGTATGAAGAGTTCACAATGATTGGGAGGAGAGCAACAGCTATTCTCAGGAAA" +
            "GCAACCAGAAGATTGATCCAGCTAATAGTAAGYGGGAGAGACGAGCAGTCAATTGCTGAG" +
            "GCAATAATTGTGGCCATGGTATTTTCACAAGAAGATTGCATGATCAAGGCAGTTCGGGGT" +
            "GACCTGAACTTTGTCAATAGGGCAAACCAGCGACTGAACCCAATGCATCAACTCTTGAGA" +
            "CACTTCCAAAAGGATGCAAAAGTGCTTTTCCAAAACTGGGGAATTGARCCCATTGACAAT" +
            "GTAATGGGAATGATCGGAATATTGCCCGACATGACCCCAAGTACTGAGATGTCGCTGAGG" +
            "GGGATAAGAGTCAGTAAGATGGGAGTAGATGAATACTCCAGCACAGAGAGGGTGACAGTG" +
            "AGCATTGACCGATTTTTAAGAGTTCGGGACCAACGGGGGAACGTACTATTGTCACCCGAA" +
            "GAAGTCAGCGAGACACAAGGAACAGAAAAGCTGACAATAACTTACTCGTCATCAATGATG" +
            "TGGGAAATTAATGGTCCTGAGTCAGTGTTGGTCAATACTTATCAGTGGATCATCAGAAAT" +
            "TGGGAAACYGTGAAAATTCAATGGTCACAGGATCCCACAATTTTRTATAACAAGATGGAA" +
            "TTCGAGCCATTTCAGTCTCTGGTCCCTAAGGCAGCCAGAGGTCAGTACAGTGGATTCGTG" +
            "AGGACACTATTCCAGCAGATGCGGGATGTGCTTGGGACGTTTGACACTGTCCAGATAATA" +
            "AAACTTCTCCCCTTTGCTGCTGCCCCACCAGAACAGAGTAGGATGCAGTTCTCCTCCTTG" +
            "ACTGTGAATGTGAGAGGATCAGGGATGAGGATACTGGTGAGAGGCAATTCTCCAGTGTTC" +
            "AATTACAACAAGGCCACCAAGAGACTTACGGTTCTCGGGAAAGATGCAGGTGCATTGACC" +
            "GAAGATCCAGATGAAGGCACAGCTGGAGTAGAGTCTGCTGTTTTAAGAGGTTTCCTCATT" +
            "TTGGGCAAAGAAGACAAGAGATACGGCCCAGCATTGAGCATCAATGAACTGAGCAATCTT" +
            "GCAAAGGGAGAAAAGGCTAATGTGCTAATTGGGCAAGGAGACGTGGTGTTGGTAATGAAA" +
            "CGGAAACGGGACTCTAGCATACTTACTGACAGCCAGACAGCGACCAAAAGGATTCGGATG" +
            "GCCATCAATTAATGTCGAATTGTTTAA"
    );
    protected File getFile() {
        return new File(AbstractTestSequenceFastaMap.class.getResource(FASTA_FILE_PATH).getFile());
    }
    protected InputStream getFileAsStream(){
        return AbstractTestSequenceFastaMap.class.getResourceAsStream(FASTA_FILE_PATH);
    }
    
    @Test
    public void parseFile() throws IOException, DataStoreException{
        
        DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> sut = buildSequenceFastaMap(getFile());
        assertParsedCorrectly(sut);
    }
    
    protected abstract DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> buildSequenceFastaMap(File file) throws IOException;
    protected void assertParsedCorrectly(DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> sut) throws DataStoreException {
        assertEquals(9, sut.size());
        final NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> nucleotideSequenceFastaRecord = sut.get("1");
        System.out.println(nucleotideSequenceFastaRecord);
        assertEquals(contig_1, nucleotideSequenceFastaRecord);
        assertEquals(contig_5, sut.get("5"));
        assertEquals(contig_9, sut.get("9"));
    }
    
}
