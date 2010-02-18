/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.cas.CasIdLookup;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class AceContigAdapter implements AceContig{

    private final Contig<PlacedRead> delegate;
    private final Map<String, AcePlacedRead> adaptedReads = new HashMap<String, AcePlacedRead>();
    
    /**
     * @param delegate
     */
    public AceContigAdapter(Contig<PlacedRead> delegate, Date phdDate,CasIdLookup idLookup) {
        this.delegate = delegate;
        for(PlacedRead read : delegate.getPlacedReads()){
            final String readId = read.getId();
            adaptedReads.put(readId, new AcePlacedReadAdapter(read,
                    phdDate, 
                    idLookup.getFileFor(readId)));
        }
    }

    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return delegate.containsPlacedRead(placedReadId);
    }

    @Override
    public NucleotideEncodedGlyphs getConsensus() {
        return delegate.getConsensus();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    @Override
    public VirtualPlacedRead<AcePlacedRead> getPlacedReadById(String id) {
     // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<AcePlacedRead> getPlacedReads() {
        return new HashSet<AcePlacedRead>(adaptedReads.values());
    }

    @Override
    public Set<VirtualPlacedRead<AcePlacedRead>> getVirtualPlacedReads() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCircular() {
        // TODO Auto-generated method stub
        return delegate.isCircular();
    }

    @Override
    public AceContig without(List<AcePlacedRead> reads) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
