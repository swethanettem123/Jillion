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
package org.jcvi.jillion.core.util.iter;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jcvi.jillion.internal.core.io.StreamUtil;

/**
 * {@code StreamingIterator} is an
 * {@link Iterator} 
 * that might not have its next element
 * loaded into memory.  {@link StreamingIterator}s
 * are useful when iterating over elements that are 
 * resource intensive so that only as few as the
 * current element need to actually be loaded into memory
 * at one time.  {@link StreamingIterator}s are
 * often used to iterate over records stored in files.
 * Client code must explicitly 
 * {@link #close()} this iterator when done iterating
 * (preferably in a try-finally block) so
 * that any resources used by this iterator
 * can be cleaned up.  Not completely iterating over all the objects in this iterator
 * <strong>and</strong> not calling {@link #close()} could in some implementations 
 * cause memory leaks,
 * deadlocks and/or permanently blocked background threads.
 * Closing a {@link StreamingIterator} before it has
 * finished iterating over all the records
 * will cause {@link #hasNext()}
 * to return {@code false} and {@link #next()}
 * to throw a {@link NoSuchElementException} as if this iterator
 * has finished iterating.
 * <p/>
 * <strong>NOTE:</strong> some implementations
 * might throw unchecked exceptions in
 * {@link #hasNext()} or {@link #next()}
 * if there are problems fetching the next element
 * to be iterated.
 * @author dkatzel
 *
 *
 */
public interface StreamingIterator<T> extends Closeable, Iterator<T>{
	/**
	 * Does this iterator have any elements left
	 * to iterate.
	 * @returns {@code false} if this iterator
	 * has been closed or if there are no more
	 * elements left to iterate.
	 * @throws RuntimeException (unchecked)
	 * if the iterator has not
	 * yet been explicitly closed
	 * and 
	 * there is a problem determining
	 * if there is a next element.
	 */
	@Override
    boolean hasNext();
	
	/**
    * Close this iterator and clean up
    * any open resources. This will
    * force this iterator's {@link #hasNext()}
    * to return {@code false}
    * and {@link #next()} to throw
    * a {@link NoSuchElementException}
    * as if there were no more elements
    * to iterate over.  
    * <p/>
    * If this method is not
    * explicitly called and this iterator
    * still has elements left to iterate over,
    * then some implementations could cause memory leaks,
    * deadlocks and/or permanently blocked threads. 
    */
    @Override
    void close();
    /**
     * Returns the next element in the iterator.
     * @throws NoSuchElementException if
     * this iterator has been closed; or if there are 
     * no more elements to iterate over.
     * @throws RuntimeException (unchecked)
	 * if the iterator has not
	 * yet been explicitly closed
	 * and either
	 * there is a problem determining
	 * if there is a next element
	 * or there is a problem
	 * fetching/creating the next element to return.
     */
    @Override
    T next();
    /**
     * Not supported; will always throw
     * UnsupportedOperationException.
     * @throws UnsupportedOperationException always.
     */
    @Override
    default void remove(){
    	throw new UnsupportedOperationException();
    }
    /**
     * Convert this StreamingIterator into a Java 8 {@link Stream}.
     * The returned Stream
     * must be closed when finished so it is recommended
     * that it is enclosed in a try-with-resource block.
     * <strong>Note:</strong> The returned Stream
     * will iterate over the elements in this
     * Stream so if this method is called,
     * do not call {@link #next()} or {@link #close()}
     * directly
     * or make any assumptions from
     * the returned values of {@link #hasNext()}.
     * @return a new Stream,
     *  will never be null.
     *  
     * @since 5.0
     */
    default Stream<T> toStream(){
    	return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                	this, Spliterator.IMMUTABLE| Spliterator.ORDERED | Spliterator.NONNULL), false)
                .onClose(
                		StreamUtil.newOnCloseRunnableThatThrowsUncheckedIOExceptionIfNecessary(this));
    }
    /**
     * Create and return a new empty streaming iterator.
     * 
     * @return a new StreamingIterator that does not have any elements.
     * @param <T> the type of elements in the stream.
     */
	static <T> StreamingIterator<T> empty(){
		return IteratorUtil.createEmptyStreamingIterator();
	}

}
