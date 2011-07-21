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

package org.jcvi;

import org.jcvi.assembly.AllInternalAssemblyUnitTests;
import org.jcvi.assembly.contig.AllInternalContigUnitTests;
import org.jcvi.auth.AllAuthUnitTests;
import org.jcvi.common.core.symbol.pos.TestTigrPeaksEncoder;
import org.jcvi.common.core.symbol.pos.TestTigrPeaksEncoderCodec;
import org.jcvi.common.core.symbol.qual.TestTigrQualitiesEncoder;
import org.jcvi.common.core.symbol.qual.TestTigrQualitiesEncoderCodec;
import org.jcvi.common.internal.TestTigrPositionFileParser;
import org.jcvi.glyph.qualClass.AllQualityClassUnitTests;
import org.jcvi.trace.AllInternalTraceUnitTests;
import org.jcvi.uid.AllUidUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author dkatzel
 *
 *
 */
@RunWith(Suite.class)
@SuiteClasses(
    {
        AllQualityClassUnitTests.class,
        TestTigrQualitiesEncoder.class,
        TestTigrQualitiesEncoderCodec.class,
        TestTigrPeaksEncoder.class,
        TestTigrPeaksEncoderCodec.class,
        
        TestTigrPositionFileParser.class,
        
        AllInternalContigUnitTests.class,
        AllInternalAssemblyUnitTests.class,
        AllInternalTraceUnitTests.class,
        AllAuthUnitTests.class,
        AllUidUnitTests.class
    }
    )
public class AllInternalUnitTests {

}
