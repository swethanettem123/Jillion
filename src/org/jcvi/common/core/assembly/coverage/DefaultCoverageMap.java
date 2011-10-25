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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.coverage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;


public class DefaultCoverageMap<V extends Placed,T extends CoverageRegion<V>> implements CoverageMap<T> {


    @SuppressWarnings("unchecked")
    public static <V extends Placed,T extends CoverageRegion<V>> DefaultCoverageMap<V,T> 
            buildCoverageMap(Collection<V> elements){
        return (DefaultCoverageMap<V,T>)new Builder(elements).build();
    }
    @SuppressWarnings("unchecked")
    public static <V extends Placed,T extends CoverageRegion<V>> DefaultCoverageMap<V,T> 
            buildCoverageMap(Collection<V> elements, int maxAllowedCoverage){
        return (DefaultCoverageMap<V,T>)new Builder(elements,maxAllowedCoverage).build();
    }
    public static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> DefaultCoverageMap<PR,T> 
        buildCoverageMap(C contig){
            return (DefaultCoverageMap<PR,T>)new Builder(contig.getPlacedReads()).build();
    }
    public static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> DefaultCoverageMap<PR,T>    
        buildCoverageMap(C contig, int maxAllowedCoverage){
            return (DefaultCoverageMap<PR,T>)new Builder(contig.getPlacedReads(),maxAllowedCoverage).build();
    }
    private static class PlacedStartComparator <T extends Placed> implements Comparator<T>,Serializable {       
        /**
         * 
         */
        private static final long serialVersionUID = -8517894363563047881L;

        @Override
        public int compare(T o1, T o2) {           
            return Long.valueOf(o1.getStart()).compareTo(o2.getStart());
        }

    }
    
    private static class PlacedEndComparator<T extends Placed> implements Comparator<T>, Serializable {       
        /**
         * 
         */
        private static final long serialVersionUID = 5135449151100427846L;

        @Override
        public int compare(T o1, T o2) {           
            return Long.valueOf(o1.getEnd()).compareTo(o2.getEnd());
        }
            
    }
    
