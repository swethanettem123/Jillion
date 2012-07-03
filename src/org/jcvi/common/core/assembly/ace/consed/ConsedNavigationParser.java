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

package org.jcvi.common.core.assembly.ace.consed;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.ace.consed.NavigationElement.Type;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;

/**
 * @author dkatzel
 *
 *
 */
public class ConsedNavigationParser {

    private static final Pattern TITLE_PATTERN = Pattern.compile("TITLE: (\\.+)\n");
    private static final Pattern TYPE_PATTERN = Pattern.compile("TYPE: (\\S+)\n");
    private static final Pattern READ_ID_PATTERN = Pattern.compile("READ: (.+)\n");
    private static final Pattern CONTIG_ID_PATTERN = Pattern.compile("CONTIG: (.+)\n");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("COMMENT: (.+)\n");
    private static final Pattern READ_POSITION_PATTERN = Pattern.compile("UNPADDED_READ_POS: (\\d+) (\\d+)\n");
    private static final Pattern CONSENSUS_POSITION_PATTERN = Pattern.compile("UNPADDED_CONS_POS: (\\d+) (\\d+)\n");
   
    public static void parse(File navFile, ConsedNavigationVisitor visitor) throws IOException{
        InputStream in =null;
        try{
            in = new FileInputStream(navFile);
            parse(in, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    public static void parse(InputStream in, ConsedNavigationVisitor visitor) throws IOException{
        TextLineParser parser = new TextLineParser(in);
        boolean inRegion=false;
        Type regionType=null;
        String id=null;
        String currentComment = null;
        Range range=null;
        visitor.visitFile();
        while(parser.hasNextLine()){
            String line = parser.nextLine();
            visitor.visitLine(line);
            Matcher matcher = TITLE_PATTERN.matcher(line);
            if(matcher.find()){
                String title = matcher.group(1);
                visitor.visitTitle(title);
            }else{
                if(inRegion){                
                    if(line.startsWith("TYPE:")){
                        Matcher typeMatcher =TYPE_PATTERN.matcher(line);
                        typeMatcher.find();                        
                        regionType = Type.valueOf(typeMatcher.group(1));
                    }else if(line.startsWith("READ:")){
                        Matcher readIdMatcher =READ_ID_PATTERN.matcher(line);
                        readIdMatcher.find();
                        id = readIdMatcher.group(1);
                    }else if(line.startsWith("CONTIG:")){
                        Matcher contigIdMatcher =CONTIG_ID_PATTERN.matcher(line);
                        contigIdMatcher.find();
                        id = contigIdMatcher.group(1);
                    }
                    else if(line.startsWith("COMMENT:")){
                        Matcher commentMatcher =COMMENT_PATTERN.matcher(line);
                        commentMatcher.find();
                        currentComment = commentMatcher.group(1);
                    }else if(line.startsWith("UNPADDED_")){                        
                        final Matcher positionMatcher;
                        switch(regionType){
                            case CONSENSUS: positionMatcher = CONSENSUS_POSITION_PATTERN.matcher(line);
                                        break;
                            case READ: positionMatcher = READ_POSITION_PATTERN.matcher(line);
                                        break;
                            default: throw new IllegalStateException("could not find region type");
                        }
                        positionMatcher.find();
                        range = Range.create(CoordinateSystem.RESIDUE_BASED,
                                Integer.parseInt(positionMatcher.group(1)),
                                Integer.parseInt(positionMatcher.group(2)));
                        
                    } else if(line.startsWith("END_REGION")){
                        inRegion=false;
                        if(regionType==null){
                            throw new IllegalStateException("could not find region type");
                        }
                        if(regionType==Type.CONSENSUS){
                            visitor.visitElement(new ConsensusNavigationElement(id, range, currentComment));
                        }else{
                            visitor.visitElement(new ReadNavigationElement(id, range, currentComment));
                            
                        }
                    }
                }else if(line.startsWith("BEGIN_REGION")){
                        inRegion=true;
                        regionType=null;
                        id=null;
                        currentComment=null;
                }
                
            }
        }
        visitor.visitEndOfFile();
    }
}
