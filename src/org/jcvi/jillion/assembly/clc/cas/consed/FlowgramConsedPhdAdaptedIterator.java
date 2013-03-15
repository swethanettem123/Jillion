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

package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.assembly.ace.consed.ConsedUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.sanger.phd.ArtificialPhd;
import org.jcvi.jillion.trace.sanger.phd.Phd;
import org.jcvi.jillion.trace.sanger.phd.PhdUtil;
import org.jcvi.jillion.trace.sff.SffFlowgram;

class FlowgramConsedPhdAdaptedIterator implements StreamingIterator<PhdReadRecord>{
	private final StreamingIterator<? extends SffFlowgram> flowgramIterator;
	private final Properties requiredComments;
	private final Date phdDate;
	private final File sffFile;
	public FlowgramConsedPhdAdaptedIterator(StreamingIterator<? extends SffFlowgram> flowgramIterator, File sffFile, Date phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.flowgramIterator = flowgramIterator;	
		this.phdDate = new Date(phdDate.getTime());
		this.sffFile = sffFile;
	}
	@Override
	public boolean hasNext() {
		return flowgramIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		SffFlowgram nextFlowgram = flowgramIterator.next();
		String id = nextFlowgram.getId();
		Phd phd= ArtificialPhd.createNewbler454Phd(
				id, 
				nextFlowgram.getNucleotideSequence(), 
				nextFlowgram.getQualitySequence(),
				requiredComments);
		
		PhdInfo phdInfo = ConsedUtil.generateDefaultPhdInfoFor(sffFile, id, phdDate);
		return new PhdReadRecord(phd,phdInfo);
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