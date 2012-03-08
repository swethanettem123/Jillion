package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;

public interface SffFileVisitorDataStoreBuilder extends SffDataStoreBuilder, SffFileVisitor{

	@Override
	public SffFileVisitorDataStoreBuilder addFlowgram(Flowgram flowgram);
}