    private List<T> regions;
    private double avgCoverage;
    private boolean avgCoverageSet;
    /**
     *
     * Creates a new <code>AbstractCoverageMap</code>.
     * @param amplicons A {@link Collection} of {@link Coordinated}s.
     */
    public DefaultCoverageMap(List<T> regions){
        this.regions = regions;
    }
    @Override
    public int getNumberOfRegions() {
        return regions.size();
    }
    @Override
    public T getRegion(int i) {
        return regions.get(i);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj instanceof DefaultCoverageMap){
            DefaultCoverageMap other = (DefaultCoverageMap) obj;
            if(getNumberOfRegions() !=other.getNumberOfRegions()){
                return false;
            }
            for( int i=0; i<getNumberOfRegions(); i++){
                if(!getRegion(i).equals(other.getRegion(i))){
                    return false;
                }
            }
            return true;
        }
       return false;
    }

      public int hashCode(){
          final int prime = 37;
          int ret = 17;
          for(T region : regions){
              ret = ret*prime + region.hashCode();
          }
          return ret;
      }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for(T region : regions){
            buf.append(region);
            buf.append("\n");
        }
        return buf.toString();
    }
    
    @Override
    public Iterator<T> iterator() {
        return regions.iterator();
    }

    @Override
    public List<T> getRegionsWithin(Range range) {
        List<T> selectedRegions = new ArrayList<T>();
        for(T region : regions){
            Range regionRange = Range.buildRange(region.getStart(), region.getEnd());
            if(regionRange.isSubRangeOf(range)){
                selectedRegions.add(region);
            }
        }
        return selectedRegions;
    }
    
    @Override
    public List<T> getRegionsWhichIntersect(Range range) {
        List<T> selectedRegions = new ArrayList<T>();
        for(T region : regions){
            Range regionRange = Range.buildRange(region.getStart(), region.getEnd());
            if(range.endsBefore(regionRange)){
                break;
            }
            if(regionRange.intersects(range)){
                selectedRegions.add(region);
            }
            
        }
        return selectedRegions;
    }
    

    @Override
    public T getRegionWhichCovers(long consensusIndex) {
        Range range = Range.buildRange(consensusIndex, consensusIndex);
        final List<T> intersectedRegion = getRegionsWhichIntersect(range);
        if(intersectedRegion.isEmpty()){
            return null;
        }
        return intersectedRegion.get(0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfRegionsWithCoverage(int coverageDepth) {
        return getRegionsWithCoverage(coverageDepth).size();
    }

    @Override
    public List<T> getRegionsWithCoverage(int coverageDepth) {
        List<T> regionsWithCoverage = new ArrayList<T>();
        for(T coverageRegion: regions){
            if(coverageRegion.getCoverage() == coverageDepth){
                regionsWithCoverage.add(coverageRegion);
            }
        }
        return regionsWithCoverage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfRegionsWithAtLeastCoverage(int coverageDepth) {
        int i=0;
        for(T coverageRegion: regions){
            if(coverageRegion.getCoverage() >= coverageDepth){
                i++;
            }
        }
        return i;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLengthOfRegionsWithCoverage(int coverageDepth) {
        long length=0;
        for(T coverageRegion: regions){
            if(coverageRegion.getCoverage() == coverageDepth){
                length +=coverageRegion.getLength();
            }
        }
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLengthOfRegionsWithAtLeastCoverage(int coverageDepth) {
        long length=0;
        for(T coverageRegion: regions){
            if(coverageRegion.getCoverage() >= coverageDepth){
                length +=coverageRegion.getLength();
            }
        }
        return length;
    }
    
    public static  class Builder<P extends Placed> extends AbstractCoverageMapBuilder<P,CoverageRegion<P>>{
        private final List<P> startCoordinateSortedList = new ArrayList<P>();
        private final List<P> endCoordinateSortedList = new ArrayList<P>();
        
        public Builder(Collection<P> elements, int maxAllowedCoverage){
            super(maxAllowedCoverage);
            initialize(elements);
        }
        public Builder(Collection<P> elements) {
            initialize(elements);
            
        }
        
        private final void initialize(Collection<P> elements){
            startCoordinateSortedList.addAll(elements);
            endCoordinateSortedList.addAll(elements);
            filterAmpliconsWithoutCoordinates(startCoordinateSortedList);
            filterAmpliconsWithoutCoordinates(endCoordinateSortedList);
            Collections.sort(startCoordinateSortedList,
                    new PlacedStartComparator<P>());
            Collections.sort(endCoordinateSortedList, new PlacedEndComparator<P>());
        }
        /**
         * If there are no coordinates (start or end are null) then we remove them
         * so they don't mess up our computations.
         * 
         * @param amp
         */
        private void filterAmpliconsWithoutCoordinates(Collection<P> amp) {
            for (Iterator<P> it = amp.iterator(); it.hasNext();) {
                P entry = it.next();
                if (entry.getLength() == 0) {
                    it.remove();
                }
            }
        }
        @Override
        protected CoverageRegionBuilder<P> createNewCoverageRegionBuilder(
                Collection<P> elements, long start) {
            return new DefaultCoverageRegion.Builder<P>(start, elements);
        }

        private List<CoverageRegion<P>> buildAllCoverageRegions(List<CoverageRegionBuilder<P>> coverageRegionBuilders) {
            
            List<CoverageRegion<P>> regions = new ArrayList<CoverageRegion<P>>(
                    coverageRegionBuilders.size());
            for (CoverageRegionBuilder<P> builder : coverageRegionBuilders) {
                regions.add(builder.build());
            }
            return regions;
        }

        @Override
        protected CoverageMap<CoverageRegion<P>> build(
                List<CoverageRegionBuilder<P>> coverageRegionBuilders) {
            return new DefaultCoverageMap<P,CoverageRegion<P>>(
                    buildAllCoverageRegions(coverageRegionBuilders));
        }

        @Override
        protected Iterator<P> createEnteringIterator() {
            return startCoordinateSortedList.iterator();
        }

        @Override
        protected Iterator<P> createLeavingIterator() {
            return endCoordinateSortedList.iterator();
        }
        
    }

    
    @Override
    public List<T> getRegions() {
        return Collections.unmodifiableList(regions);
    }
    @Override
    public synchronized double getAverageCoverage() {
        if(avgCoverageSet){
            return avgCoverage;
        }        
        avgCoverage= computeAvgCoverage();
        avgCoverageSet=true;
        return avgCoverage;
    }
    private double computeAvgCoverage() {
        if(getNumberOfRegions()==0){
            //no coverage
            return 0F;
        }
        long total=0;
        long length=0;
        for(T coverageRegion : getRegions()){
            total += coverageRegion.getLength() * coverageRegion.getCoverage();
            length += coverageRegion.getLength();
        }
        return ((double)total)/length;
    }
    @Override
    public int getRegionIndexWhichCovers(long consensusIndex) {
        T region = getRegionWhichCovers(consensusIndex);
        
        return regions.indexOf(region);
    }
    @Override
    public int getMaxCoverage() {
        if(regions.isEmpty()){
            return 0;
        }
        int maxCoverage=0;
        for(T region : regions){
            maxCoverage = Math.max(maxCoverage, region.getCoverage());
        }
        return maxCoverage;
    }
    @Override
    public int getMinCoverage() {
        if(regions.isEmpty()){
            return 0;
        }
        int minCoverage=Integer.MAX_VALUE;
        for(T region : regions){
            minCoverage = Math.min(minCoverage, region.getCoverage());
        }
        return minCoverage;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        if(isEmpty()){
            return 0L;
        }
        return regions.get(regions.size()-1).getEnd()+1;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isEmpty() {
        return regions.isEmpty();
    }


}



