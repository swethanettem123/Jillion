/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.trace.archive;

import org.jcvi.jillion_experimental.trace.archive.TraceTypeCode;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTraceTypeCode {

    @Test
    public void getTraceTypeCodeFor(){
        for(TraceTypeCode code : TraceTypeCode.values()){
            String toString = code.toString();
            assertEquals(code, TraceTypeCode.getTraceTypeCodeFor(toString));
            assertEquals("lowercase",code, TraceTypeCode.getTraceTypeCodeFor(toString.toLowerCase()));
            assertEquals("uppercase",code, TraceTypeCode.getTraceTypeCodeFor(toString.toUpperCase()));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void unknownCodeShouldThrowIllegalArgumentException(){
        TraceTypeCode.getTraceTypeCodeFor("unknown code");
    }
    
    @Test(expected = NullPointerException.class)
    public void nullCodeShouldThrowNullPointerException(){
        TraceTypeCode.getTraceTypeCodeFor(null);
    }
}