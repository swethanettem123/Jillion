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

package org.jcvi.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public class ReverseComplimentFasta {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("fasta", "path to fasta file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output reverse complemented file")
        .longName("out")
        .isRequired(true)
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File fastaFile = new File(commandLine.getOptionValue("fasta"));
            File outputFile = new File(commandLine.getOptionValue("o"));
            final NucleotideSequenceFastaRecordWriter writer = new NucleotideSequenceFastaRecordWriterBuilder(outputFile).build();
            FastaFileVisitor visitor = new AbstractFastaVisitor() {
				
				@Override
				protected boolean visitRecord(String id, String comment, String entireBody) {
                	NucleotideSequence revComplement = new NucleotideSequenceBuilder(entireBody)
                    									.reverseComplement()
                    									.build();
                	try {
						writer.write(id,revComplement,comment);
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
                    return true;
                }
            };
            FastaFileParser.parse(fastaFile, visitor);
            IOUtil.closeAndIgnoreErrors(writer);
            
        } catch (ParseException e) {
            e.printStackTrace();
            printHelp(options);
            System.exit(1);
        }
        
        
        

    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "reverseComplement -fasta <input fasta> -o <output fasta>", 
                
                "read in a fasta file and output a new fasta file which contains " +
                "all the same records, except they are reverse complemented",
                options,
                "Created by Danny Katzel");
        
    }

}
