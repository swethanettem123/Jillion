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
/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.IOException;


class SffDecoderException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 5849079908602188978L;

    /**
     * @param message
     * @param cause
     */
    public SffDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SffDecoderException(String message) {
        super(message);
    }

}
