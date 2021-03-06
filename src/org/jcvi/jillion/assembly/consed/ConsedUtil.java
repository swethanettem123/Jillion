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
/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.consed.ace.AceAssembledReadBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.ConsensusAceTag;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdDirDataStore;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * This class contains utility scripts for
 * converting {@link AceContig} data into
 * data that can work with Consed.
 * @author dkatzel
 *
 *
 */
public final class ConsedUtil {
	
	
    /**
     * 
     */
    private static final String CONTIG_RENAME_TAG_TYPE = "contigName";

    /**
     * Consed rename comment header which tells us what the contig SHOULD 
     * be named instead of the given ID.
     */
    private static final Pattern CONTIG_RENAME_PATTERN = Pattern.compile("U(\\w+)");
    
    private static final Pattern CONSED_ACE_PATTERN = Pattern.compile("((.+?)\\.)?ace(\\.(\\d+))?$");
    
    private static final Pattern CONSED_ACE_VERSION_PATTERN = Pattern.compile("((.+?)\\.)?ace\\.(\\d+)$");
    
    private static final Pattern ACE_CONTIG_ID_PATTERN = Pattern.compile("(\\S+)_(\\d+)_\\d+");
    
    private ConsedUtil(){
		//private constructor
	}
    /**
     * Convert a string of basecalls with '*' to 
     * represent gaps (which is what consed uses) with '-' instead. 
     * @param basecallsWithAceGaps a string of basecalls with the '*' to 
     * represent gaps.
     * @return a new string with all the '*' converted into '-'.
     */
    public static String convertAceGapsToContigGaps(String basecallsWithAceGaps) {
        return basecallsWithAceGaps.replace('*', '-');
    }
   
