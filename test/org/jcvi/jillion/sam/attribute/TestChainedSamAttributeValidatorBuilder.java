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
package org.jcvi.jillion.sam.attribute;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jcvi.jillion.sam.SamAttributed;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SamHeader.class, SamAttribute.class})
public class TestChainedSamAttributeValidatorBuilder {

	private SamAttributed IGNORE = new SamAttributed() {
		
		@Override
		public boolean hasAttribute(ReservedSamAttributeKeys key) {
			return false;
		}
		
		@Override
		public boolean hasAttribute(SamAttributeKey key) {
			return false;
		}
		
		@Override
		public SamAttribute getAttribute(ReservedSamAttributeKeys key) {
			return null;
		}
		
		@Override
		public SamAttribute getAttribute(SamAttributeKey key) {
			return null;
		}
	};
	@Test(expected = NullPointerException.class)
	public void nullValidatorShouldThrowNPE(){
		new ChainedSamAttributeValidatorBuilder()
				.addValidator(null);
	}
	
	@Test
	public void defaultConstructorShouldFirstUseReservedAttributeValidator(){
		ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator validator = (ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator)
																						new ChainedSamAttributeValidatorBuilder().build();
		
		@SuppressWarnings("unchecked")
		List<SamAttributeValidator> validators =Whitebox.getInternalState(validator, List.class);
		assertEquals(1, validators.size());
		assertTrue(validators.get(0) == ReservedAttributeValidator.INSTANCE);
	}
	
	@Test
	public void createEmptyValidator(){
		ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator validator = (ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator)
				new ChainedSamAttributeValidatorBuilder(false).build();
		
		@SuppressWarnings("unchecked")
		List<SamAttributeValidator> validators =Whitebox.getInternalState(validator, List.class);
		assertTrue(validators.isEmpty());
	}
	
	@Test
	public void firstValidatorFailsShouldNotCall2ndValidator() throws InvalidAttributeException{
		SamAttributeValidator v1 = createMock(SamAttributeValidator.class);
		SamAttributeValidator v2 = createMock(SamAttributeValidator.class);
		
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		
		v1.validate(isA(SamHeader.class), isA(SamAttributed.class), isA(SamAttribute.class));
		expectLastCall().andThrow(expected);
		
		replay(v1, v2);
		
		SamAttributeValidator chain = new ChainedSamAttributeValidatorBuilder(false)
											.addValidator(v1)
											.addValidator(v2)
											.build();
		
		SamHeader mockHeader = PowerMock.createMock(SamHeader.class);
		SamAttribute mockAttr = PowerMock.createMock(SamAttribute.class);
		
		PowerMock.replay(mockHeader, mockAttr);
		try{
			chain.validate(mockHeader, IGNORE, mockAttr);
			fail("should throw exception");
		}catch(InvalidAttributeException actual){
			assertEquals(expected, actual);
		}
		verify(v1, v2);
		
	}
	@Test
	public void firstValidatorPassesShouldThenCall2ndValidator() throws InvalidAttributeException{
		SamAttributeValidator v1 = createMock(SamAttributeValidator.class);
		SamAttributeValidator v2 = createMock(SamAttributeValidator.class);
		
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		
		v1.validate(isA(SamHeader.class), isA(SamAttributed.class), isA(SamAttribute.class));
		
		v2.validate(isA(SamHeader.class), isA(SamAttributed.class), isA(SamAttribute.class));
		expectLastCall().andThrow(expected);
		
		replay(v1, v2);
		
		SamAttributeValidator chain = new ChainedSamAttributeValidatorBuilder(false)
											.addValidator(v1)
											.addValidator(v2)
											.build();
		
		SamHeader mockHeader = PowerMock.createMock(SamHeader.class);
		SamAttribute mockAttr = PowerMock.createMock(SamAttribute.class);
		
		PowerMock.replay(mockHeader, mockAttr);
		try{
			chain.validate(mockHeader, IGNORE, mockAttr);
			fail("should throw exception");
		}catch(InvalidAttributeException actual){
			assertEquals(expected, actual);
		}
		verify(v1, v2);
		
	}
	
	@Test
	public void wholeChainPasses() throws InvalidAttributeException{
		SamAttributeValidator v1 = createMock(SamAttributeValidator.class);
		SamAttributeValidator v2 = createMock(SamAttributeValidator.class);
		
		
		
		v1.validate(isA(SamHeader.class), isA(SamAttributed.class), isA(SamAttribute.class));
	
		v2.validate(isA(SamHeader.class), isA(SamAttributed.class), isA(SamAttribute.class));		
		
		replay(v1, v2);
		
		SamAttributeValidator chain = new ChainedSamAttributeValidatorBuilder(false)
											.addValidator(v1)
											.addValidator(v2)
											.build();
		
		SamHeader mockHeader = PowerMock.createMock(SamHeader.class);
		SamAttribute mockAttr = PowerMock.createMock(SamAttribute.class);
		
		PowerMock.replay(mockHeader, mockAttr);
		
		chain.validate(mockHeader, IGNORE, mockAttr);
		
		verify(v1, v2);
		
	}
}
