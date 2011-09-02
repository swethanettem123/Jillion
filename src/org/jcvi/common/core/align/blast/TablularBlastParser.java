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

package org.jcvi.common.core.align.blast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;

/**
 * Parse TablularBlast output ( created using the "-m 8" option in blast).
 * 
 * @author dkatzel
 *
 *
 */
public final class TablularBlastParser {

    private static final Pattern HIT_PATTERN = Pattern.compile(
       //AF178033 EMORG:AF031391  85.48             806     117          0       1       806         99       904     1e-179     644.8
            "(\\S+)\\s+(\\S+)\\s+(\\d+\\.?\\d*)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\d+\\.?\\d*)");
           
   
    private TablularBlastParser(){}
    
    public static void parse(File tabularBlastOutput, BlastVisitor visitor) throws IOException{
        InputStream in =null;
        try{
            in = new FileInputStream(tabularBlastOutput);
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    public static void parse(InputStream tabularBlastOutput, BlastVisitor visitor) throws IOException{
        TextLineParser parser = new TextLineParser(tabularBlastOutput);
        visitor.visitFile();
        while(parser.hasNextLine()){
            String line = parser.nextLine();
            visitor.visitLine(line);
            Matcher matcher = HIT_PATTERN.matcher(line);
            if(matcher.find()){
                BlastHit hit =BlastHitBuilder.create(matcher.group(1))
                                .subject(matcher.group(2))
                                .percentIdentity(Double.parseDouble(matcher.group(3)))
                                .alignmentLength(Integer.parseInt(matcher.group(4)))
                                .numMismatches(Integer.parseInt(matcher.group(5)))
                                .numGapOpenings(Integer.parseInt(matcher.group(6)))
                                .queryRange(Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                                        Integer.parseInt(matcher.group(7)),
                                        Integer.parseInt(matcher.group(8))))
                                .subjectRange(Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                                    Integer.parseInt(matcher.group(9)),
                                    Integer.parseInt(matcher.group(10))))
                                .eValue(new BigDecimal(matcher.group(11)))
                                .bitScore(new BigDecimal(matcher.group(12)))
                                .build();
                visitor.visitBlastHit(hit);
            }
        }
        visitor.visitEndOfFile();
    }
}