    public static PhdInfo generateDefaultPhdInfoFor(File traceFile, String readId,
			Date phdDate) {
		final String id;
        if(traceFile ==null){
        	id= readId;
        }else{
            final String extension = FileUtil.getExtension(traceFile.getName());
            if("sff".equals(extension)){        
                id="sff:"+traceFile.getName()+":"+readId;
            }
            else if("scf".equals(extension)){        
                id=traceFile.getName();
            }
            else{
                id= readId;
            }
        }
        return new PhdInfo(id, readId+".phd.1", phdDate);
	}
    /**
     * Split a contig which may contain zero coverage areas (0x)
     * into multiple contigs which all have at least some coverage at every
     * location.  If the given contig is split, the new contigs will be named
     * {@code <original_id>_<ungapped reference 1-based start>_<ungapped reference 1-based end>}
     * <p>
     * Some Assemblers (mostly reference assemblers) create contigs with zero coverage
     * regions (0x) but that have the reference basecalls as the consensus in those 
     * areas. This method removes the parts of the contig which only have consensus. 
     * @param contigBuilder an {@link AceContig} that may have 0x regions.  Can not be null.
     * @param adjustIdCoordinates this contig id already has coordinates appended to the end
     * of the id, adjust these coordinates instead of appending new ones...
     * @return a {@link SortedMap} of (possibly new) AceContigs of the broken given contig.
     * The keys of the map are the Ranges into the original contig where these contigs
     * are placed and the values of the  map are the (possibly new) AceContigs.
     * If there are no 0x regions in the given contig, then a Map containing
     * one entry containing the Range covered and the reference of the given contig is returned.
     */
    public static SortedMap<Range,AceContig> split0xContig(AceContigBuilder contigBuilder, boolean adjustIdCoordinates){
        List<Range> coveredRegions = new ArrayList<Range>();
        NucleotideSequence unSplitConsensus = contigBuilder.getConsensusBuilder().build();
        CoverageMap<AceAssembledReadBuilder> coverageMap = new CoverageMapBuilder<AceAssembledReadBuilder>(contigBuilder.getAllAssembledReadBuilders())
        															.build();
        for(CoverageRegion<AceAssembledReadBuilder> region : coverageMap){
            if(region.getCoverageDepth()>0){
                
                final Range contigRange =region.asRange();
                coveredRegions.add(contigRange);
            }
        }
        
        List<Range> contigRanges =Ranges.merge(coveredRegions);
        SortedMap<Range, AceContig> map = new TreeMap<Range, AceContig>(Range.Comparators.ARRIVAL);
        
        String originalContigId= contigBuilder.getContigId();
        int oldStart=1;
        if(adjustIdCoordinates){
            Matcher matcher = ACE_CONTIG_ID_PATTERN.matcher(originalContigId);
            if(matcher.matches()){
                originalContigId = matcher.group(1);
                oldStart=Integer.parseInt(matcher.group(2));
            }
        }
        
        if(contigRanges.size()==1){
            //contig in 1 piece        	
        	Range gappedContigRange = contigRanges.get(0);
			Range ungappedContigRange = AssemblyUtil.toUngappedRange(unSplitConsensus, gappedContigRange);
        	//we might still have 0x regions at the edges so check
        	//to see if we're full (ungapped) size
        	if(ungappedContigRange.getLength() <unSplitConsensus.getUngappedLength()){
        		String newContigId = computeSplitContigId(unSplitConsensus, originalContigId, oldStart, gappedContigRange);
            	contigBuilder.setContigId(newContigId);
        	}
        	map.put(gappedContigRange, contigBuilder.build());
            return map;
        }
        for(Entry<Range, AceContigBuilder> splitContigEntry: contigBuilder.split(contigRanges).entrySet()){
        	//id is now <original_id>_<ungapped 1-based start>_<ungapped 1-based end>
           
            AceContigBuilder splitContigBuilder = splitContigEntry.getValue();
            Range contigRange = splitContigEntry.getKey();
			String newContigId = computeSplitContigId(unSplitConsensus, originalContigId,
    				oldStart, contigRange);
			
			splitContigBuilder.setContigId(newContigId);
			map.put(contigRange, splitContigBuilder.build());
        }
        
        return map;
    }
	private static String computeSplitContigId(NucleotideSequence consensus,
			String originalContigId, int oldStart, Range contigRange) {
		String contigId = String.format("%s_%d_%d",originalContigId, 
                oldStart + consensus.getUngappedOffsetFor((int) contigRange.getBegin()),
                oldStart + consensus.getUngappedOffsetFor((int) contigRange.getEnd()));
		return contigId;
	}
    /**
     * Checks to see if the given {@link ConsensusAceTag} is denotes
     * that the contig has been renamed.
     * @param consensusTag the tag to check.
     * @return {@code true} if this tag denotes a contig rename; {@code false}
     * otherwise.
     * @throws NullPointerException if consensusTag is null.
     */
    public static boolean isContigRename(ConsensusAceTag consensusTag){
        return CONTIG_RENAME_TAG_TYPE.equals(consensusTag.getType());
    }
    /**
     * Get the new name this contig should be named according to the given
     * rename tag.
     * @param contigRenameTag a {@link ConsensusAceTag} that denotes
     * the contig has been renamed.
     * @return the new name that the contig should be renamed to.
     * @throws NullPointerException if contigRenameTag is null.
     * @throws IllegalArgumentException if the given tag is not a contig rename
     * tag or if the tag text does not match the known pattern for 
     * contig renames.
     */
    public static String getRenamedContigId(ConsensusAceTag contigRenameTag){
        if(!isContigRename(contigRenameTag)){
            throw new IllegalArgumentException("not a contig rename");
        }
        String data= contigRenameTag.getData();
        Matcher matcher = CONTIG_RENAME_PATTERN.matcher(data);
        if(matcher.find()){
            return matcher.group(1);
        }
        throw new IllegalArgumentException("consensus tag does not contain rename info : "+contigRenameTag);
    }
    /**
     * Get the ace file from the given editDir
     * with the given ace file prefix and
     * the given version.
     * @param editDir the consed edit dir the ace file is in,
     * if this parameter is null, then no editDir exists.
     * @param filenamePrefix prefix to ace file name
     * before the ".ace.$version"; can not be null.
     * @param version the ace version number;
     * must be &ge; 1
     * @return the {@link File} to that ace file;
     * or {@code null} if the specified ace does not exist
     * or the editDir is {@code null} .
     * @throws IllegalArgumentException if version &lt; 1.
     */
    public static File getAceFile(File editDir, String filenamePrefix, int version){
    	if(filenamePrefix == null){
    		throw new NullPointerException("file name prefix can not be null");
    	}
    	if(version <0){
			throw new IllegalArgumentException("version must be >= 1");
		}
    	//don't need to check if edit_dir
    	//doesn't exist
    	//since we later check 
    	//if the ace exists
    	//which will also check if parent dir exists
    	//need to also check that it does not exist
    	if(editDir==null){
    		return null;
    	}
    	File aceFile = new File(editDir, String.format("%s.ace.%d",filenamePrefix, version));
    	if(aceFile.exists()){
    		return aceFile;
    	}
    	return null;
    }
    /**
     * Gets the latest ace file with the given prefix in the given edit_dir.
     * 
     *<p>
     *Consed labels each version of the ace file with a incrementing     *
     *value so {@code prefix.ace.2} is newer than {@code prefix.ace.1}.
     *
     * @param editDir the consed edit_dir folder to inspect.
     * If this parameter is null, then this method will
     * return null.
     * @param filenamePrefix the beginning part of the file name to filter,
     * incase there are more than 1 groups of versioned assemblies.
     * 
     * @return the File object representing the latest version of the ace file
     * with the given prefix in the given edit_dir; {@code null}
     * if no such file exists or if editDir is also {@code null}.
     */
    public static File getLatestAceFile(File editDir, final String filenamePrefix){
    	//need to also check that it does not exist
    	if(editDir==null || !editDir.exists()){
    		return null;
    	}
        int highestAceFileVersion=Integer.MIN_VALUE;
        File highestAceFile=null;
        try{
        for(File file : editDir.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.startsWith(filenamePrefix) && CONSED_ACE_PATTERN.matcher(name).find();
            }
        
     })){
           
            int version = getAceVersionFor(file);
            if(version > highestAceFileVersion){
                highestAceFileVersion=version;
                highestAceFile = file;
            }
        }
        return highestAceFile;
        }catch(NullPointerException e){
        	throw e;
        }
    }
    
    public static File generateNextAceFileFor(File editDir, String filenamePrefix){
    	File latestAceFile = getLatestAceFile(editDir, filenamePrefix);
    	if(latestAceFile ==null){
    		//no ace file make the .1
    		return new File(editDir, filenamePrefix + ".ace.1");
    	}
    	return new File(editDir, generateNextAceVersionNameFor(latestAceFile));
    }
    
    
    public static File getPhdDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"phd_dir");
    }
	private static void verifyNotNull(File consedDir) {
		if(consedDir==null){
            throw new NullPointerException("consedDir can not be null");
        }
	}
	/**
	 * Get the File representing the Consed
	 * "edit_dir" of this consed package.
	 * @param consedDir the consed dir to use;
	 * can not be null, but might not exist.
	 * @return a new File object with representing
	 * the edit_dir, this File may not exist on the file system.
	 * @throws NullPointerException if consedDir is null.
	 */
    public static File getEditDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"edit_dir");
    }
    public static File getChromatDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"chromat_dir");
    }
    public static File getPhdBallDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"phdball_dir");
    }
    public static int getAceVersionFor(File consedAceFile){
        String name = consedAceFile.getName();
        Matcher matcher = CONSED_ACE_VERSION_PATTERN.matcher(name);
        if(!matcher.matches()){
            throw new IllegalArgumentException("could not parse version from "+ name);
        }
        return Integer.parseInt(matcher.group(3));
    }
    /**
     * Get the root consed directory for the given 
     * ace file.
     * @param aceFile the ace file to use; can not be null
     * but does not have to exist.
     * @return a File which may or may not exist
     * @throws NullPointerException if aceFile is null.
     */
    public static File getConsedDirFor(File aceFile){
		//consed/edit_dir/ace.version
		return aceFile.getParentFile().getParentFile();
    }
    /**
     * Gets the part of the ace file name
     * before the ".ace" part.  This method
     * works on files that end in either
     * ".ace" or ".ace.\d+".
     * 
     * @param aceFile the ace {@link File} whose
     * name is to be parsed;
     * can not be null.
     * @return the part of the name before ".ace".
     * @throws NullPointerException if aceFile is null.
     * 
     * @apiNote This is the same as
     * <pre>
     * {@code getAcePrefixFor(aceFile.getName())}
     * </pre>
     * 
     * @see #getAcePrefixFor(String)
     */
    public static String getAcePrefixFor(File aceFile){
    	return getAcePrefixFor(aceFile.getName());
    }
    /**
     * Gets the part of the ace file name
     * before the ".ace" part.  This method
     * works on files that end in either
     * ".ace" or ".ace.\d+".
     * 
     * @param aceFileName the ace file name to parse;
     * can not be null.
     * @return the part of the name before ".ace".
     * @throws NullPointerException if aceFileName is null.
     */
    public static String getAcePrefixFor(String aceFileName){
    	//supports both ace.version
    	//and just plain .ace
    	return aceFileName.replaceAll("\\.ace(\\.\\d+)?$", "");
    }
    
    public static String generateNextAceVersionNameFor(File consedAceFile){
        String name = consedAceFile.getName();
        Matcher matcher = CONSED_ACE_VERSION_PATTERN.matcher(name);
        if(!matcher.matches()){
            throw new IllegalArgumentException("could not parse version from "+ name);
        }
        String prefix = matcher.group(2);
        int version= Integer.parseInt(matcher.group(3));
        
        return String.format("%sace.%d",
                prefix==null?"": prefix+".", 
                        version+1);
    }
    
    
    public static PhdDataStore createPhdDataStoreFor(File consedDir) throws IOException{
    	if(consedDir == null){
			throw new NullPointerException("consed dir can not be null");
		}
		 File phdDir = ConsedUtil.getPhdDirFor(consedDir);
         File phdballDir = ConsedUtil.getPhdBallDirFor(consedDir);
         List<PhdDataStore> datastores = new ArrayList<PhdDataStore>();
         if(phdDir.exists()){
        	 datastores.add(new PhdDirDataStore(phdDir));
         }
         if(phdballDir.exists()){
        	 datastores.add(new PhdDirDataStore(phdballDir));
         }
         return DataStore.chain(PhdDataStore.class, datastores);
    }
    
    public static enum ClipPointsType{
    	VALID,
    	NEGATIVE_VALID_RANGE,
    	ALL_LOW_QUALITY,
    	NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION
    	;
    	
    	public static ClipPointsType getType(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			if(qualLeft == -1 && qualRight ==-1){
				return ClipPointsType.ALL_LOW_QUALITY;
	        }
	        if((qualRight-qualLeft) <0){
	            //invalid converted ace file? 
	            return ClipPointsType.NEGATIVE_VALID_RANGE;
	        }    
	        
	        //dkatzel 4/2011 - There have been cases when qual coords and align coords
	        //do not match; usually qual is a sub set of align
	        //but occasionally, qual goes beyond the align coords.
	        //I guess this happens in a referenced based alignment for
	        //reads at the edges when the reads have good quality 
	        //beyond the reference.
	        //It might also be possible that the read has been 
	        //edited and that could have changed the coordinates.
	        //Therefore intersect the qual and align coords
	        //to find the region we are interested in
	        Range qualityRange = Range.of(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
	        Range alignmentRange = Range.of(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
	        if(qualityRange.intersects(alignmentRange)){
	        	return ClipPointsType.VALID;
	        }else{	        
	        	//no intersection! 
	        	//I've only seen this on really bad quality
	        	//////////////////////////////////////////////////////////
	        	//dkatzel - 2012-01-26
	        	//email response from David Gordon (author of consed)
	        	//regarding what to do if these ranges don't overlap
	        	//From David Gordon:
	        	//the first 2 numbers indicate the high quality segment
	        	//(roughly corresponding to that above quality 13).
	        	//The last 2 numbers indicates the portion of the read aligned
	        	//to the consensus sequence.
	        	//
	        	//Hence there is a very short high quality segment 
	        	//634-649 (only 16 bases).  And the portion of the 
	        	//read aligned to the consensus is 851-1758 is very low quality.
	        	//
	        	//Consed treats these reads like any others.  
	        	//The "dim" menu on the Aligned Reads Window 
	        	//indicates what portion of the read to dim.  
	        	//If you set it on dim both low quality and unaligned, 
	        	//this entire read would be dimmed.
	        	/////////////////////////////////////////////////////////
	        	//dkatzel -therefore if consed dims the entire read
	        	//that's enough justification for me to throw the read out
	        	return ClipPointsType.NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION;
	        }
		}
    }
    
    
}
