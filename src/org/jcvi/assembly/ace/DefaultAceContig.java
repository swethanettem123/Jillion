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
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.contig.AbstractContig;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class  DefaultAceContig extends AbstractContig<AcePlacedRead> implements AceContig{

    

    private DefaultAceContig(String id, NucleotideSequence consensus,
            Set<AcePlacedRead> reads) {
        super(id, consensus, reads);
    }

    public static class Builder{
        private NucleotideSequence fullConsensus;
        private String contigId;
        private Logger logger = Logger.getRootLogger();
        private CoordinateSystem adjustedContigIdCoordinateSystem=null;
        
        private List<DefaultAcePlacedRead.Builder> aceReadBuilders = new ArrayList<DefaultAcePlacedRead.Builder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        
        public Builder(String contigId, String fullConsensus){
           this(contigId,
        		   new DefaultNucleotideSequence(
                    NucleotideGlyph.getGlyphsFor(ConsedUtil.convertAceGapsToContigGaps(fullConsensus)))
            );
        }
        public Builder(String contigId, NucleotideSequence fullConsensus){
        	this.fullConsensus = fullConsensus;
        	 this.contigId = contigId;
        }
        
        public Builder adjustContigIdToReflectCoordinates(CoordinateSystem coordinateSystem){
            adjustedContigIdCoordinateSystem = coordinateSystem;
            return this;
        }
        public Builder setContigId(String contigId){
            this.contigId = contigId;
            return this;
        }
        public String getContigId() {
            return contigId;
        }

        public Builder logger(Logger logger){
            this.logger = logger;
            return this;
        }
        public int numberOfReads(){
            return aceReadBuilders.size();
        }
        
        public Builder addRead(AcePlacedRead acePlacedRead) {
         return addRead(acePlacedRead.getId(),
        		 NucleotideGlyph.convertToString(acePlacedRead.getEncodedGlyphs().decode()),
        		 (int)acePlacedRead.getStart(),
        		 acePlacedRead.getSequenceDirection(),
        		 acePlacedRead.getValidRange(),
        		 acePlacedRead.getPhdInfo(),
        		 acePlacedRead.getUngappedFullLength());
        }
    	@Deprecated
        public Builder addRead(String readId, String validBases, int offset,
                SequenceDirection dir, Range clearRange,PhdInfo phdInfo){
            return addRead(readId, validBases, offset, dir, clearRange, phdInfo,
                    validBases.replaceAll("-", "").length());
        }
        public Builder addRead(String readId, String validBases, int offset,
                SequenceDirection dir, Range clearRange,PhdInfo phdInfo,int ungappedFullLength) {
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211
            int correctedOffset = Math.max(0,offset);
            adjustContigLeftAndRight(validBases, correctedOffset);
            try{
                DefaultAcePlacedRead.Builder aceReadBuilder = createNewAceReadBuilder(readId, validBases, correctedOffset, dir, 
                        clearRange,phdInfo,ungappedFullLength);
                
                
                aceReadBuilders.add(aceReadBuilder);
            }catch(Exception e){
                logger.error("could not add read "+ readId, e);               
            }
            return this;
        }
        private DefaultAcePlacedRead.Builder createNewAceReadBuilder(
                String readId, String validBases, int offset,
                SequenceDirection dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength) {
            return new DefaultAcePlacedRead.Builder(
                    fullConsensus,readId,
                    ConsedUtil.convertAceGapsToContigGaps(validBases),
                    offset,dir,clearRange,phdInfo,ungappedFullLength);
        }
        private void adjustContigLeftAndRight(String validBases, int offset) {
            adjustContigLeft(offset);
            adjustContigRight(validBases, offset);
        }
        private void adjustContigRight(String validBases, int offset) {
            final int endOfNewRead = offset+ validBases.length();
            if(endOfNewRead <= fullConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private void adjustContigLeft(int offset) {
            
            if(contigLeft ==-1 || offset <contigLeft){
                contigLeft = offset;
            }
        }
        public DefaultAceContig build(){
            Set<AcePlacedRead> placedReads = new HashSet<AcePlacedRead>(aceReadBuilders.size());
            
            if(numberOfReads()==0){
                //force empty contig if no reads...
                return new DefaultAceContig(contigId, new DefaultNucleotideSequence(""),placedReads);
            }
            
            List<NucleotideGlyph> updatedConsensus = updateConsensus(fullConsensus.decode());
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211
            contigLeft = Math.max(contigLeft, 0);
            contigRight = Math.min(contigRight,(int)fullConsensus.getLength());
            //here only include the gapped valid range consensus bases
            //throw away the rest
            final List<NucleotideGlyph> validConsensusGlyphs = 
                    new ArrayList<NucleotideGlyph>(
                            updatedConsensus.subList(contigLeft, contigRight));
            
            NucleotideSequence validConsensus = new DefaultNucleotideSequence(validConsensusGlyphs);
            for(DefaultAcePlacedRead.Builder aceReadBuilder : aceReadBuilders){
                int newOffset = aceReadBuilder.offset() - contigLeft;
                aceReadBuilder.reference(validConsensus,newOffset);
                placedReads.add(aceReadBuilder.build());                
            }
            final String newContigId;
            if(adjustedContigIdCoordinateSystem !=null){
                Range gappedContigRange = Range.buildRange(contigLeft, contigRight);
                Range ungappedContigRange = validConsensus.convertGappedValidRangeToUngappedValidRange(gappedContigRange)
                                    .convertRange(adjustedContigIdCoordinateSystem);
                //contig left and right are in 0 based use
                newContigId = String.format("%s_%d_%d",contigId,
                        ungappedContigRange.getLocalStart(),
                        ungappedContigRange.getLocalEnd());
            }else{
                newContigId = contigId;
            }
            return new DefaultAceContig(newContigId, validConsensus,placedReads);
        }
        
        protected List<NucleotideGlyph> updateConsensus(List<NucleotideGlyph> validConsensusGlyphs){
            return validConsensusGlyphs;
        }
    }
    
}
