/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.trace.chromat.abi.tag;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.trace.chromat.abi.tag.AsciiTaggedDataRecord;
import org.jcvi.jillion.trace.chromat.abi.tag.StringTaggedDataRecord;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataName;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataType;

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
