package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;

public class TestCoverageMapBuilderMinCoverage extends AbstractTestCoverageMapMinCoverage{

	protected CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig){
		return new ContigCoverageMapBuilder<AssembledRead>(contig)
														.build();
	}
	
	protected CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig, int max, int min){
		return new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(max, min)
														.build();
	}
	
	
}
