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
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestSff454NameUtil {

    @Test
    public void isSffRead(){
        assertTrue(Sff454NameUtil.is454Read( "C3U5GWL01CBXT2"));
        assertFalse(Sff454NameUtil.is454Read("IVAAA01T48C03PB1234F"));
        assertFalse(Sff454NameUtil.is454Read("IVAAA01T48HA2F"));
        //now check mated reads
        assertTrue("clc split mate",Sff454NameUtil.is454Read("F3P0QKL01AMPVE_1-93"));
        assertTrue("newbler split mate",Sff454NameUtil.is454Read("ERESL0I01CLM9Q_left"));
        assertTrue("sffToCA split mate",Sff454NameUtil.is454Read("ERESL0I01EGOIMb"));
    }
    
    @Test
    public void getUniveralAccessionNumberFrom(){
        assertEquals("C3U5GWL01CBXT2", Sff454NameUtil.parseUniversalAccessionNumberFrom("C3U5GWL01CBXT2"));
        assertEquals("clc split mate",
                "F3P0QKL01AMPVE", Sff454NameUtil.parseUniversalAccessionNumberFrom("F3P0QKL01AMPVE_1-93"));
        
        assertEquals("newbler split mate",
                "ERESL0I01CLM9Q", Sff454NameUtil.parseUniversalAccessionNumberFrom("ERESL0I01CLM9Q_left"));
        assertEquals("sffToCA split mate",
                "ERESL0I01EGOIM", Sff454NameUtil.parseUniversalAccessionNumberFrom("ERESL0I01EGOIMb"));
    }
    @Test
    public void getRegionNumber(){
        assertEquals(1, Sff454NameUtil.getRegionNumber("C3U5GWL01CBXT2"));
        assertEquals(2, Sff454NameUtil.getRegionNumber("F3P0QKL02AMPVE_1-93"));
        
        assertEquals(12, Sff454NameUtil.getRegionNumber("ERESL0I12CLM9Q_left"));
 
    }
    @Test
    public void parseDateFromName(){
        DateTime expectedDate = new DateTime(2004, 9, 22, 16, 59, 10, 0);
        assertEquals(expectedDate.toDate(), Sff454NameUtil.getDateOfRun("C3U5GWL01CBXT2"));
    }
   
    
    @Test
    public void parseLocation(){
        assertEquals(new Sff454NameUtil.Location(838,3960), Sff454NameUtil.parseLocationOf("C3U5GWL01CBXT2"));
    }
    
    @Test
    public void generateAccessionName(){
        //example from 454 Data Analysis Software Manual page 533
        String rigRunName = "R_2006_10_10_20_18_48_build04_adminrig_100x7075SEQ082806BHTF960397NewBeadDep2Region4EXP106";
        assertEquals("EBO6PME01EE3WX", Sff454NameUtil.generateAccessionNumberFor(rigRunName, 1, new Sff454NameUtil.Location(1695,767)));
    }
    
    @Test
    public void computeRigRunName(){
    	LocalDateTime dateTime = LocalDateTime.of(2006,9,4,6,18,48);
    	String rigName = "build04";
    	String username = "adminrig";
    	String freeForm = "blahblah";
    	
    	Date legacyDate =Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    	
    	assertEquals("R_2006_09_04_06_18_48_build04_adminrig_blahblah", 
    			Sff454NameUtil.computeRigRunName(legacyDate, rigName, username, freeForm));
    }
    
}
