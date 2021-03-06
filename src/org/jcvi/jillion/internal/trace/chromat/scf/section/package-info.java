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
/**
 * An SCF file is divided into several sections; each section
 * contains only certain fields of a Chromatogram.  Each Section
 * is formatted differently and some sections are even
 * formatted differently depending on the specification version.
 * Therefore, Section specific encoders and decoders are needed
 * to handle each Section.
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;
