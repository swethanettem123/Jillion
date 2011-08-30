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

package org.jcvi.common.io.ansi;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestTextAttributes {

    @Test
    public void correctEscapeCode(){
        assertEquals(new EscapeCode(1),TextAttributes.BOLD.getEscapeCode());
        assertEquals(new EscapeCode(4),TextAttributes.UNDERLINE.getEscapeCode());
        assertEquals(new EscapeCode(5),TextAttributes.BLINK.getEscapeCode());
        assertEquals(new EscapeCode(7),TextAttributes.REVERSE.getEscapeCode());
        assertEquals(new EscapeCode(8),TextAttributes.CONCEAL.getEscapeCode());
    }
}
