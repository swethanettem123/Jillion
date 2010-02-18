/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
/**
 * {@code AbstractFOQueue} is an abstract 
 * implementation of Queue that performs
 * "First Out" operations.
 * @author dkatzel
 */
public abstract class AbstractFOQueue<E> implements Queue<E>{
    /**
     * A Deque used internally
     * for storing the elements.  All operations
     * work off of this.
     */
    private final Deque<E> wrappedDeque = new ArrayDeque<E>();
    /**
     * Constructs an empty queue.
     */
    public AbstractFOQueue(){
        super();
    }
   
    /**
     * Adds E to the given deque, or throw
     * an exception if e can not be added due to capacity problems.
     * @param e the element to add.
     * @param deque the {@link Deque} to add the element to.
     * @return {@code true}.
     * @throws IllegalStateException if the element cannot be added at this 
     * time due to capacity restrictions.
     * @throws ClassCastException if the class of the specified 
     * element prevents it from being added to this queue.
     * @throws NullPointerException if the specified element is null 
     * and this queue does not permit null elements.
     * @throws IllegalArgumentException if some property of this 
     * element prevents it from being added to this queue.
     * 
     */
    protected abstract boolean add(E e, Deque<E> deque);
    /**
     * Try to offer the given element to the given Deque.
     * @param e the element to add.
     * @param deque the Deque to offer the element to.
     * @return {@code true} if the element has been added; {@code false} otherwise  .
     * @throws ClassCastException if the class of the specified 
     * element prevents it from being added to this queue.
     * @throws NullPointerException if the specified element is null 
     * and this queue does not permit null elements.
     * @throws IllegalArgumentException if some property of this 
     * element prevents it from being added to this queue.
     * 
     */
    protected abstract boolean offer(E e, Deque<E> deque);

    @Override
    public E element() {
        return wrappedDeque.element();
    }

   

    @Override
    public E peek() {
        return wrappedDeque.peek();
    }

    @Override
    public E poll() {
        return wrappedDeque.poll();
    }

    @Override
    public E remove() {
        return wrappedDeque.remove();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for(E element : c){
            if(this.add(element)){
                modified =true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        wrappedDeque.clear();
        
    }

    @Override
    public boolean contains(Object o) {
        return wrappedDeque.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrappedDeque.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return wrappedDeque.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return wrappedDeque.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return wrappedDeque.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return wrappedDeque.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return wrappedDeque.retainAll(c);
    }

    @Override
    public int size() {
        return wrappedDeque.size();
    }

    @Override
    public Object[] toArray() {
        return wrappedDeque.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrappedDeque.toArray(a);
    }
    @Override
    public boolean add(E e) {
        return add(e, wrappedDeque);
    }
    @Override
    public boolean offer(E e) {
        return offer(e, wrappedDeque);
    }
    
    
}
