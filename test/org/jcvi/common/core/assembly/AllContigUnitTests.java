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

package org.jcvi.common.core.assembly;

import org.jcvi.common.core.assembly.ace.AllAceUnitTests;
import org.jcvi.common.core.assembly.asm.AllAsmUnitTests;
import org.jcvi.common.core.assembly.cas.AllCasUnitTests;
import org.jcvi.common.core.assembly.contig.celera.AllCeleraUnitTests;
import org.jcvi.common.core.assembly.ctg.AllCtgUnitTests;
import org.jcvi.common.core.assembly.tasm.AllTasmUnitTests;
import org.jcvi.common.core.assembly.util.slice.AllQualityValueStrategyUnitTests;
import org.jcvi.common.core.assembly.util.trimmer.AllTrimUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
    TestDefaultPlacedRead.class,
    AllQualityValueStrategyUnitTests.class,
    AllCtgUnitTests.class,
    AllAceUnitTests.class, 
    AllTrimUnitTests.class,    
    AllCasUnitTests.class,
    AllCeleraUnitTests.class,
    AllAsmUnitTests.class,
    AllTasmUnitTests.class
    }
    )
public class AllContigUnitTests {

}
