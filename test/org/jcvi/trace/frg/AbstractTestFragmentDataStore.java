/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.File;
import java.io.FileInputStream;

import org.jcvi.Distance;
import org.jcvi.Range;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.encoder.TigrQualitiesEncodedGyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.DefaultLibrary;
import org.jcvi.sequence.Library;
import org.jcvi.sequence.MateOrientation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class  AbstractTestFragmentDataStore {
    private static final String FILE = "files/example.frg2";
    
    private static final TigrQualitiesEncodedGyphCodec QUALITY_CODEC = TigrQualitiesEncodedGyphCodec.getINSTANCE();
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);

    Library library = new DefaultLibrary(".",Distance.buildDistance(5821F, 1513F), MateOrientation.INNIE);
    final Range clearRangeFor678 = Range.buildRange(0,835);
    final Range clearRangeFor061 = Range.buildRange(0,650);
    
    Fragment fragEndingIn78 = new DefaultFragment(
            "334369678",
            new DefaultNucleotideEncodedGlyphs(
                    NucleotideGlyph.getGlyphsFor(
                            "ATGATCGGCAGTGAATTGTATACGACTCACTATAGGGCGAATTGGAGCTCCACGCGGTGGCGGCCGCTCTAGAACTAGTGGATCCCCCGGGCTGCAGGAA" +
                            "TTCGATTAGGTGGAGGCCACGCTGCGCGACCCCAGCGCCCAGTCCGTAACGCACGTGCTGCAGGCAGGTGCCGGTCAGTGTGTGTGTGGTGGGGGCGGCG" +
                            "GCAGGGGGGTTGCGTACAGCATGGTGCTTGAAATTGGAAAGGAAGGAAGTCAGCCGTCAATGGAAGACACGAGTTAGTGCGGGCTTGCCCACATCATTGG" +
                            "CTGTGTATGGGGGGGGCGGTCATGGCTCAGAACGGAGTGATTACAGGCGCCATAGGCCGCCTGGCACAGCTTGACACAGGAGCACTCCCGCATGCATGCA" +
                            "CTGTCTCTGTCAGGTGTGACAGAGACAGTGTCACACCTGACATGCCGTGTTGCTCTCCTGTGTGTCCGGTGCCGCAGGAGCGCTCGCGCAAGCTGTCCTC" +
                            "GGACGTCAGCTCGCTCAAGCGCCAGCTGGGGGAGCGCGACAAGCAGGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGC" +
                            "GTGTGCGTGCGTGTGTGTGCGTGTGCGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGCGTGAGACGGAAAGAGCCAAG" +
                            "AAGAGCGCGAACTAAAGGAACAACATGGAAATAGGCGCGGCACCAAAGGTGAACCCTGGGCAACCCCATGGAATCCACAGGGAATCCCGTGTAAACCAAG" +
                            "GGACCTGAGGAGAGCACCAACAAGATCAGACGANNA")), 
            new DefaultEncodedGlyphs<PhredQuality>(RUN_LENGTH_CODEC,
                    QUALITY_CODEC.decode(
                            (
                                    "555566;;;666;;<<<;;<<?CDDDB?<??<<<AADDHHHPVSUUKKG;98:<<>>=???B=;;=>@CDDB?BEDDDIKDVVVKKDDDDDKKKSNNQXP"+
                                "OLMMMUOPPPSNQJJKKKKKQbXNNPWJJJKKDHEEESYLLFGFFLbb^^^^WWW\\\\\\^\\\\XXX[NQSYYSSSSSSJJTTT[[dZZZYY[gg[[[[[XXR"+
                                "[YTGGGGGW`YYYYYRRRRR[YYY[dVdd\\YP``PPSMMPPPPMMNSZZ```````\\[YYYYdgggggggddgdddbb``gggdbZZZ\\gggggggggg`"+
                                "dddddddd``g`gg`````ggg`g`ggdd````````Z`g``bZZZgggggg`````g````````````Z\\\\ZZ`d``gg```dgddd````g``gg``"+
                                "gggggggggd`````dddd``ZZZ``````ddddddddZ``dggg`\\ZZZZZ```d`````ZZ`Z\\ZZZZ````````````dgg``g``gg[````gdZ"+
                                "ZZZZZdZZZYY`````gg`gg`````P`ggggg````````[gSXXgg``dVVVYT[][[[XXXggggggg]][ggggggggggg[[[ggggggggZSYY"+
                                "YYOOOOOO[[[[^^^^^^^^^^VVVQQPSPKKMEDD>DDJDGJEEGJJIDDEEEECAAHFGGJJJJLPLL<<;<<HE@::88786666667866667966"+
                                "6666877778744696657544466664546699877766667667<<766766778888866666789988868666886666666866677787778<"+
                                "9:99:8876666678776667666669987575005"
                            ).getBytes())),

            clearRangeFor678, clearRangeFor678,library,"#  Not a comment; this is annotation about where the fragment came from\n"
        );
    Fragment fragEndingIn61 = new DefaultFragment(
            "334370061",
            new DefaultNucleotideEncodedGlyphs(
                    NucleotideGlyph.getGlyphsFor(
                            "ACTCAGCCTAAATACCTCACTAAGGGAACAAAGCTGGTACGGGCCCCCCCTCGAGGTCGACGGTATCGATAAGCTTGATCGGCTGGTCCCATTCGCCTTC" +
                            "CCATTCCAATTCCCGTATTCCCATCCCCACTCCGATCCCCATTCGCAGATTCCCATTCCCATATTCACCATTCCCAGCCCCAGGCCACGCACCAGCGAGC" +
                            "CCGAGAGCTCCGGCAGCAGCAGCGCAGCGGAGCCGCTCGGCGACATCCCCGCCGCCGCCCCGCCCAGCAGCTGCGACTGCGACGGCTGCGAGCCCGAGCT" +
                            "CGAGCCCGTGAAGCCGCCTCCCGCCGCCGCAGCCGCGCCCCGCCCGCCTCCTCCGCCTCCGCCTGCGCCTCCGCCGGTGGCGTGCGTGGCTGCTGCTGTG" +
                            "GCGAGATGCTCCTCCAGCTGCGCCACCAGCTGTGCCCGGTGCGCCAGGTCCGACTCCAGCGCCCGGATCTTGGAGCCCAGCTCGCCGATCTGCGGCGTGG" +
                            "AGCCGTGGGTTGGTTGCGCGGTCCTCAGGGTCCCGTGGGGGTGATCAGTTGCATACCCGTGGGGATGCCATGGGGGATGGCGCAGGGTTCGACCGTGTGG" +
                            "AGGGCGGGCGCAGAACCAGGGCGCAGGCACTAAGGCGCGCGCATCATGGGN")), 
            new DefaultEncodedGlyphs<PhredQuality>(RUN_LENGTH_CODEC,
                    QUALITY_CODEC.decode(
                            (
                                    "6689;;6687;>BG>?<??;:9??>NL?;::?9><??<??<::???G@C>888;;AGGGHKKKKKKHHKKKKPCCCCCASK=C=??COM[[bQS]bbbUU"+
                                    "UbbbbbGGCCCCCCFLCFKKFFMSSSbbVVVVKGGGGGOOOOOMUUVVIIIIGGMMMKIKLULIKLbGGLLKKMMMUUUVSVSKKMVVNNNNNNNNSKKG"+
                                    "KHHNNNGKKKKKSVVVSSS\\\\VVVXVVVV\\\\VQKHHHHHNSSRGGGGGKQJDD<;ADBEHJHMPWSSSUUUSSSSVVSSPSXVVKLBJ@JJQXXSQNVbV"+
                                    "NNNNNURQOOHGGCBAA?DKGG?K?GEEJIGC===@@NSKJ=<=B@DDR[\\VVNKMMSSVLKNNKQQSWWOOGGEGGDDDDDGVNSSSNKKNNNSVNNSV"+
                                    "VVPOOSUSUUV[[WSSSNSKQJGGEEEGGNGJHQMOOUUUUUQUUNSVSKPKKKVVSQQVVV\\XXSSRXVbbVVV\\bVSSVSSSUUVUUVVUUUPOOKGE"+
                                    "EEEEEEEEIFHD==?BBDGNOUOVKEAAADDDDEEGGFJIGGJHJGJKMLMJHHKKKNOLVJGB=>@@@>EEEIBIIJMGG><778>ADFJJLLGCCA@>"+
                                    ">==BDGGGG??B===@A>==@??<<<<<<<;999;;BBBBBBBBGB=4440"
                            ).getBytes())),

                            clearRangeFor061, clearRangeFor061,library,""
        );
    AbstractFragmentDataStore sut;
    @Before
    public void setup() throws Exception{
        File fileToParse = new File(AbstractTestFragmentDataStore.class.getResource(FILE).getFile());
        sut = createFragmentDataStore(fileToParse);
        new Frg2Parser().parse(new FileInputStream(fileToParse), sut);
    }
    
    protected abstract AbstractFragmentDataStore createFragmentDataStore(File file) throws Exception;
    @Test
    public void assertFragEndingIn61isCorrect() throws DataStoreException{
        Fragment fragment = sut.get(fragEndingIn61.getId());
        assertValuesCorrect(fragEndingIn61, fragment);
    }
    @Test
    public void assertFragEndingIn78IsCorrect() throws DataStoreException{
        Fragment fragment = sut.get(fragEndingIn78.getId());
        assertValuesCorrect(fragEndingIn78, fragment);
    }
    
    @Test
    public void hasMate() throws DataStoreException{
        assertTrue(sut.hasMate(fragEndingIn78));
        assertTrue(sut.hasMate(fragEndingIn61));
    }
    @Test
    public void getMateOf() throws DataStoreException{
        assertEquals(fragEndingIn78, sut.getMateOf(fragEndingIn61));
        assertEquals(fragEndingIn61, sut.getMateOf(fragEndingIn78));
    }
    @Test
    public void contains() throws DataStoreException{
        assertTrue(sut.contains(fragEndingIn78.getId()));
        assertTrue(sut.contains(fragEndingIn61.getId()));
    }
    @Test
    public void containsLibrary() throws DataStoreException{
        assertTrue(sut.containsLibrary(library.getId()));
    }

    private void assertValuesCorrect(Fragment expectedFragment, Fragment actualFragment) {
        assertEquals(expectedFragment.getId(), actualFragment.getId());
        assertEquals(expectedFragment.getBasecalls(), actualFragment.getBasecalls());
        assertEquals(expectedFragment.getEncodedGlyphs(), actualFragment.getEncodedGlyphs());
        assertEquals(expectedFragment.getQualities(), actualFragment.getQualities());
        assertEquals(expectedFragment.getValidRange(), actualFragment.getValidRange());
        assertEquals(expectedFragment.getVectorClearRange(), actualFragment.getVectorClearRange());
        assertEquals(library, actualFragment.getLibrary());
        assertEquals(expectedFragment.getComment(), actualFragment.getComment());
        assertEquals(expectedFragment.getLength(), actualFragment.getLength());
        assertEquals(expectedFragment.getLibraryId(), actualFragment.getLibraryId());
        
    }
    
}
