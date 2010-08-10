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
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.fasta.fastq.AllFastqUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestQualityFastaRecord.class ,
     TestDefaultEncodedNuclotideFastaRecord.class,
     TestFastaParser.class,
     TestDefaultQualityFastaMap.class,
     
     TestDefaultSequenceFastaMap.class,
     TestLargeSequenceFastaMap.class,
     
     TestDefaultSequenceFastaDataStoreWithNoComment.class,
     TestLargeSequenceFastaMapWithNoComment.class,
     
     TestFlowgramQualityFastaMap.class,
     
     TestPositionFastaRecord.class,
     
     TestDefaultPositionsFastaDataStore.class,
     TestLargePositionsFastaMap.class,
     TestLargeQualityFastaMap.class,
     TestNucleotideFastaH2DataStore.class,
     TestFilteredNucleotideFastaH2DataStore.class,
     
     TestQualityFastaH2DataStore.class,
     TestFilteredQualityFastaH2DataStore.class,
     
     TestNucleotideDataStoreFastaAdatper.class,
     
     AllFastqUnitTests.class
    }
    )
public class AllFastaUnitTests {

}
