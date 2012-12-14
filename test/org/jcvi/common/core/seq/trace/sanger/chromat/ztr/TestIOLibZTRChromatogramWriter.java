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

package org.jcvi.common.core.seq.trace.sanger.chromat.ztr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.TraceEncoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.IOLibLikeZtrChromatogramWriter;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZtrChromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRChromatogramFile;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOLibZTRChromatogramWriter {

	ResourceFileServer RESOURCES = new ResourceFileServer(TestIOLibZTRChromatogramWriter.class);

	@Test
	public void testEncodeAndDecode() throws FileNotFoundException, TraceDecoderException, IOException, TraceEncoderException{
		ZtrChromatogram chromatogram = ZTRChromatogramFile.create(RESOURCES.getFile("files/GBKAK82TF.ztr"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOLibLikeZtrChromatogramWriter.INSTANCE.write(chromatogram, out);
		ZtrChromatogram reParsed = ZTRChromatogramFile.create("id",new ByteArrayInputStream(out.toByteArray()));
		
		assertEquals(chromatogram, reParsed);
		
	}
	

}
