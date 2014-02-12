package org.jcvi.jillion.sam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.attribute.SamAttributeKeyFactory;
import org.jcvi.jillion.sam.attribute.SamAttributeType;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;

public class SamFileParser extends AbstractSamFileParser{
	
	private static final Pattern SPLIT_LINE_PATTERN = Pattern.compile("\t");
	
	private static final Pattern TYPED_TAG_VALUE_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9]):(([AifZHB]):)?(.+)");
	
	
	private final File samFile;
	private final SamAttributeValidator validator;
	
	public SamFileParser(File samFile) throws IOException {
		if(samFile ==null){
			throw new NullPointerException("sam file can not be null");
		}
		if(!FileUtil.getExtension(samFile).equals("sam")){
			throw new IllegalArgumentException("must be .sam file" + samFile.getAbsolutePath());
		}
		if(!samFile.exists()){
			throw new FileNotFoundException(samFile.getAbsolutePath());
		}
		if(!samFile.canRead()){
			throw new IllegalArgumentException("sam file not readable " + samFile.getAbsolutePath());
		}
		this.samFile = samFile;
		validator = ReservedAttributeValidator.INSTANCE;
	}

	@Override
	public boolean canAccept() {
		return true;
	}

	
	@Override
	public void accept(SamVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		TextLineParser parser =null;
		
		try{
			parser= new TextLineParser(samFile);
			
			SamHeader header = parseHeader(parser).build();
			visitor.visitHeader(header);
			while(parser.hasNextLine()){
				String line = parser.nextLine().trim();
				if(line.isEmpty()){
					//skip blanks?
					continue;
				}			
				SamRecord record = parseRecord(header, line);
				visitor.visitRecord(record);
			}
			visitor.visitEnd();
		}finally{
			IOUtil.closeAndIgnoreErrors(parser);
		}
	}
	
	private SamRecord parseRecord(SamHeader header, String line) throws IOException{
		String[] fields = SPLIT_LINE_PATTERN.split(line);
		if(fields.length <11){
			//not a sam line?
			throw new IOException("invalid sam record line : " + line);
		}
		SamRecord.Builder builder = new SamRecord.Builder(header, validator);
		
		builder.setQueryName(fields[0]);
		builder.setFlags(SamRecordFlags.parseFlags(Integer.parseInt(fields[1])));
		builder.setReferenceName(fields[2]);
		builder.setStartPosition(Integer.parseInt(fields[3]));
		builder.setMappingQuality(Byte.parseByte(fields[4]));
		builder.setCigar(Cigar.parse(fields[5]));
		builder.setNextReferenceName(fields[6]);
		builder.setNextPosition(Integer.parseInt(fields[7]));
		builder.setObservedTemplateLength(Integer.parseInt(fields[8]));
		builder.setSequence(parseSequence(fields[9]));
		builder.setQualities(parseQualities(fields[10]));
		
		//anything else is an optional field
		for(int i=11; i<fields.length; i++){
			Matcher matcher = TYPED_TAG_VALUE_PATTERN.matcher(fields[i]);
			if(matcher.matches()){
				String key = matcher.group(1);
				String optionalType = matcher.group(3);
				String value = matcher.group(4);
				if(optionalType == null){
					//type not specified, check is reserved?
					ReservedSamAttributeKeys reserved =ReservedSamAttributeKeys.parseKey(key);
					if(reserved ==null){
						//not reserved...
						throw new IOException("unknown optional attribute without type information (not reserved) : "  + fields[i]);
					}
					try {
						builder.addAttribute(new SamAttribute(reserved, value));
					} catch (InvalidAttributeException e) {
						throw new IOException("invalid attribute value for " + fields[i], e);
					}
				}else{
					SamAttributeKey customKey = SamAttributeKeyFactory.getKey(key);
					SamAttributeType type = SamAttributeType.parseType(optionalType.charAt(0), value);
					try {
						builder.addAttribute(new SamAttribute(customKey, type, value));
					} catch (InvalidAttributeException e) {
						throw new IOException("invalid attribute value for " + fields[i], e);
					}
				}
			}else{
				throw new IOException("invalid attribute format " + fields[i]);
			}
		}
		return builder.build();
	}
	private static NucleotideSequence parseSequence(String s){
		if(SamRecord.UNAVAILABLE.equals(s)){
			return null;
		}
		return new NucleotideSequenceBuilder(s).build();
	}
	private static QualitySequence parseQualities(String s){
		if(SamRecord.UNAVAILABLE.equals(s)){
			return null;
		}
		//always encoded in sanger format
		return FastqQualityCodec.SANGER.decode(s);

	}

	
	

	
	
	
	
	
	
}