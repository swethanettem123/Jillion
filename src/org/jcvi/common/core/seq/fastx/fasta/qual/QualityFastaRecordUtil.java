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
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;

public final class QualityFastaRecordUtil {
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+).*");
   

    private QualityFastaRecordUtil(){
    	//can not instantiate
    }
    public static QualitySequenceFastaRecord buildFastaRecord(
            String identifier, String comment, CharSequence sequence) {
    	Scanner scanner = new Scanner(sequence.toString());
    	QualitySequenceBuilder builder = new QualitySequenceBuilder();
    	while(scanner.hasNextByte()){
    		builder.append(scanner.nextByte());
    	}
    	scanner.close();
    	return new DefaultQualityFastaRecord(identifier, comment, builder.build());
    }

    public static List<PhredQuality> parseQualities(CharSequence sequence) {
        Scanner scanner = new Scanner(sequence.toString());
        List<PhredQuality> result = new ArrayList<PhredQuality>();
        while(scanner.hasNextByte()){
            result.add(PhredQuality.valueOf(scanner.nextByte()));
        }
        scanner.close();
        return result;
    }

    public static String parseIdentifierFromIdLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.matches()){
            return idMatcher.group(1);
        }
        return null;
    }
}
