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
 * Created on Aug 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import org.jcvi.trace.sanger.traceFileServer.JTraceFilenameUtil.SourceLIMS;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestJTraceFilename {
    private final String JLIMS_traceFileName = "F319112_C24_JTC_POTATO-B-01-100-110KB_1064145307578_1064145307602_094_1119728942909.ztr";
    
    private final String Tracker_barcoded_traceFileName = "A300O2YV_B22_JTC_JAAA311TF_1090617433951_1090617433962_095_1094392116666.ztr";
    
    private final String Tracker_non_barcoded_traceFileName = "A-1038559-560-561_A05_TIGR_HMX8Z64T1001EWALK65B_1038561_1119610671976_001_1119728634200.ztr";
    
    private final String external_traceFileName = "Unknown_Z99_EXT_BMMBZ17TR_0_0_000_1108821227875.ztr";
    
    private final String XXX_loaded_by_gel_TIGR_closure_traceFileName = "1035901-903-905-947_I11_TIGR_JPCCB01T27A03PB2A3FB_1036199_1119374593789_XXX_1119368952315.scf";
    @Test
    public void invalidFileNameShouldThrowIllegalArgumentException(){
        try{
            JTraceFilenameUtil.getPlateIDFrom("not a valid filename");
            fail("should throw IllegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("not a valid filename is not a JTrace filename", e.getMessage());
        }
    }
    @Test(expected = NullPointerException.class)
    public void nullFileNameShouldThrowNullPointerException(){
        JTraceFilenameUtil.getPlateIDFrom(null);
          
    }
    @Test
    public void getFileFormat(){
        assertEquals("JLIMS", "ztr", JTraceFilenameUtil.getFileFormatFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "ztr", JTraceFilenameUtil.getFileFormatFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "ztr", JTraceFilenameUtil.getFileFormatFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External","ztr", JTraceFilenameUtil.getFileFormatFrom(external_traceFileName));
        assertEquals("TIGR gel loaded","scf", JTraceFilenameUtil.getFileFormatFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    @Test
    public void getTraceFileID(){
        assertEquals("JLIMS", 1119728942909L, JTraceFilenameUtil.getTraceFileIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", 1094392116666L, JTraceFilenameUtil.getTraceFileIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", 1119728634200L, JTraceFilenameUtil.getTraceFileIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External",1108821227875L, JTraceFilenameUtil.getTraceFileIDFrom(external_traceFileName));
        assertEquals("TIGR gel loaded",1119368952315L, JTraceFilenameUtil.getTraceFileIDFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    
    @Test
    public void getCapillaryID(){
        assertEquals("JLIMS", "094", JTraceFilenameUtil.getCapillaryIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "095", JTraceFilenameUtil.getCapillaryIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "001", JTraceFilenameUtil.getCapillaryIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "000", JTraceFilenameUtil.getCapillaryIDFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", "XXX", JTraceFilenameUtil.getCapillaryIDFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    
    @Test
    public void getTraceID(){
        assertEquals("JLIMS", 1064145307602L, JTraceFilenameUtil.getTraceIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", 1090617433962L, JTraceFilenameUtil.getTraceIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", 1119610671976L, JTraceFilenameUtil.getTraceIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", 0L, JTraceFilenameUtil.getTraceIDFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", 1119374593789L, JTraceFilenameUtil.getTraceIDFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    @Test
    public void getRunID(){
        assertEquals("JLIMS", 1064145307578L, JTraceFilenameUtil.getRunIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", 1090617433951L, JTraceFilenameUtil.getRunIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", 1038561L, JTraceFilenameUtil.getRunIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", 0L, JTraceFilenameUtil.getRunIDFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", 1036199L, JTraceFilenameUtil.getRunIDFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    
    @Test
    public void getPlateId(){
        assertEquals("JLIMS", "F319112", JTraceFilenameUtil.getPlateIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "A300O2YV", JTraceFilenameUtil.getPlateIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "A-1038559-560-561", JTraceFilenameUtil.getPlateIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "Unknown", JTraceFilenameUtil.getPlateIDFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", "1035901-903-905-947", JTraceFilenameUtil.getPlateIDFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    @Test
    public void getPlateWell(){
        assertEquals("JLIMS", "C24", JTraceFilenameUtil.getPlateWellFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "B22", JTraceFilenameUtil.getPlateWellFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "A05", JTraceFilenameUtil.getPlateWellFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "Z99", JTraceFilenameUtil.getPlateWellFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", "I11", JTraceFilenameUtil.getPlateWellFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    
    @Test
    public void getSourceLIMS(){
        assertEquals("JLIMS", SourceLIMS.JTC, JTraceFilenameUtil.getSourceLIMSFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", SourceLIMS.JTC, JTraceFilenameUtil.getSourceLIMSFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", SourceLIMS.TIGR, JTraceFilenameUtil.getSourceLIMSFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", SourceLIMS.EXT, JTraceFilenameUtil.getSourceLIMSFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", SourceLIMS.TIGR, JTraceFilenameUtil.getSourceLIMSFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
    
    @Test
    public void getLIMSParentID(){
        assertEquals("JLIMS", "POTATO-B-01-100-110KB", JTraceFilenameUtil.getLIMSParentIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "JAAA311TF", JTraceFilenameUtil.getLIMSParentIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "HMX8Z64T1001EWALK65B", JTraceFilenameUtil.getLIMSParentIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "BMMBZ17TR", JTraceFilenameUtil.getLIMSParentIDFrom(external_traceFileName));
        assertEquals("TIGR gel loaded", "JPCCB01T27A03PB2A3FB", JTraceFilenameUtil.getLIMSParentIDFrom(XXX_loaded_by_gel_TIGR_closure_traceFileName));
    }
}
