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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.tigr.tasm.TasmVisitor.TasmVisitorCallback;
import org.jcvi.jillion.assembly.tigr.tasm.TasmVisitor.TasmVisitorCallback.TasmVisitorMemento;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code TasmFileParser} can create
 * parser objects that can parse
 * TIGR Assembler encoded files ({@literal .tasm} files).
 * Files of this type are produced by
 * the legacy  TIGR Assembler program
 * and usually have a file extension of {@literal .tasm} 
 * or {@literal .asm}.  The {@literal .asm} extension has been deprecated
 * since it can be easily confused with TIGR Assembler's
 * replacement, Celera Assembler, which also produces
 * assembly files with a {@literal .asm} file extension although the data
 * is encoded completely differently.
 * 
 * @author dkatzel
 *
 */
public abstract class TasmFileParser implements TasmParser{
    /**
     * Each contig data is separated by a pipe ('|').
     */
    private static final String END_OF_CONTIG = "|";
    /**
     * Each line in a tasm file that 
     * contains record data will be a whitespace separated
     * key value pair.  These keys correspond to 
     * TIGR Project Database tables and columns 
     * while the values correspond to their values
     * of various types.
     * 
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+.*$)");
    /**
     * Create a new instance of {@link TasmFileParser}
     * that will parse the given tasm encoded file.
     * @param tasmFile a tasm encoded file.
     * @return a new {@link TasmFileParser} instance;
     * will not be null.
     * @throws NullPointerException if tasmFile is null.
     * @throws IOException if tasmFile does not exist
     * or is not readable.
     */
    public static TasmParser create(File tasmFile) throws IOException{
    	return new FileBasedTasmFileParser(tasmFile);
    }
    /**
     * Create a new instance of {@link TasmFileParser}
     * that will parse the given tasm encoded {@link InputStream}.
     * InputStream parsers can not create {@link TasmVisitorMemento}
     * via {@link TasmVisitorCallback#createMemento()}
     * and {@link TasmVisitorCallback#canCreateMemento()} 
     * will always return {@code false}.
     * @param in a tasm encoded {@link InputStream}.
     * @return a new {@link TasmFileParser} instance
     * that is not able to create mementos;
     * will not be null.
     * @throws NullPointerException if tasmFile is null.
     * or is not readable.
     */
    public static TasmParser create(InputStream in){
    	return new InputStreamBasedTasmFileParser(in);
    }
    private TasmFileParser(){
    	//can not instantiate outside of this file
    }
    
     
    final void parseTasm(TextLineParser parser, TasmVisitor visitor) throws IOException{
         

         ParserState parserState = new ParserState(visitor);
         while(parser.hasNextLine() && parserState.keepParsing()) {
             String line = parser.nextLine();
             Matcher matcher = KEY_VALUE_PATTERN.matcher(line);
             
             if(matcher.find()){
                 String key = matcher.group(1);
                 String value = matcher.group(2).trim();
                 parserState.handleCurrentAttribute(key, value);
             }else{
            	 parserState.fireEndOfContigHeader();
            	 
        		 boolean endOfRecord = isEndOfRecord(line);
        		 boolean endOfContig = isEndOfContig(line);
        		 if(endOfRecord || endOfContig){                    
        			 parserState.fireEndOfRead();
            		 if(endOfContig){
            			 parserState.handleEndOfContig();
            			 parserState.beginNewContig(parser.getPosition());            			 
            		 }else{
	            		 parserState.beginNewRead();
            		 }
        		 }
             }
             
         }
         parserState.fireEndOfContigHeader();
         parserState.fireEndOfRead();
         parserState.handleEndOfContig();
         
         visitor.visitEnd();
        
    }
	

	
    
    
    
    /**
     * @param line
     * @return
     */
    private static boolean isEndOfContig(String line) {
        return line.trim().equals(END_OF_CONTIG);
    }

    private static final boolean isEndOfRecord(String line) {
        return line.trim().isEmpty();
    }
    abstract AbstractCallback createCallback(long currentOffset);
    
    
    private static class OffsetMementoCallback extends AbstractCallback{
    

		private final long offset;
		
		public OffsetMementoCallback(long offset) {
			this.offset = offset;
		}
		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public TasmVisitorMemento createMemento() {
			return new OffsetMemento(offset);
		}
    	
    }
    
