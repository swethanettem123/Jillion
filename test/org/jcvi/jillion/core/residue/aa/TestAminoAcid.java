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
package org.jcvi.jillion.core.residue.aa;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestAminoAcid {
    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                new Object[]{AminoAcid.Isoleucine,"Isolucine","Ile","I"},
                new Object[]{AminoAcid.Leucine,"Leucine","Leu","L"},
                new Object[]{AminoAcid.Lysine,"Lysine","Lys","K"},
                new Object[]{AminoAcid.Methionine,"Methionine","Met","M"},
                new Object[]{AminoAcid.Phenylalanine,"Phenylalanine","Phe","F"},
                new Object[]{AminoAcid.Threonine,"Threonine","Thr","T"},
                new Object[]{AminoAcid.Tryptophan,"Tryptophan","Trp","W"},
                new Object[]{AminoAcid.Valine,"Valine","Val","V"},
                new Object[]{AminoAcid.Cysteine,"Cysteine","Cys","C"},
                new Object[]{AminoAcid.Glutamine,"Glutamine","Gln","Q"},
                new Object[]{AminoAcid.Glycine,"Glycine","Gly", "G"},
                new Object[]{AminoAcid.Proline,"Proline","Pro","P"},
                new Object[]{AminoAcid.Serine,"Serine","Ser","S"},
                new Object[]{AminoAcid.Tyrosine,"Tyrosine","Tyr", "Y"},
                new Object[]{AminoAcid.Arginine,"Arginine","Arg","R"},
                new Object[]{AminoAcid.Histidine,"Histidine","His","H"},
                new Object[]{AminoAcid.Alanine,"Alanine","Ala","A"},
                new Object[]{AminoAcid.Asparagine,"Asparagine","Asn","N"},
                new Object[]{AminoAcid.Aspartic_Acid,"Aspartic Acid", "Asp","D"},
                new Object[]{AminoAcid.Glutamic_Acid,"Glutamic Acid","Glu","E"}
        });
    }
    
    private final String abbreviation;
    private final String threeLetterAbbreviation;
    private final String name;
    private final AminoAcid expectedAminoAcid;
    /**
     * @param name
     * @param threeLetterAbbreviation
     * @param abbreviation
     */
    public TestAminoAcid(AminoAcid expectedAminoAcid,String name, String threeLetterAbbreviation,
            String abbreviation) {
        this.expectedAminoAcid = expectedAminoAcid;
        this.name = name;
        this.threeLetterAbbreviation = threeLetterAbbreviation;
        this.abbreviation = abbreviation;
    }
    
   @Test
   public void getGlyphFor(){
       assertSame("full name",expectedAminoAcid,AminoAcid.parse(name));
       assertSame("full name lowercase",expectedAminoAcid,AminoAcid.parse(name.toLowerCase()));
       assertSame("full name uppercase",expectedAminoAcid,AminoAcid.parse(name.toUpperCase()));
       
       assertSame("3 letter abbreviation",expectedAminoAcid,AminoAcid.parse(threeLetterAbbreviation));
       assertSame("3 letter abbreviation lowercase",expectedAminoAcid,AminoAcid.parse(threeLetterAbbreviation.toLowerCase()));
       assertSame("3 letter abbreviation uppercase",expectedAminoAcid,AminoAcid.parse(threeLetterAbbreviation.toUpperCase()));
       
       assertSame("abbreviation",expectedAminoAcid,AminoAcid.parse(abbreviation));
       assertSame("abbreviation lowercase",expectedAminoAcid,AminoAcid.parse(abbreviation.toLowerCase()));
       assertSame("abbreviation uppercase",expectedAminoAcid,AminoAcid.parse(abbreviation.toUpperCase()));
   }
   @Test
   public void name(){
       assertEquals(name, expectedAminoAcid.getName());
   }
   @Test
   public void threeLetterAbreviation(){
       assertEquals(threeLetterAbbreviation, expectedAminoAcid.get3LetterAbbreviation());
   }
   @Test
   public void abbreviation(){
       assertEquals(abbreviation, expectedAminoAcid.getCharacter().toString());
   }
}
