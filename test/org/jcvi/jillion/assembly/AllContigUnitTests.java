/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.ca.AllCeleraAssemblerTests;
import org.jcvi.jillion.assembly.clc.cas.AllCasUnitTests;
import org.jcvi.jillion.assembly.consed.AllConsedUnitTests;
import org.jcvi.jillion.assembly.tigr.ctg.AllTigrContigUnitTests;
import org.jcvi.jillion.assembly.tigr.tasm.AllTasmUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
    TestDefaultPlacedRead.class,
   
    TestContigDataStoreTransformationService.class,
    
    AllTigrContigUnitTests.class,
    AllConsedUnitTests.class,
    
    AllCasUnitTests.class,
   
    AllCeleraAssemblerTests.class,
    AllTasmUnitTests.class
    }
    )
public class AllContigUnitTests {

}
