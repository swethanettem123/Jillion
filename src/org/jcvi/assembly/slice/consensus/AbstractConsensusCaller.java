/*
 * Created on Jun 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractConsensusCaller implements ConsensusCaller{
 private final PhredQuality highQualityThreshold;
    
    public AbstractConsensusCaller(PhredQuality highQualityThreshold){
        this.highQualityThreshold = highQualityThreshold;
    }

    
    public PhredQuality getHighQualityThreshold() {
        return highQualityThreshold;
    }
    
    
    
    @Override
    public ConsensusResult callConsensus(Slice slice) {
        if(slice.getCoverageDepth() ==0){
            //by definition, an empty slice is a Gap
            return new DefaultConsensusResult(NucleotideGlyph.Gap,0);
        }
        return callConsensusWithCoverage(slice);
    }


    protected abstract ConsensusResult callConsensusWithCoverage(Slice slice);


    protected Map<NucleotideGlyph, Integer> generateBasecallHistogramMap(
            Slice slice) {
        Map<NucleotideGlyph, Integer> histogramMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice.getSliceElements()){
            NucleotideGlyph basecall =sliceElement.getBase();
            histogramMap.put(basecall, Integer.valueOf(histogramMap.get(basecall) + 1));
        }
        removeUnusedBases(histogramMap);
        return histogramMap;
    }
    protected Map<NucleotideGlyph, Integer> generateHighQualityHistogramMap(
            Slice slice) {
        Map<NucleotideGlyph, Integer> histogramMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice.getSliceElements()){
            NucleotideGlyph basecall =sliceElement.getBase();
            if(sliceElement.getQuality().compareTo(getHighQualityThreshold())>=0){
                histogramMap.put(basecall, Integer.valueOf(histogramMap.get(basecall) + 1));
            }
        }
        removeUnusedBases(histogramMap);
        return histogramMap;
    }


    private void removeUnusedBases(Map<NucleotideGlyph, Integer> histogramMap) {
        List<NucleotideGlyph> tobeRemoved = new ArrayList<NucleotideGlyph>();
        for(Entry<NucleotideGlyph, Integer> entry : histogramMap.entrySet()){
            if(entry.getValue().equals(Integer.valueOf(0))){
                tobeRemoved.add(entry.getKey());
            }
        }
        for(NucleotideGlyph baseToRemove : tobeRemoved){
            histogramMap.remove(baseToRemove);
        }
    }

    protected Map<NucleotideGlyph, Integer> generateQualityValueSumMap(Slice slice) {
        Map<NucleotideGlyph, Integer> qualityValueSumMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice.getSliceElements()){
            NucleotideGlyph basecall =sliceElement.getBase();
            final Integer previousSum = qualityValueSumMap.get(basecall);
            //ignore not ACGT-?
            if(previousSum!=null){
                qualityValueSumMap.put(basecall, Integer.valueOf(previousSum + sliceElement.getQuality().getNumber()));
            }
            
        }
        return qualityValueSumMap;
    }

    private Map<NucleotideGlyph, Integer> initalizeNucleotideMap() {
        Map<NucleotideGlyph, Integer> map = new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class);
        for(NucleotideGlyph glyph : NucleotideGlyph.getGlyphsFor("ACGT-")){
            map.put(glyph, Integer.valueOf(0));
        }
        return map;
    }
}
