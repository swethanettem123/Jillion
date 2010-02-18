/*
 * Created on Jun 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.contigChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.assembly.ContigCheckerXMLWriter;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AceFileVisitor;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.analysis.ContigChecker;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.datastore.ContigDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.idReader.IdReaderException;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.TraceQualityDataStoreAdapter;
import org.jcvi.trace.sanger.phd.LargePhdDataStoreFactory;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStoreFactory;
import org.jcvi.util.DefaultMemoryMappedFileRange;

public class ContigCheckerAceFile {

    /**
     * Run the generic contig checker from a contig file
     * @param args 
     *              0 - ace file
     *              1 - phd ball file
     *              2 - outputDir
     *              3 - prefix
     *              4 - low seq cov threshold
     *              5 - high seq cov threshold
     *              6 percent read direction difference threshold
     *              7 - max Coverage (used to determine cache size)
     * @throws IOException 
     * @throws IOException
     * @throws DataStoreException 
     * @throws DataStoreException 
     * @throws TraceDecoderException 
     * @throws IdReaderException 
     */
    public static void main(String[] args) throws IOException, DataStoreException, TraceDecoderException {
        if(args.length != 8){
            throw new IllegalArgumentException("invalid parameters");
        }
        File aceFile = new File(args[0]);
        File phdBallFile = new File(args[1]);
        String outputBasePath = args[2];
        String prefix =args[3];
        
        int lowSequenceCoverageThreshold=Integer.parseInt(args[4]);
        int highSequenceCoverageThreshold=Integer.parseInt(args[5]);
        int percentReadDirectionDifferenceTheshold=Integer.parseInt(args[6]);
        int maxCoverage = Integer.parseInt(args[7]);
        ContigDataStore<AcePlacedRead, AceContig> contigDataStore = new MemoryMappedAceFileDataStore(aceFile, new DefaultMemoryMappedFileRange());
        AceFileParser.parseAceFile(aceFile, (AceFileVisitor)contigDataStore);
        PhdDataStoreFactory phdDataStoreFactory = new LargePhdDataStoreFactory(maxCoverage);
        PhdDataStore phdDataStore = phdDataStoreFactory.createPhdDataStoreFactoryFor(phdBallFile);
       
        
        
        for(Iterator<String>contigIdIterator=contigDataStore.getIds(); contigIdIterator.hasNext();){
            
            final String id = contigIdIterator.next();
            System.out.println(id);
            AceContig contig =contigDataStore.get(id) ;
            final QualityDataStore qualityFastaDataStore = 
                                new TraceQualityDataStoreAdapter<Phd>(phdDataStore);
            
            ContigCheckerStruct<AcePlacedRead> struct = new ContigCheckerStruct<AcePlacedRead>(contig, 
                    qualityFastaDataStore, PhredQuality.valueOf(30));
            ContigChecker<AcePlacedRead> contigChecker = new ContigChecker<AcePlacedRead>(struct, percentReadDirectionDifferenceTheshold,
                    lowSequenceCoverageThreshold, highSequenceCoverageThreshold);
            contigChecker.run();
            ContigCheckerUtil.writeContigCheckerResults(outputBasePath, prefix,id,struct, contigChecker,true);
            final String contigPrefix = outputBasePath+"/"+prefix+"."+id;
            OutputStream xmlOut = new FileOutputStream(contigPrefix + ".contigChecker.xml");
            ContigCheckerXMLWriter<AcePlacedRead> xmlWriter = new ContigCheckerXMLWriter<AcePlacedRead>(xmlOut);
            xmlWriter.write(struct);
            xmlWriter.close();  
            
        }
    }
    
    private static AceContig removeReferenceFromContig(
            PhdDataStore phdDataStore, AceContig contig) throws DataStoreException {
        List<AcePlacedRead> readsToRemove = new ArrayList<AcePlacedRead>();
        for(AcePlacedRead read : contig.getPlacedReads()){
            if(!phdDataStore.contains(read.getId())){
                readsToRemove.add(read);
            }
        }
        if(readsToRemove.isEmpty()){
            return contig;
        }
        return contig.without(readsToRemove);
    }

}
