package org.jcvi.common.core.assembly.clc.cas.var;

import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code ReferenceVariations} is an object representation
 * of all the {@link Variation}s found by the {@code find_variations}
 * program for a single reference assembly.
 * @author dkatzel
 *
 */
public interface ReferenceVariations {
	/**
	 * Get the id of the reference used by the assembly.
	 * @return a String; never null.
	 */
	String getReferenceId();
	/**
	 * Get a {@link CloseableIterator}
	 * of all the {@link Variation}s found for this
	 * assembly compared to the reference.
	 * @return a new {@link CloseableIterator}; never
	 * null but may contain no elements. if no variations 
	 * were found.
	 */
	CloseableIterator<Variation> getVariationIterator();
}