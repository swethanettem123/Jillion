/*
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.Range;
import org.junit.Test;
public class TestDefaultPlacedContigClone {

    Clone clone = createMock(Clone.class);
    Contig contig = createMock(Contig.class);
    Placed seq1 = Range.buildRange(0, 10);
    Placed seq2 = Range.buildRange(5, 15);
    Placed seq3 = Range.buildRange(20, 30);
    Placed seq4 = Range.buildRange(8, 22);
   
    
    @Test
    public void emptyList(){
        List<DefaultPlacedContigClone> placedClones = new DefaultPlacedContigClone.Builder(contig, clone).build();
        
        assertTrue(placedClones.isEmpty());
    }
    
    private void assertPlacedContigCloneCorrect(Contig contig, Clone clone, Placed placed,PlacedContigClone actual ){
        assertEquals(contig, actual.getContig());
        assertEquals(clone, actual.getClone());
        assertEquals(Range.buildRange(placed.getStart(), placed.getEnd()),
                Range.buildRange(actual.getStart(), actual.getEnd()));
    }
    @Test
    public void onePlaced(){
        Set<Placed> set = new HashSet<Placed>();
        set.add(seq1);
        List<DefaultPlacedContigClone> placedClones = new DefaultPlacedContigClone.Builder(contig, clone).addAll(set).build();
        assertEquals(1, placedClones.size());
        assertPlacedContigCloneCorrect(contig, clone, seq1, placedClones.get(0));
        
    }
    
    @Test
    public void twoOverlappingPlacedShouldHaveOneLargePlacement(){
        Set<Placed> set = new HashSet<Placed>();
        set.add(seq1);
        set.add(seq2);
        List<DefaultPlacedContigClone> placedClones = new DefaultPlacedContigClone.Builder(contig, clone).addAll(set).build();
        assertEquals(1, placedClones.size());
        assertPlacedContigCloneCorrect(contig, clone, 
                Range.buildRange(0, 15), 
                placedClones.get(0));

    }
    
    @Test
    public void twoNonOverlappingPlacedShouldHaveTwoPlacements(){
        Set<Placed> set = new HashSet<Placed>();
        set.add(seq1);
        set.add(seq3);
        List<DefaultPlacedContigClone> placedClones = new DefaultPlacedContigClone.Builder(contig, clone).addAll(set).build();
        assertEquals(2, placedClones.size());
        assertPlacedContigCloneCorrect(contig, clone, seq1, placedClones.get(0));
        assertPlacedContigCloneCorrect(contig, clone, seq3, placedClones.get(1));

    }
    
    @Test
    public void mergeTwoPlacements(){
        Set<Placed> set = new HashSet<Placed>();
        set.add(seq1);
        set.add(seq3);
        set.add(seq4);
        List<DefaultPlacedContigClone> placedClones = new DefaultPlacedContigClone.Builder(contig, clone).addAll(set).build();
        assertEquals(1, placedClones.size());
        assertPlacedContigCloneCorrect(contig, clone, 
                Range.buildRange(0, 30), 
                placedClones.get(0));
    }
}
