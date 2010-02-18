/*
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.util.Arrays;

import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.DefaultConfidence;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;


public class SCFChromatogramBuilder extends BasicChromatogramBuilder{

    
    private byte[] substitutionConfidence;
    private byte[] insertionConfidence;
    private byte[] deletionConfidence;

    private byte[] privateData;
   

    /**
     * @return the substitutionConfidence
     */
    public byte[] substitutionConfidence() {
        return Arrays.copyOf(substitutionConfidence, substitutionConfidence.length);
    }

    /**
     * @param substitutionConfidence the substitutionConfidence to set
     */
    public SCFChromatogramBuilder substitutionConfidence(byte[] substitutionConfidence) {
        this.substitutionConfidence = Arrays.copyOf(substitutionConfidence, substitutionConfidence.length);
        return this;
    }

    /**
     * @return the insertionConfidence
     */
    public byte[] insertionConfidence() {
        return Arrays.copyOf(insertionConfidence, insertionConfidence.length);
    }

    /**
     * @param insertionConfidence the insertionConfidence to set
     */
    public SCFChromatogramBuilder insertionConfidence(byte[] insertionConfidence) {
        this.insertionConfidence = Arrays.copyOf(insertionConfidence, insertionConfidence.length);
        return this;
    }

    /**
     * @return the deletionConfidence
     */
    public byte[] deletionConfidence() {
        return Arrays.copyOf(deletionConfidence, deletionConfidence.length);
    }

    /**
     * @param deletionConfidence the deletionConfidence to set
     */
    public SCFChromatogramBuilder deletionConfidence(byte[] deletionConfidence) {
        this.deletionConfidence = Arrays.copyOf(deletionConfidence, deletionConfidence.length);
        return this;
    }

    /**
     * @return the privateData
     */
    public byte[] privateData() {
        return privateData==null? null:Arrays.copyOf(privateData, privateData.length);
    }

    /**
     * @param privateData the privateData to set
     */
    public SCFChromatogramBuilder privateData(byte[] privateData) {
        this.privateData = privateData==null? null:Arrays.copyOf(privateData, privateData.length);
        return this;
    }

    public SCFChromatogram getChromatogram() {
        Chromatogram basicChromo = super.build();
        return new SCFChromatogramImpl(basicChromo,
                createOptionalConfidence(substitutionConfidence()),
                createOptionalConfidence(insertionConfidence()),
                createOptionalConfidence(deletionConfidence()),
                createPrivateData());
    }
    private Confidence createOptionalConfidence(byte[] confidence){
        if(confidence ==null){
            return null;
        }
        return new DefaultConfidence(confidence);
    }
    private PrivateData createPrivateData() {
        if(privateData() ==null){
            return null;
        }
        return new PrivateData(privateData());
    }


}
