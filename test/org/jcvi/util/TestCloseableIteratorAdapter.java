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

package org.jcvi.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.common.core.util.CloseableIterator;
import org.jcvi.common.core.util.CloseableIteratorAdapter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCloseableIteratorAdapter {

	List<String> stooges = Arrays.asList("larry","moe","curly");
	private CloseableIterator<String> sut;
	@Before
	public void setup(){
		sut = CloseableIteratorAdapter.adapt(stooges.iterator());
		
	}
	@Test
	public void adaptedIteratorShouldIterateCorrectly(){
		assertTrue(sut.hasNext());
		for(int i=0; i< stooges.size(); i++){
			assertEquals(stooges.get(i),sut.next());
		}
		assertFalse(sut.hasNext());
		try{
			sut.next();
			fail("should throw NoSuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			
		}
	}
	
	@Test
	public void closingIteratorShouldMakeIteratorAppearFinished() throws IOException{
		sut.next(); //larry
		sut.next(); //moe
		sut.close(); //close before we get to curly
		assertFalse(sut.hasNext());
		try{
			sut.next();
			fail("should throw NoSuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			assertEquals("iterator has been closed", expected.getMessage());
		}
	}
	@Test
	public void closingMultipleTimesShouldHaveNoEffect() throws IOException{
		sut.close();
		sut.close();
	}
}
