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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.seq.fastx.FastXRecord;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public interface FastqRecord extends FastXRecord<Nucleotide,NucleotideSequence>{
    
    NucleotideSequence getNucleotides();
    
    QualitySequence getQualities();
    /**
     * 
    * Delegates to {@link #getNucleotides()}.
     */
    @Override
    NucleotideSequence getSequence();
    /**
     * Convenience method to format {@link FastqRecord}
     * using SANGER quality encoding and without
     * duplicating the id on the quality defline.
     * this is the same as
     * {@link #toFormattedString(FastqQualityCodec, boolean) toFormattedString(FastqQualityCodec.SANGER, false)}
     * @return a multiline string which is the fastq encoded version of
     * this {@link FastqRecord}, never null.
     */
    String toFormattedString();
    /**
     * Convenience method to format this {@link FastqRecord} without
     * duplicating the id on the quality defline.
     * this is the same as
     * {@link #toFormattedString(FastqQualityCodec, boolean) toFormattedString(qualityCodec, false)}
     * @param qualityCodec the {@link FastqQualityCodec} to use to encode
     * the qualities, can not be null.
     * @return a multiline string which is the fastq encoded version of
     * this {@link FastqRecord}, never null.
     * @throws NullPointerException if qualityCodec is null.
     */
    String toFormattedString(FastqQualityCodec qualityCodec);
    /**
     * Encode the this {@link FastqRecord} into a formatted
     * multiline String which is used in .fastq formatted files.
     * @param qualityCodec the {@link FastqQualityCodec} to use to encode
     * the qualities, can not be null.
     * @param should the read id be written (again) on the qualities line,
     * many fastq formats no longer duplicate the id on the quality line to
     * save space.
     * @return a multiline string which is the fastq encoded version of
     * this {@link FastqRecord}, never null.
     * @throws NullPointerException if qualityCodec is null.
     */
    String toFormattedString(FastqQualityCodec qualityCodec, boolean writeIdOnQualityLine);
}
