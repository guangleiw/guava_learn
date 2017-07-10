/*
 * Copyright (C) 2007 The guava_lear Authors galen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

import javax.annotation.Nullable;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;

/**
 * Elements of a multiset that are equal to one another are referred to as
 * occurrences of the same single element.The total number of occurrences of an
 * element in a multiset is called the count of that element. Since the count of
 * an element is represented as an int , a mutiset may never contain more than
 * Integer.MAX_VALUE occurrences of any one element.
 * 
 * Mutiset refines the specification of several methods from Collection.It also
 * defines an additional query operation, count, which returns the count of an
 * element. There are five new bulk-modification operations, for example add.
 * These modification operations are optional, but implementations which support
 * the standard collection operations add or remove.Finally , two collection
 * views are provided: elementSet and entrySet is similar but contains
 * Multiset.Entry instances, each providing both a distinct element and the
 * count of that element.
 * 
 * CREATION METHODS: static create() : returning an empty multiset; static
 * create(Iterable<? extends E>): returning a mutiset containing the given
 * initial elements.
 * 
 * EQUAL: using equals to determine whether two instances should be considered
 * "the same"
 * 
 * If your values may be zero, negative , or outside the range of an int, you
 * may wish to use com.google.common.util.concurrent.AtomicLongMap instead.
 * 
 * @author wangguanglei1
 *
 * @param <E>
 */
@GwtCompatible
public interface Multiset<E> extends Collection<E> {

    /**
     * Returns the total number of all occurrences of all elements in this
     * multiset.
     */
    int size();

    /**
     * Returns the number of occurrences of an element in this multiset(the
     * count of the element);
     * 
     * @param element
     *            the element to count occurrences of
     * @return
     */
    int count(@Nullable @CompatibleWith("E") Object element);

    /**
     * Adds a number of occurrences of an element to this multiset.
     * 
     * @param element
     * @param occurrences
     * @throws IllegalArgumentException
     *             if occurrences is negative, or if this operation would result
     *             in more than Integer.MAX_VALUE
     * @throws NullPointerException
     *             if element is null and this implementation does not permit
     *             null elements.
     */
    @CanIgnoreReturnValue
    int add(@Nullable E element, int occurrences);

    /**
     * Removes a number of occurrences of the specified element from this
     * multiset. If the multiset contains fewer than this number of occurrences
     * to begin with, all occurrences will be removed.
     * 
     * @param element
     * @param occurrences
     * @return
     */
    @CanIgnoreReturnValue
    int remove(@Nullable @CompatibleWith("E") Object element, int occurrences);

    /**
     * Adds or removes the necessary occurrences of an element such that the
     * element attains the desired count.
     * 
     * @param element
     * @param count
     * @throws IllegalArgumentException
     *             if count is negative
     * @throws NullPointerException
     *             if element is null and this implementation does not permit
     *             null elements. Note that if count is zero, the implementor
     *             may optionally return zero instead.
     */
    @CanIgnoreReturnValue
    int setCount(E element, int count);

    /**
     * Conditionally sets the count of an element to a new value, as described
     * in setCount(Object , int), provided that the element has the expected
     * current count. If the current count is not oldCount , no change is made.
     * 
     * @param element
     * @param oldCount
     * @param newCount
     * @return true if the condition for modification was met. This implies that
     *         the mutiset was indeed modified, unless oldCount == newCount.
     * @throws IllegalArgumentException
     *             if oldCount or newCount is negative.
     * @throws NullPointerException
     *             if element is null and the implementation does not permit
     *             null elements.
     * 
     */
    @CanIgnoreReturnValue
    boolean setCount(E element, int oldCount, int newCount);

    // views
    /**
     * Returns the set of distinct elements contained in this multiset. The
     * element set is backed by the same data as the multiset, so any change to
     * either is immediately reflected in the other. The other of the elements
     * in the element set is unspecified.
     * 
     * @return
     */
    Set<E> elementSet();

    /**
     * Returns a view of the contents of this multiset, grouped into
     * Mutiset.Entry instances, each providing an element of the multiset and
     * the count of that element. This set contains exactly on entry for each
     * distinct element in the multiset(thus it always has the same size as the
     * elementSet). The order of the elements in the element set is unspecified.
     * 
     * @return
     */
    Set<Entry<E>> entrySet();

    /**
     * An unmodifiable element-count pair for multiset. The entrySet method
     * returns a view of the mutiset whose elements are of this class. A
     * multiset implementation may return Entry instances that are either live
     * "read-through" views to Multiset,or immutable snapshots. Note that this
     * type is unrelated to the similarly-named type Map.Entry.
     * 
     * @author wangguanglei1
     *
     * @param <E>
     */
    interface Entry<E> {

        E getElement();

        int getCount();

        @Override
        boolean equals(Object o);

        @Override
        int hashCode();

        @Override
        String toString();
    }
    
//    default void forEachEntry(ObjectConsumer<? super E> action){
//        checkNotNull(action);
//        entrySet().forEach(entry->action.accept(entry.getElement(),entry.getCount()));
//    }
    
    // Comparison and hashing
    
    boolean equals(@Nullable Object object);
    
    int hashCode();
    
    @Override
    String toString();
    
    // Refined Collection Methods
    
    /**
     * 
     */
    @Override
    Iterator<E> iterator();
    
    @Override
    boolean contains(@Nullable Object element);
    
    @Override
    boolean conatinsAll(Collection<?> elements);
    
    @CanIgnoreReturnValue
    @Override
    boolean add(E element);
    
    
    @CanIgnoreReturnValue
    @Override
    boolean remove(@Nullable Object element);
    
    @CanIgnoreReturnValue
    @Override
    boolean removeAll(Collection<?> c);
    
    @CanIgnoreReturnValue 
    @Override
    boolean retainAll(Collection<?> c);
    
    
//    @Override
//    default void forEach(Consumer<? super E> action) {
//      checkNotNull(action);
//      entrySet()
//          .forEach(
//              entry -> {
//                E elem = entry.getElement();
//                int count = entry.getCount();
//                for (int i = 0; i < count; i++) {
//                  action.accept(elem);
//                }
//              });
//    }

    @Override
    default Spliterator<E> spliterator() {
      return Multisets.spliteratorImpl(this);
    }
    

}
