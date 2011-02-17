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

package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jcvi.assembly.ace.PhdInfo;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;
import org.jcvi.trace.sanger.phd.ArtificialPhd;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdUtil;
import org.jcvi.util.CloseableIterator;
import org.joda.time.DateTime;

public class FlowgramConsedPhdAdaptedIterator implements PhdReadRecordIterator{
	private final CloseableIterator<? extends Flowgram> flowgramIterator;
	private final Properties requiredComments;
	private final DateTime phdDate;
	private final File sffFile;
	public FlowgramConsedPhdAdaptedIterator(CloseableIterator<? extends Flowgram> flowgramIterator, File sffFile, DateTime phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.flowgramIterator = flowgramIterator;	
		this.phdDate = phdDate;
		this.sffFile = sffFile;
	}
	@Override
	public boolean hasNext() {
		return flowgramIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		Flowgram nextFlowgram = flowgramIterator.next();
		String id = nextFlowgram.getId();
		Phd phd= ArtificialPhd.createNewbler454Phd(
				id, 
				nextFlowgram.getBasecalls(), 
				nextFlowgram.getQualities(),
				requiredComments);
		
		PhdInfo phdInfo = ConsedUtil.generatePhdInfoFor(sffFile, id, phdDate);
		return new DefaultPhdReadRecord(phd,phdInfo);
	}

	@Override
	public void remove() {
		flowgramIterator.remove();
		
	}
	@Override
	public void close() throws IOException {
		flowgramIterator.close();
		
	}

}