    private static final class OffsetMemento implements TasmVisitorMemento{
    	private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		private final long getOffset() {
			return offset;
		}
    	
    }
    private static class NoMementoCallback extends AbstractCallback{

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public TasmVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create mementos");
		}
    	
    }
    
    private abstract static class AbstractCallback implements TasmVisitorCallback{
    	private volatile boolean keepParsing=true;   	
    	
    	@Override
		public void halt() {
    		keepParsing = false;			
		}



		public boolean keepParsing(){
    		return keepParsing;
    	}
    }
    /**
     * {@code ParserState} is an object
     * that maintains the current {@link ContigState},
     * {@link ReadState}, {@link TasmContigVisitor}
     * and {@link TasmVisitorCallback}s
     * to simplify code and reduce cyclomatic complexity.
     * @author dkatzel
     *
     */
    private class ParserState{
    	 ContigState currentContigState=new ContigState();
         ReadState currentReadState = null;
         AbstractCallback callback=null;
         TasmContigVisitor contigVisitor=null;
         
         private final TasmVisitor visitor;
         
         private long beginContigHeaderOffset;
         
         public ParserState(TasmVisitor visitor) {
			this.visitor = visitor;
		}

		public boolean keepParsing(){
        	 return callback==null || callback.keepParsing();
         }
         
         public void handleCurrentAttribute(String key, String value) throws IOException{
        	 if(currentContigState==null){
            	 currentReadState.handleAttribute(key, value);
             }else{
            	 currentContigState.handleAttribute(key, value);                      
             }
         }
         
         public void beginNewContig(long currentOffset){        	 
        	 if(keepParsing()){
    			 currentContigState=new ContigState();
    			 this.beginContigHeaderOffset = currentOffset;
			 }
         }
         
         public void beginNewRead(){
        	 currentReadState = new ReadState();
         }
         
         protected void fireEndOfRead() {
     		if(!keepParsing() || currentReadState ==null || contigVisitor==null ){
     			return;
     		}
 			 final Direction dir;
 			 final Range validRange;
 			 if(currentReadState.seqRight < currentReadState.seqLeft){
 				 dir = Direction.REVERSE;
 				 validRange = Range.of(CoordinateSystem.RESIDUE_BASED, currentReadState.seqRight, currentReadState.seqLeft);
 			 }else{
 				 dir = Direction.FORWARD;
 				 validRange = Range.of(CoordinateSystem.RESIDUE_BASED, currentReadState.seqLeft, currentReadState.seqRight);
 			 }
 			 TasmContigReadVisitor readVisitor = contigVisitor.visitRead(currentReadState.id, currentReadState.gappedStartOffset, 
 					 									dir, validRange);
 			 if(readVisitor !=null && callback.keepParsing()){
 				 readVisitor.visitBasecalls(currentReadState.sequence);
 				 readVisitor.visitEnd();
 			 }
     	}
         
         public void fireEndOfContigHeader(){
        	 if(currentContigState!=null){
        		 callback = createCallback(beginContigHeaderOffset);        		 
        		 contigVisitor= currentContigState.handleContigHeader(callback, visitor);
        		 currentReadState=null;
        	 }
        	 currentContigState=null;
         }
         
         public void handleEndOfContig(){
        	 if(contigVisitor !=null){
    			 if(callback.keepParsing()){
    				 contigVisitor.visitEnd();
    			 }else{
    				 contigVisitor.halted();
    			 }
    		 }
         }
         
        
    }
    /**
     * {@code ContigState} keeps track of the current
     * contig attributes parsed so far.
     * when the entire contig header has been
     * parsed (we start seeing read data or end of file for annotation contigs)
     * we can we can use the data collected as parameters
     * in the visit contig methods.
     * @author dkatzel
     *
     */
	private static class ContigState{
    	private String contigId;
    	private float avgCoverage=0F;
    	private NucleotideSequence consensus;
    	private Long caContigId=null;
    	private String comment, comName, editPerson, assemblyMethod;
    	private long editDate;
    	private boolean isCircular=false;
    	private int numberOfReads=0;
    	private Integer bacId;
    	
    	public void handleAttribute(String key, String value) throws IOException{
			TasmContigAttribute attribute = TasmContigAttribute.getAttributeFor(key);
    		switch(attribute){
    			case ASMBL_ID : contigId = value;
    							break;
    			case AVG_COVERAGE : avgCoverage = Float.parseFloat(value);
    							break;
    			case CA_CONTIG_ID :caContigId = Long.parseLong(value);
    							break;
    			case BAC_ID : bacId = Integer.parseInt(value);
    						break;
    			case COM_NAME : comName = value;
    					break;
    			case COMMENT : comment = value;
    				break;
    			case EDIT_DATE : try {
									editDate = TasmUtil.parseEditDate(value).getTime();
								} catch (ParseException e) {
									throw new IOException("error parsing edit date " + value, e);
								}
    				break;
    			case EDIT_PERSON : editPerson = value;
    				break;
    			case GAPPED_CONSENSUS :consensus = new NucleotideSequenceBuilder(value).build();
    				break;
    			case IS_CIRCULAR : isCircular ="1".equals(value);
    				break;
    			case METHOD : assemblyMethod = value;
    						break;
    			case NUMBER_OF_READS : numberOfReads = Integer.parseInt(value);
    								break;
				default : //do nothing
    		}
    	}
    	
    	
    	
    	public TasmContigVisitor handleContigHeader(AbstractCallback callback, TasmVisitor visitor) {
     		
    		TasmContigVisitor contigVisitor =visitor.visitContig(callback, contigId);
    		if(callback.keepParsing() && contigVisitor !=null){
     			 contigVisitor.visitConsensus(consensus);
     			 visitCaId(callback, contigVisitor);
     			 visitComments(callback, contigVisitor);
     			 visitCoverage(callback, contigVisitor);
     			 visitEnd(callback, contigVisitor);
     		 }
    		return contigVisitor;
     	}


		private void visitEnd(AbstractCallback callback,
				TasmContigVisitor contigVisitor) {
			if(callback.keepParsing()){
				 contigVisitor.visitLastEdited(editPerson, new Date(editDate));
			 }else{
				 contigVisitor.halted();
			 }
		}


		private void visitCoverage(AbstractCallback callback,
				TasmContigVisitor contigVisitor) {
			if(callback.keepParsing()){
				 contigVisitor.visitCoverageData(numberOfReads, avgCoverage);
			 }else{
				 contigVisitor.halted();
			 }
		}


		private void visitComments(AbstractCallback callback,
				TasmContigVisitor contigVisitor) {
			if(callback.keepParsing()){
				 contigVisitor.visitComments(
						 bacId, 
						 comment, 
						 comName, 
						 assemblyMethod, 
						 isCircular);
			 }else{
				 contigVisitor.halted();
			 }
		}


		private void visitCaId(AbstractCallback callback,
				TasmContigVisitor contigVisitor) {
			if(callback.keepParsing() && caContigId !=null){
				 contigVisitor.visitCeleraId(caContigId);
			 }else{
				 contigVisitor.halted();
			 }
		}
    }
	/**
     * {@code ReadState} keeps track of the current
     * read attributes parsed so far.
     * when the entire read has been
     * parsed (we start seeing the next read data or next contig or end of file)
     * we can we can use the data collected as parameters
     * in the visit read methods.
     * @author dkatzel
     *
     */
	private static class ReadState{
		private NucleotideSequence sequence;
		private String id;
		private long gappedStartOffset;
		private int seqLeft;
		private int seqRight;
		public void handleAttribute(String key, String value) throws IOException{
    		TasmReadAttribute attribute = TasmReadAttribute.getAttributeFor(key);
    		switch(attribute){
    			case GAPPED_SEQUENCE : sequence = new NucleotideSequenceBuilder(value).build();
    									break;
    			case NAME : id = value.trim();
    							break;
    			case CONTIG_START_OFFSET : gappedStartOffset = Long.parseLong(value);
    											break;
    			case SEQUENCE_LEFT : seqLeft = Integer.parseInt(value);
    										break;
    			case SEQUENCE_RIGHT : seqRight = Integer.parseInt(value);
									break;
    			default : //do nothing
			}
		}
	}
	
	
	private static final class FileBasedTasmFileParser extends TasmFileParser{
    	private final File tasmFile;
    	
    	public FileBasedTasmFileParser(File tasmFile) throws FileNotFoundException{
    		if(!tasmFile.exists()){
    			throw new FileNotFoundException("tasm file does not exist : " + tasmFile.getAbsolutePath());
    		}
    		if(!tasmFile.canRead()){
    			throw new FileNotFoundException("tasm file is not readable : " + tasmFile.getAbsolutePath());
        		
    		}
    		this.tasmFile = tasmFile;
    	}

		@Override
		protected AbstractCallback createCallback(long offset) {
			return new OffsetMementoCallback(offset);
		}

		@Override
		public void parse(TasmVisitor visitor) throws IOException {
			InputStream in = new BufferedInputStream(new FileInputStream(tasmFile));
			TextLineParser parser = new TextLineParser(in);
			try{
				parseTasm(parser, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(parser, in);
			}			
		}

		@Override
		public void parse(TasmVisitor visitor,
				TasmVisitorMemento memento) throws IOException {
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type");
			}
			
			long startOffset = ((OffsetMemento)memento).getOffset();
			long fileLength =tasmFile.length();
			if(fileLength<=startOffset){
				throw new IllegalArgumentException("memento seeks beyond file");
			}
			InputStream in=null;
			try{
				in = new BufferedInputStream( new RandomAccessFileInputStream(tasmFile, startOffset));
				TextLineParser parser = new TextLineParser(in,startOffset);
				parseTasm(parser, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}

		@Override
		public boolean canParse() {
			return true;
		}
		
		
    	
    }
	
	private static final class InputStreamBasedTasmFileParser extends TasmFileParser{
    	private final OpenAwareInputStream in;
    	
    	public InputStreamBasedTasmFileParser(InputStream in){
    		this.in = new OpenAwareInputStream(new BufferedInputStream(in));
    	}

		@Override
		protected AbstractCallback createCallback(long offset) {
			return new NoMementoCallback();
		}

		@Override
		public void parse(TasmVisitor visitor) throws IOException {
			if(!canParse()){
				throw new IOException("inputstream is closed");
			}
			TextLineParser parser = new TextLineParser(in);
			try{
				parseTasm(parser, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(parser, in);
			}			
		}
		@Override
		public boolean canParse() {
			return in.isOpen();
		}

		@Override
		public void parse(TasmVisitor visitor,
				TasmVisitorMemento memento) throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
			
		}
    	
    }
}
