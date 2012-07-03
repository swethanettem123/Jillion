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
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;
/**
 * {@code AceFileParser} contains methods for parsing
 * ACE formatted files.
 * @author dkatzel
 * @see <a href = "http://www.phrap.org/consed/distributions/README.20.0.txt">Consed documentation which contains the ACE FILE FORMAT</a>
 */
public final class AceFileParser {
    /**
     * Parse the given aceFile and call the appropriate methods on the given AceFileVisitor.
     * @param aceFile the ace file to parse, can not be null.
     * @param visitor the visitor to be visited, can not be null.
     * @throws IOException if the ace file does not exist or 
     * if there is a problem reading the ace file .
     * @throws NullPointerException if either the aceFile or the visitor are {@code null}.
     */
    public static void parse(File aceFile, AceFileVisitor visitor) throws IOException{
        InputStream in = new FileInputStream(aceFile);
        try{
            parse(in, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given {@link InputStream} containing ace encoded data
     * and call the appropriate methods on the given AceFileVisitor.
     * If the entire inputStream is parsed, then it will automatically
     * be closed, however if there is an error while reading the {@link InputStream},
     * then the {@link InputStream} will be left open.
     * @param inputStream the inputStream to parse, can not be null.
     * @param visitor the visitor to be visited, can not be null.
     * @throws IOException if there is a problem reading the ace file.
     * @throws NullPointerException if either the aceFile or the visitor are {@code null}.
     */
    public static void parse(InputStream inputStream, AceFileVisitor visitor) throws IOException{
        if(inputStream ==null){
            throw new NullPointerException("input stream can not be null");
        }
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        visitor.visitFile();
        ParserState parserState = new ParserState(visitor, inputStream);
        while(!parserState.done()){
            parserState = parserState.parseNextSection();
        }
        handleEndOfParsing(visitor, parserState);
        visitor.visitEndOfFile();
        
    }
    /**
     * This method figures out if we need
     * to call {@link AceFileVisitor#visitEndOfContig()}
     * or not and might also close the {@link ParserState}
     * (which should close the stream)
     * 
     * @param visitor
     * @param parserState
     */
    private static void handleEndOfParsing(AceFileVisitor visitor,
            ParserState parserState) {
        if(!parserState.stopParsing && parserState.inAContig){        
            visitor.visitEndOfContig();
        }else{
            //if parser state reached the end of the file
            //then we have already closed our stream
            //this will force it if we haven't
            IOUtil.closeAndIgnoreErrors(parserState);
        }
    }
    /**
     * {@code ParserState} keeps track of the where
     * we are in an ace file (in the first contig, 
     * what read number we are on etc...) as well
     * which parts of the  ace file the 
     * {@link AceFileVisitor} implementation
     * wants to visit.
     * 
     * @author dkatzel
     *
     */
    private static class ParserState implements Closeable{
        final boolean isFirstContigInFile;
        final AceFileVisitor visitor;
        final TextLineParser parser;
        private boolean stopParsing;
        private boolean parseCurrentContig;
        private boolean inAContig;
        private int expectedNumberOfReads;
        private int numberOfReadsSeen;
        
        ParserState(AceFileVisitor visitor,
                InputStream inputStream) throws IOException{
           this(visitor, true,  new TextLineParser(new BufferedInputStream(inputStream)), false,true,false,0,0);
       }
        
        public ParserState stopParsing(){
            return new ParserState(visitor, isFirstContigInFile, parser,true,parseCurrentContig,inAContig, expectedNumberOfReads, numberOfReadsSeen);
        }
        public ParserState dontParseCurrentContig(){
            return new ParserState(visitor, isFirstContigInFile, parser,stopParsing,false,inAContig, expectedNumberOfReads, numberOfReadsSeen);
        }
        
        public boolean seenAllExpectedReads(){
            return numberOfReadsSeen == expectedNumberOfReads;
        }
        public ParserState seenRead(){
            numberOfReadsSeen++;
            return this;
        }
        /**
         * @return
         * @throws IOException 
         */
        public ParserState parseNextSection() throws IOException {
            String lineWithCR = parser.nextLine();           
            visitor.visitLine(lineWithCR);
            String line = lineWithCR.endsWith("\n")
                        ? lineWithCR.substring(0, lineWithCR.length()-1)
                        : lineWithCR; 
            return SectionHandler.handleSection(line, this);
        }
        ParserState(AceFileVisitor visitor, boolean isFirstContigInFile,
               TextLineParser parser, boolean stopParsing, boolean parseCurrentContig, boolean inAContig,
               int numberOfExpectedReads, int numberOfReadsSeen) {
            this.visitor = visitor;
            this.isFirstContigInFile = isFirstContigInFile;
            this.parser = parser;
            this.stopParsing=stopParsing;
            this.parseCurrentContig = parseCurrentContig;
            this.inAContig = inAContig;
            this.numberOfReadsSeen = numberOfReadsSeen;
            this.expectedNumberOfReads = numberOfExpectedReads;
        }
        public boolean done(){
            return stopParsing || !parser.hasNextLine();
        }
        
        public boolean parseCurrentContig(){
            return parseCurrentContig;
        }
        
        /**
         * Returns new ParserStruct instance but which
         * states that a different contig is being visited. 
         * @return a new ParserStruct object with the same 
         * values except {@link #isFirstContigInFile} is now
         * set to {@code false}.
         */
        ParserState inAContig(int numberOfExpectedReads){
            return new ParserState(visitor, false, parser,stopParsing,true,true, numberOfExpectedReads,0);
        }
        /**
         * Returns new ParserStruct instance but which
         * states that a different contig is being visited. 
         * @return a new ParserStruct object with the same 
         * values except {@link #isFirstContigInFile} is now
         * set to {@code false}.
         */
        ParserState notInAContig(){
            if(expectedNumberOfReads !=numberOfReadsSeen){
                throw new IllegalStateException(
                        String.format("did not visit all expected reads : %d vs %d", numberOfReadsSeen, expectedNumberOfReads));
            }
            return new ParserState(visitor, false, parser,stopParsing,true,false,0,0);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            parser.close();
            
        }
    }
    /**
     * Each Section of an ACE file needs to be handled 
     * differently.  This might require firing visitor methods
     * or reading additional lines of text from the ace file.
     * @author dkatzel
     */
    private enum SectionHandler{
        
        ACE_HEADER("^AS\\s+(\\d+)\\s+(\\d+)"){
            @Override
            ParserState handle(Matcher headerMatcher, ParserState struct, String line) {
                
                int numberOfContigs = Integer.parseInt(headerMatcher.group(1));
                int totalNumberOfReads = Integer.parseInt(headerMatcher.group(2));
                struct.visitor.visitHeader(numberOfContigs, totalNumberOfReads);
                return struct;
            }
        },
        CONSENSUS_QUALITIES("^BQ\\s*"){
            @Override
            ParserState handle(Matcher matcher, ParserState parserState, String line) {
                if(parserState.parseCurrentContig()){
                    parserState.visitor.visitConsensusQualities();
                }
                return parserState;
            }
        },
        /**
         * Handles both basecalls from contig consensus as well
         * as basecalls from reads.
         */
        BASECALLS("^([\\-*a-zA-Z]+)\\s*$"){
            @Override
            ParserState handle(Matcher basecallMatcher, ParserState parserState, String line) {
                if(line.indexOf('-') !=-1){
                    //contains a gap as a '-' instead of a '*'
                    throw new IllegalStateException("invalid ace file: found '-' used as a gap instead of '*' : "+line);
                }
                if(parserState.parseCurrentContig()){
                    parserState.visitor.visitBasesLine(basecallMatcher.group(1));
                }
                return parserState;
            } 
        },
        CONTIG_HEADER("^CO\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([UC])"){
            @Override
            ParserState handle(Matcher contigMatcher, ParserState struct, String line) {
                ParserState ret = struct;
                if(!struct.isFirstContigInFile && !ret.visitor.visitEndOfContig()){
                    ret= ret.stopParsing();
                }
                String contigId = contigMatcher.group(1);
                int numberOfBases = Integer.parseInt(contigMatcher.group(2));
                int numberOfReads = Integer.parseInt(contigMatcher.group(3));
                int numberOfBaseSegments = Integer.parseInt(contigMatcher.group(4));
                boolean reverseComplimented = isComplimented(contigMatcher.group(5));
                
                ret= ret.inAContig(numberOfReads);
                boolean parseCurrentContig = ret.visitor.shouldVisitContig(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplimented);
                if(parseCurrentContig){
                	ret.visitor.visitBeginContig(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplimented);
                }else{
                    ret = ret.dontParseCurrentContig();
                }
                return ret;
            } 
            
        },
        ASSEMBLED_FROM("^AF\\s+(\\S+)\\s+([U|C])\\s+(-?\\d+)"){
            @Override
            ParserState handle(Matcher assembledFromMatcher, ParserState parserState, String line) {
                if(parserState.parseCurrentContig()){
                    String name = assembledFromMatcher.group(1);
                    final String group = assembledFromMatcher.group(2);
                    Direction dir = isComplimented(group)? Direction.REVERSE : Direction.FORWARD;
                    int fullRangeOffset = Integer.parseInt(assembledFromMatcher.group(3));
                    parserState.visitor.visitAssembledFromLine(name, dir, fullRangeOffset);
                }
                return parserState;
            } 
        },
        READ_HEADER("^RD\\s+(\\S+)\\s+(\\d+)"){
            @Override
            ParserState handle(Matcher readMatcher, ParserState parserState, String line) {
                if(parserState.parseCurrentContig()){
                    String readId = readMatcher.group(1);
                    int fullLength = Integer.parseInt(readMatcher.group(2));
                    parserState.visitor.visitReadHeader(readId, fullLength);
                }
                return parserState;
            } 
        },
        READ_QUALITY("^QA\\s+(-?\\d+)\\s+(-?\\d+)\\s+(\\d+)\\s+(\\d+)"){
            @Override
            ParserState handle(Matcher qualityMatcher, ParserState parserState, String line) {
                if(parserState.parseCurrentContig()){
                    int clearLeft = Integer.parseInt(qualityMatcher.group(1));
                    int clearRight = Integer.parseInt(qualityMatcher.group(2));
                    
                    int alignLeft = Integer.parseInt(qualityMatcher.group(3));
                    int alignRight = Integer.parseInt(qualityMatcher.group(4));
                    parserState.visitor.visitQualityLine(clearLeft, clearRight, alignLeft, alignRight);
                }
                return parserState;
            } 
        },
        TRACE_DESCRIPTION("^DS\\s+"){
            
            private final Pattern chromatFilePattern = Pattern.compile("CHROMAT_FILE:\\s+(\\S+)\\s+");
            private final Pattern phdFilePattern = Pattern.compile("PHD_FILE:\\s+(\\S+)\\s+");
            private final Pattern timePattern = Pattern.compile("TIME:\\s+(.+:\\d\\d\\s+\\d\\d\\d\\d)");
            private final Pattern sffFakeChromatogramPattern = Pattern.compile("sff:(\\S+)?\\.sff:(\\S+)");
             
            @Override
            ParserState handle(Matcher qualityMatcher, ParserState parserState, String line) throws IOException {
                if(parserState.parseCurrentContig()){
                    Matcher chromatogramMatcher = chromatFilePattern.matcher(line);
                    if(!chromatogramMatcher.find()){
                        throw new IOException("could not parse chromatogram name from "+line);
                    }
                    String traceName =  chromatogramMatcher.group(1);
                    String phdName = parsePhdName(line, traceName);
                    
                    Matcher timeMatcher = timePattern.matcher(line);
                    if(!timeMatcher.find()){
                        throw new IOException("could not parse phd time stamp from "+ line);
                    }
                    Date date;
					try {
						date = AceFileUtil.parsePhdDate(                                                
						        timeMatcher.group(1));
					} catch (Exception e) {
						throw new IllegalStateException("error parsing chromatogram time stamp '"+timeMatcher.group(1)+"'",e);
					}
                    parserState.visitor.visitTraceDescriptionLine(traceName, phdName, date);
                    
                }
                parserState.seenRead();
                return parserState;
            }

            private String parsePhdName(String line, String traceName) {
                Matcher phdMatcher = phdFilePattern.matcher(line);
                String phdName;
                if(phdMatcher.find()){
                	phdName = phdMatcher.group(1);
                }else{
                    //sff's some times are in the format CHROMAT_FILE: sff:[-f:]<sff file>:<read id>
                    Matcher sffNameMatcher =sffFakeChromatogramPattern.matcher(traceName);
                    if(sffNameMatcher.find()){
                       
                    String sffRootName = sffNameMatcher.group(2);
                    final String group = sffNameMatcher.group(1);
                    boolean isForward = group.startsWith("-f:");
                    phdName = String.format("%s_%s", sffRootName,isForward?"left":"right");
                    }
                    else{
                        phdName = traceName;
                    }
                }
                return phdName;
            } 
        },
        READ_TAG("^RT\\{"){
            private final Pattern readTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{6}:\\d{6})");
            
            @Override
            ParserState handle(Matcher qualityMatcher, ParserState parserState, String line) throws IOException {
                ParserState currentParserState=parserState;
            	if(currentParserState.inAContig && currentParserState.seenAllExpectedReads()){                    
                    currentParserState =currentParserState.notInAContig();
                    currentParserState.visitor.visitEndOfContig();
                }
                
                String lineWithCR;
                lineWithCR = currentParserState.parser.nextLine();
                currentParserState.visitor.visitLine(lineWithCR);
                Matcher readTagMatcher = readTagPattern.matcher(lineWithCR);
                if(!readTagMatcher.find()){
                    throw new IllegalStateException("expected read tag infomration: " + lineWithCR); 
                }
                String id = readTagMatcher.group(1);
                String type = readTagMatcher.group(2);
                String creator = readTagMatcher.group(3);
                long gappedStart = Long.parseLong(readTagMatcher.group(4));
                long gappedEnd = Long.parseLong(readTagMatcher.group(5));
                Date creationDate;
				try {
					creationDate = AceFileUtil.parseTagDate(                                                
					        readTagMatcher.group(6));
				} catch (ParseException e) {
					throw new IllegalStateException("error parsing date from read tag", e);
				}
                currentParserState.visitor.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, true);
                lineWithCR = currentParserState.parser.nextLine();
                currentParserState.visitor.visitLine(lineWithCR);
                if(!lineWithCR.startsWith("}")){
                    throw new IllegalStateException("expected close read tag: " + lineWithCR); 
                }
                return currentParserState;
            } 
        },
        WHOLE_ASSEMBLY_TAG("^WA\\{"){
            private final Pattern wholeAssemblyTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\d{6}:\\d{6})");
            
            @Override
            ParserState handle(Matcher qualityMatcher, ParserState parserState, String line) throws IOException {
                ParserState currentParserState = parserState;
            	if(currentParserState.inAContig && currentParserState.seenAllExpectedReads()){
                    currentParserState =currentParserState.notInAContig();
                    currentParserState.visitor.visitEndOfContig();                    
                }
                String lineWithCR;
                lineWithCR = currentParserState.parser.nextLine();
                currentParserState.visitor.visitLine(lineWithCR);
                Matcher tagMatcher = wholeAssemblyTagPattern.matcher(lineWithCR);
                if(!tagMatcher.find()){
                    throw new IllegalStateException("expected whole assembly tag information: " + lineWithCR); 
                }
                String type = tagMatcher.group(1);
                String creator = tagMatcher.group(2);
                Date creationDate;
				try {
					creationDate = AceFileUtil.parseTagDate(                                                
					        tagMatcher.group(3));
				} catch (ParseException e) {
					throw new IllegalStateException("error parsing date from while assembly tag", e);
				}
                                            
                StringBuilder data = parseWholeAssemblyTagData(currentParserState);
                currentParserState.visitor.visitWholeAssemblyTag(type, creator, creationDate, data.toString());
                return currentParserState;
            }

            private StringBuilder parseWholeAssemblyTagData(ParserState struct)
                    throws IOException {
                String lineWithCR;
                boolean doneTag =false;
                StringBuilder data = new StringBuilder();
                while(!doneTag && struct.parser.hasNextLine()){
                    lineWithCR = struct.parser.nextLine();
                    struct.visitor.visitLine(lineWithCR);
                    if(lineWithCR.startsWith("}")){
                    	doneTag =true;
                    }else{
                        data.append(lineWithCR);
                    }                    
                }
                if(!doneTag){
                    throw new IllegalStateException("unexpected EOF, Whole Assembly Tag not closed!"); 
                }
                return data;
            } 
        },
        CONSENSUS_TAG("^CT\\{"){
            private final Pattern consensusTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{6}:\\d{6})(\\s+(noTrans))?");
            
            @Override
            ParserState handle(Matcher qualityMatcher, ParserState parserState, String line) throws IOException {
                ParserState currentParserState = parserState;
            	if(currentParserState.inAContig && currentParserState.seenAllExpectedReads()){                    
                    currentParserState =currentParserState.notInAContig();
                    currentParserState.visitor.visitEndOfContig();
                }
                String lineWithCR;
                lineWithCR = currentParserState.parser.nextLine();
                currentParserState.visitor.visitLine(lineWithCR);
                Matcher tagMatcher = consensusTagPattern.matcher(lineWithCR);
                if(!tagMatcher.find()){
                    throw new IllegalStateException("expected read tag infomration: " + lineWithCR); 
                }
                String id = tagMatcher.group(1);
                String type = tagMatcher.group(2);
                String creator = tagMatcher.group(3);
                long gappedStart = Long.parseLong(tagMatcher.group(4));
                long gappedEnd = Long.parseLong(tagMatcher.group(5));
                Date creationDate;
				try {
					creationDate = AceFileUtil.parseTagDate(                                                
					        tagMatcher.group(6));
				} catch (ParseException e) {
					throw new IllegalStateException("error parsing date from consensus tag", e);
				}
                boolean isTransient = tagMatcher.group(7)!=null;
                
                currentParserState.visitor.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
                
                
                boolean doneTag =false;
                boolean inComment=false;
                
                parseConsensusTagData(currentParserState, doneTag, inComment);
                currentParserState.visitor.visitEndConsensusTag();
                return currentParserState;
            }

            private void parseConsensusTagData(ParserState parserState,
                    boolean pDoneTag, boolean pInComment) throws IOException {
                String lineWithCR;
                StringBuilder consensusComment=null;
                boolean doneTag = pDoneTag;
                boolean inComment = pInComment;
                while(!doneTag && parserState.parser.hasNextLine()){
                    lineWithCR = parserState.parser.nextLine();
                    parserState.visitor.visitLine(lineWithCR);
                    if(lineWithCR.startsWith("COMMENT{")){
                        inComment=true;
                        consensusComment = new StringBuilder();
                    }else{
                        if(inComment){
                            if(lineWithCR.startsWith("C}")){                                                            
                                parserState.visitor.visitConsensusTagComment(consensusComment.toString());
                                inComment=false;
                            }else{
                                consensusComment.append(lineWithCR);
                            }
                        }else if(lineWithCR.startsWith("}")){
                        	 doneTag =true;
                        }
                        else{                           
                            parserState.visitor.visitConsensusTagData(lineWithCR);
                        }
                    }
                }
                if(!doneTag){
                    throw new IllegalStateException("unexpected EOF, Consensus Tag not closed!"); 
                }
            } 
        },
        IGNORE{
            @Override
            ParserState handle(Matcher matcher, ParserState struct,
                    String line) throws IOException {
                return struct;
            }
        }
        ;
        private static final String COMPLIMENT_STRING = "C";
        /**
         * All handlers are considered except IGNORE.
         */
        private static final EnumSet<SectionHandler> HANDLERS_TO_CONSIDER = EnumSet.complementOf(EnumSet.of(IGNORE));
        private final Pattern pattern;
        private SectionHandler() {
            pattern = null;
        }
        private SectionHandler(String patternStr) {
            pattern = Pattern.compile(patternStr);
        }
        final Matcher matcher(String line) {            
            return pattern.matcher(line);
        }
        final boolean isComplimented(final String orientation) {
            return COMPLIMENT_STRING.equals(orientation);
        }
        abstract ParserState handle(Matcher matcher, ParserState struct, String line) throws IOException;
        /**
         * ResultHandler stores the SectionHandler that will 
         * be used to handle the current section and the Matcher
         * that matched the section (we store this so we 
         * don't have to match the string twice).
         * @author dkatzel
         */
        private static class ResultHandler{
            final SectionHandler handler;
            final Matcher matcher;
            
            ResultHandler(SectionHandler handler, Matcher matcher) {
                this.handler = handler;
                this.matcher = matcher;
            }
            ParserState handle(String line, ParserState struct) throws IOException{
                return handler.handle(matcher, struct, line);
            }
        }
        private static ResultHandler findCorrectHandlerFor(String line){
            for(SectionHandler handler : HANDLERS_TO_CONSIDER){
                Matcher matcher = handler.matcher(line);
                if(matcher.find()){
                    return new ResultHandler(handler,matcher);
                }
            }
            //if we don't find a handler, then we ignore it
            return new ResultHandler(IGNORE,null);
        }
        public static ParserState handleSection(String line, ParserState struct) throws IOException{
            return findCorrectHandlerFor(line).handle(line, struct);

        }
        
    }
    
}
