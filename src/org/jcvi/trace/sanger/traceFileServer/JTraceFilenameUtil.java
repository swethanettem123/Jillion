/*
 * Created on Aug 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * {@code JTraceFilenameUtil} is a Utility class that 
 * can parse out JTrace information from a file name 
 * generated by JTrace 2.0.
 * @author dkatzel
 *
 *
 */
public final class JTraceFilenameUtil {
    /**
     * {@code SourceLIMS} is an enum
     * that states where a given Trace
     * was sequenced.
     * @author dkatzel
     *
     *
     */
    public enum SourceLIMS{
        /**
         * Trace was sequenced at the JTC.
         */
        JTC,
        /**
         * Trace was sequenced at TIGR.
         */
        TIGR,
        /**
         * Trace was sequenced externally and loaded into JTrace.
         */
        EXT
    }
    /**
     * Regular expression pattern conforming to the JTrace 2.0 format
     * used to parse out all information from a trace file name.
     */
    private static final Pattern JTRACE_2_PATTERN = Pattern.compile(
            "^(\\S+)_(\\S+)_(\\S+)_(\\S+)_(\\d+)_(\\d+)_(\\d{3})_(\\d+)\\.(.+)$");
    /**
     * private constructor.
     */
    private JTraceFilenameUtil(){}
    
    private static Matcher getMatcherFor(String jtraceFileName){
        Matcher matcher = JTRACE_2_PATTERN.matcher(jtraceFileName);
        if(matcher.matches()){
            return matcher;
        }
        throw new IllegalArgumentException(jtraceFileName + " is not a JTrace filename");  
    }
    /**
     * Gets the JTrace Plate Identifier. Which will be one of the following
     * values depending on the source of the trace:
     * <ul>
     *  <li>Trace file's parent sequencing plate barcode
     *      if plate generated/processed using barcode 
     *      based LIMS system tracking; this encompasses all 
     *      JLIMS plates and a majority to all of Tracker 3730 plates.
     *  </li>
     *  <li>
     *  The set of run ids (separated by hyphens) 
     *  specified to generate this trace file's 
     *  parent plate sheet file; this encompasses all 
     *  Tracker 3100/3130 plates and a small portion 
     *  of Tracker 3730 plates.
     *  </li>
     *  <li>UNKNOWN for externally tracked chromatograms
     *  (i.e. FLIM insertions).</li>
     * </ul>
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return a the Plate ID of this trace file.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static String getPlateIDFrom(String jtraceFileName){
        Matcher matcher = getMatcherFor(jtraceFileName);
        return matcher.group(1);
    }
    /**
     * Gets the sequencing plate well identifier.
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return A01-P24 for 384 well plates, A01-H12 for 96 well plates.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static String getPlateWellFrom(String jtraceFileName){
        return getMatcherFor(jtraceFileName).group(2);
    }
    
    /**
     * Get the {@link SourceLIMS} from the given trace file name.
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the SourceLIMS where this trace was sequenced.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static SourceLIMS getSourceLIMSFrom(String jtraceFileName){
        return SourceLIMS.valueOf(getMatcherFor(jtraceFileName).group(3));
    }
    /**
     * Get the LIMS Parent Identifier. Depending on the source of the trace,
     *  the LIMS Parent ID will be one of the following:
     * <ul>
     * <li>
     *  JLIMS based trace files : Parent library (for random sequencing data) or
     *  parent study (for resequencing data).
     * </li>
     * <li>
     * TIGR based trace files: TIGR Sequence name.
     * </li>
     * <li>External trace file: Trace file identifier 
     * provided during cataloging.
     * </li>
     * </ul>
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the LIMS Parent Identifier.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static String getLIMSParentIDFrom(String jtraceFileName){
        return getMatcherFor(jtraceFileName).group(4);
    }
    /**
     * Get the Run Identifier. Depending on the source of the trace,
     *  the Run ID will be one of the following:
     * <ul>
     * <li>
     *  JLIMS based trace files : JLIMS Run Id.
     * </li>
     * <li>
     * TIGR based trace files: Tracker Gel ID.
     * </li>
     * <li>External trace file: {@code 0}.
     * </li>
     * </ul>
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the Run ID as a long.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static long getRunIDFrom(String jtraceFileName){
        return Long.parseLong(getMatcherFor(jtraceFileName).group(5));
    }
    /**
     * Get the Trace Identifier.
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the Trace ID as a long or {@code 0} for an External trace.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static long getTraceIDFrom(String jtraceFileName){
        return Long.parseLong(getMatcherFor(jtraceFileName).group(6));
    }
    /**
     * Get the sequencer assigned unique capillary id which 
     * produced the trace file.
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the capillary ID as a short or {@code 0} for an External trace.
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static short getCapillaryIDFrom(String jtraceFileName){
        return Short.parseShort(getMatcherFor(jtraceFileName).group(7));
    }
    /**
     * Get the unique Trace File Identifier.
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the Trace File ID as a long. 
     * @throws IllegalArgumentException if the given file name
     * is not a valid JTrace File name.
     * @throws NullPointerException if the given file is null.
     */
    public static long getTraceFileIDFrom(String jtraceFileName){
        return Long.parseLong(getMatcherFor(jtraceFileName).group(8));
    }
    /**
     * Get the format the file is encoded in (ex: ztr).
     * @param jtraceFileName (not null) the a JTrace generated file name.
     * @return the format as a String.
     */
    public static String getFileFormatFrom(String jtraceFileName){
        return getMatcherFor(jtraceFileName).group(9);
    }
}
