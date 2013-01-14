/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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

package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.ab1.tag;

import org.jcvi.jillion.core.io.IOUtil;

public class DefaultAsciiTaggedDataRecord extends AbstractTaggedDataRecord<StringTaggedDataRecord,String> implements AsciiTaggedDataRecord{

	public DefaultAsciiTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected String parseDataFrom(byte[] data) {
		return new String(data,IOUtil.UTF_8);
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<String> getParsedDataType() {
        return String.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<StringTaggedDataRecord> getType() {
        return StringTaggedDataRecord.class;
    }


}
