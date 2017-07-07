/*
 * Copyright (C) 2007 The guava_learn Authors Galen
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.CollectPreconditions.checkNonnegative;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;

/**
 * A comparator, with additional methods to support common operations.
 * 
 * Like other fluent types, there are three types of methods present: methods
 * for acquiring/chaining/using. Acquiring The common ways to get an instance of
 * Ordering are: Subclass it and implement instead of implementing Comparator
 * directly. Pass a pre-existing Comparator instance to from(Comparator). Use
 * the natural ordering Ordering(natural).
 * 
 * Chaining Then you can use the chaining methods to get an altered version of
 * that Ordering, including:
 * reverse/compound(Comparator)/onResultOf(Function)/nullsFirst(nullsLast)
 *
 * Using Finally, use the resulting Ordering anywhere a Comparator is required,
 * or use any of its special operations, such as:
 * immutableSortedCopy/isOrdered/isStrictlyOrdered/min/max
 * 
 * Understanding complex ordering Complex chained orderings like the following
 * example can be challenging to understand. Ordering<Foo> ordering =
 * Ordering.natural() .nullsFirst() .onResultOf(getBarFunction) .nullsLast();
 * 
 * @author wangguanglei1
 *
 * @param <T>
 */
public abstract class Ordering<T> implements Comparator<T> {

    /**
     * Constructs a new instance of this class (only invokable by the subclass
     * constructor, typically implicit).
     */
    protected Ordering() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Returns a sserializable ordering that uses the natural of the values. The
     * ordering throws as NullPointerException when passed a null parameter.
     * 
     * 
     * @return
     */
    @GwtCompatible
    @SuppressWarnings("unchecked")
    public static <C extends Comparable> Ordering<C> natural() {
        return (Ordering<C>) NaturalOrdering.INSTANCE;
    }

    // static factories

    /**
     * Returns an ordering based on an existing comparator instance. Note that
     * it is unnecessary to create a new anonymous inner class implementing
     * Comparator just to pass it in here. Instead , simply subclass Ordering
     * and implement its compare method directly.
     * 
     * @param comparator
     *            the comparator that defines the order
     * @return comparator itself if it is already an Ordering;otherwise an
     *         ordering that wraps that comparator
     */
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(Comparator<T> comparator) {
        return (comparator instanceof Ordering) ? (Ordering<T>) comparator : new ComparatorOrdering<T>(comparator);
    }

    /**
     * Simply returns its argument
     * 
     * @deprecated no need to use this
     */
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(Ordering<T> ordering) {
        return checkNotNull(ordering);
    }

    /**
     * Returns an ordering that compares objects according to the order in
     * which they appear in the given list.Only objects present in the list may
     * be compared. This comparator imposes a "partial ordering" over the type
     * T. Subsequent changes to the valuesInOrder list will have no effect on
     * the returned comparator. Null values in the list are not supported.
     * 
     * The generated comparator is serializable if all the provided values are
     * serializable.
     * 
     * @param valusInOrder
     *            the values that the returned comparator will be able to
     *            compare, in the order the comparator should inuce
     * @return the comparator described above
     * @throws NullPointerException
     *             if any of the provided values is null
     * @throws IllegalArgumentException
     *             if valuesInOrder contains any duplicate values (according to
     *             Object equals)
     */
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(List<T> valuesInOrder) {
        return new ExplicitOrdering<T>(valuesInOrder);
    }

    public static <T> Ordering<T> explicit(T leastValue, T... remainingValuesInOrder) {
        return explicit(Lists.asList(leastValue, remainingValuesInOrder));
    }

    // Ordering<Object> singletons

    /**
     * Returns an ordering which treats all values as equal,indicating
     * "no ordering." Passing this ordering to any stable sort algorithm results
     * in no change to the order of elements.Note especially that sortedCopy
     * and immutableSortedCopy are stable, and in the returned instance these
     * are implemented by simply copying the source list.
     * 
     * Example:
     * Ordering.allEqual().nullsLast().sortedCopy(asList(t,null,e,s,null,t,null));
     * 
     * @return
     */
    @GwtCompatible
    public static Ordering<Object> allEqual() {
        return AllEqualOrdering.INSTANCE;
    }

    /**
     * Returns an ordering that compares objects by the natural ordering of
     * their string representations as returned by toString(). It does not
     * support null values.
     * 
     * The comparator is serializable.
     * 
     * @return
     */
    @GwtCompatible
    public static Ordering<Object> usingToString() {
        return UsingToStringOrdering.INSTANCE;
    }

    /**
     * Returns an arbitrary ordering over all objects, for which compare(a,b) ==
     * 0 implies a == b (identity equality). There is no meaning whatsoever to
     * the order imposed, but it is constant for the life of the VM. Because the
     * ordering is identity-based , it is not "consistent with equals(Object)"
     * as defined by comparator. Use caution when building a SortedSet or
     * SortedMap from it, as the resulting collection will not behave exactly
     * according to spec.
     * 
     * This ordering is not serializable, as its implementation relies on
     * identityHashCode(Object), so its behavior cannot be preserved across
     * serialization.
     * 
     * @return
     */
    public static Ordering<Object> arbitrary() {
        return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
    }

    private static class ArbitraryOrderingHolder {
        static final Ordering<Object> ARBITRARY_ORDERING = new ArbitraryOrdering();
    }

    @VisibleForTesting
    static class ArbitraryOrdering extends Ordering<Object> {

        private final AtomicInteger counter = new AtomicInteger(0);
        private final ConcurrentMap<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeMap();

        private Integer getUid(Object obj) {
            Integer uid = uids.get(obj);
            if (uid == null) {
                uid = counter.getAndIncrement();
                Integer alreadySet = uids.putIfAbsent(obj, uid);
                if (alreadySet != null) {
                    uid = alreadySet;
                }
            }
            return uid;
        }

        @Override
        public int compare(Object left, Object right) {
            if (left == right) {
                return 0;
            } else if (left == null) {
                return -1;
            } else if (right == null) {
                return 1;
            }
            int leftCode = identityHashCode(left);
            int rightCode = identityHashCode(right);
            if (leftCode != rightCode) {
                return leftCode < rightCode ? -1 : 1;
            }

            int result = getUid(left).compareTo(getUid(right));
            if (result == 0) {
                throw new AssertionError();
            }
            return result;
        }

        @Override
        public String toString() {
            return "Ordering.arbitrary()";
        }

        int identityHashCode(Object object) {
            return System.identityHashCode(object);
        }
    }

    // Instance-based factories (and any static equivalents)

    /**
     * Returns the reverse of this ordering; the Ordering equivalent to
     * reverseOrder(Comparator).
     * 
     * @return
     */
    // type paramter lets us avoid the extra <String> in statements like:
    // ORdering<String> o = Ordering.<String>natural().reverse();
    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<S>(this);
    }

    /**
     * Returns an ordering that treats {@code null} as less than all other
     * values and uses {@code this} to compare non-null values.
     * 
     * @return
     */
    public <S extends T> Ordering<S> nullsFirst() {
        return new NullsFirstOrdering<S>(this);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsLast() {
        return new NullsLastOrdering<S>(this);
    }

    /**
     * Returns a new ordering on F which orders elements by first applying a
     * function to them then comparing those results using this. For example, to
     * compare objects by their string forms, in a case-insensitive manner, use:
     * 
     * Ordering.from(String.CASE_INSENSITIVE_ORDER).onResultOf(Functions.
     * toStringFunction())
     * 
     * @param function
     * @return
     */
    @GwtCompatible
    public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
        return new ByFunctionOrdering<F, T>(function, this);
    }

    <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
        return onResultOf(Maps.<T2> keyFunction());
    }

    /**
     * Returns an ordering which first uses the ordering this , but which in the
     * event of a "tie", then delegate to secondaryComparator. For example, to
     * sort a bug list first by status and second by priority, you might use
     * byStatus.compound(byPriority).For a compound ordering with three or more
     * components,simply chain multiple calls to this method.
     * 
     * An ordering produced by this method, or a chain of calls to this method,
     * is equivalent to one created using compound(Iterable) on the same
     * component comparators.
     * 
     * @param secondaryComparator
     * @return
     */
    @GwtCompatible(serializable = true)
    public <U extends T> Ordering<U> compound(Comparartor<? super U> secondaryComparator) {
        return new CompoundOrdering<U>(this, checkNotNull(secondaryComparator));
    }

    /**
     * Returns a new ordering which sorts iterables by comparing corresponding
     * elements pairwise until a nonzero result is found; imposes
     * "dictionary order". If the end of one iterable is reached, but not the
     * other, the shorter iterable is considered to be less than the longer one.
     * For example, a lexicographical natural ordering over integers considers
     * {@code [] < [1] < [1, 1] <
     * [1, 2] < [2]}.
     *
     * <p>
     * Note that {@code ordering.lexicographical().reverse()} is not equivalent
     * to {@code
     * ordering.reverse().lexicographical()} (consider how each would order
     * {@code [1]} and {@code [1,
     * 1]}).
     *
     * <p>
     * <b>Java 8 users:</b> Use {@link Comparators#lexicographical(Comparator)}
     * instead.
     *
     * @since 2.0
     */
    @GwtCompatible(serializable = true)
    // type parameter <S> lets us avoid the extra <String> in statements like:
    // Ordering<Iterable<String>> o =
    // Ordering.<String>natural().lexicographical();
    public <S extends T> Ordering<Iterable<S>> lexicographical() {
        /*
         * Note that technically the returned ordering should be capable of
         * handling not just {@code Iterable<S>} instances, but also any {@code
         * Iterable<? extends S>}. However, the need for this comes up so rarely
         * that it doesn't justify making everyone else deal with the very ugly
         * wildcard.
         */
        return new LexicographicalOrdering<S>(this);
    }

    // Regular instance methods

    @Override
    public abstract int compare(@Nullable T left, @Nullable T right);

    /**
     * Returns the least of the specified values according to this ordering. If
     * there are multiple least values, the first of those is returned. The
     * iterator will be left exhausted:its hasNext() method will return false;
     * 
     * @param iterator
     *            the iterator whose minimum element is to be determined.
     * @throws NoSuchElementException
     *             if iterator is empty
     * @throws ClassCastException
     *             if the parameters are not mutually comparable under this
     *             ordering.
     * 
     */
    public <E extends T> E min(Iterator<E> iterator) {
        E minSoFar = iterator.next();

        while (iterator.hasNext()) {
            minSoFar = min(minSoFar, iterator.next());
        }

        return minSoFar;
    }

    /**
     * Returns the least of the specified values according to this ordering. If
     * there are multiple least values, the first of those is returned.
     * 
     * @param iterable
     *            the iterable whose minimum element is to be determined
     * @throws NoSuchElementException
     *             if {@code iterable} is empty
     * @throws ClassCastException
     *             if the parameters are not <i>mutually comparable</i> under
     *             this ordering.
     */
    public <E extends T> E min(Iterable<E> iterable) {
        return min(iterable.iterator());
    }

    /**
     * Returns the lesser of the two values according to this ordering. If the
     * values compare as 0, the first is returned.
     * 
     * Implementation note: this method is invoked by the default
     * implementations of the other min overloads, so overriding it will affect
     * their behavior.
     * 
     * @param a
     *            value to compare , returned if less than or equal to b.
     * @param b
     * @throws ClassCastException
     *             if the parameters are not mutually comparable under this
     *             ordering.
     */
    public <E extends T> E min(@Nullable E a, @Nullable E b) {
        return (compare(a, b) <= 0) ? a : b;
    }

    /**
     * Returns the least of the specified values according to this morning.If
     * there are multiple least values, the first of those is returned.
     * 
     * @param a
     * @param b
     * @param c
     * @param rest
     *            values to compare
     * @throws ClassCastException
     *             if the parameters are not mutually comparable under this
     *             ordering.
     */
    public <E extends T> E min(@Nullable E a, @Nullable E b, @Nullable E c, E... rest) {
        E minSoFar = min(min(a, b), c);
        for (E r : rest) {
            minSoFar = min(minSoFar, r);
        }

        return minSoFar;
    }

    /**
     * Returns the greatest of the specified values according to this ordering.
     * If there are multiple greatest values, the first of those is returned.
     * The iterator will be left exhausted: its hasNext() method will return
     * false.
     * 
     * @param iterator
     *            the iterator whose maximum element is to be determined.
     * @throws NoSuchElementException
     *             if iterator is empty.
     * 
     */
    public <E extends T> E max(Iterator<E> iterator) {
        E maxSoFar = iterator.next();

        while (iterator.hasNext()) {
            maxSoFar = max(maxSoFar, iterator.next());
        }

        return maxSoFar;
    }

    /**
     * Returns the greatest of the specified values according to this ordering.
     * If there are multiple greatest values, the first of those is returned.
     *
     * @param iterable
     *            the iterable whose maximum element is to be determined
     * 
     * @throws NoSuchElementException
     *             if {@code iterable} is empty
     * @throws ClassCastException
     *             if the parameters are not <i>mutually comparable</i> under
     *             this ordering.
     */
    public <E extends T> E max(Iterable<E> iterable) {
        return max(iterable.iterator());
    }

    /**
     * Returns the greater of the two values according to this ordering. If the
     * values compare as 0, the first is returned.
     * 
     * @param a
     *            value to compare, returned if greater than or equal to b.
     * @param b
     *            value to compare.
     * @throws ClassCastException
     *             if the parameters are not <i>mutually comparable</i> under
     *             this ordering.
     */
    public <E extends T> E max(@Nullable E a, @Nullable E b) {
        return (compare(a, b) >= 0) ? a : b;
    }

    /**
     * Returns the greatest of the specified values according to this ordering.
     * If there are multiple greatest values, the first of those is returned.
     * 
     * @param a
     *            value to compare, returned if greater than or equal to the
     *            rest.
     * @param b
     *            value to compare
     * @param c
     *            value to compare
     * @param rest
     *            values to compare
     * @throws ClassCastException
     *             if the parameters are not <i>mutually comparable</i> under
     *             this ordering.
     */
    public <E extends T> E max(@Nullable E a, @Nullable E b, @Nullable E c, E... rest) {
        E maxSoFar = max(max(a, b), c);

        for (E r : rest) {
            maxSoFar = max(maxSoFar, r);
        }

        return maxSoFar;
    }

    public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
        if (iterable instanceof Collection) {
            Collection<E> collection = (Collection<E>) iterable;
            if (collection.size() <= 2L * k) {
                @SuppressWarnings("unchecked")
                E[] array = (E[]) collection.toArray();
                Arrays.sort(array, this);
                if (array.length > k) {
                    array = Arrays.copyOf(array, k);
                }
                return Collections.unmodifiableList(Arrays.asList(array));
            }
        }
        return leastOf(iterable.iterator(), k);
    }

    /**
     * Returns the k least elements from the given iterator according to this
     * ordering, in order from least to greatest. If there are fewer than k
     * elements present,all will be included.
     * 
     * When multiple elements are equivalent , it is undefined which will come
     * first.
     * 
     * @return an immutable RandomAccess list of the k least elements in
     *         ascending order
     * @throws IllegalArgumentException
     *             if k is negative.
     */
    public <E extends T> List<E> leastOf(Iterator<E> iterator, int k) {
        checkNotNull(iterator);
        checkNonnegative(k, "k");

        if (k == 0 || !iterator.hasNext()) {
            return ImmutableList.of();
        } else if (k >= Integer.MAX_VALUE / 2) {
            // k is realy large ; just do a straightforward sorted-copy-and
            // sublist
            ArrayList<E> list = Lists.newArrayList(iterator);
            Collections.sort(list, this);
            if (list.size() > k) {
                list.subList(k, list.size()).clear();
            }
            list.trimToSize();
            return Collections.unmodifiableList(list);
        } else {
            TopKSelector<E> selector = TopKSelector.least(k, this);
            selector.offerAll(iterator);
            return selector.topK();
        }
    }

    /**
     * Returns the {@code k} greatest elements of the given iterable according
     * to this ordering, in order from greatest to least. If there are fewer
     * than {@code k} elements present, all will be included.
     *
     * <p>
     * The implementation does not necessarily use a <i>stable</i> sorting
     * algorithm; when multiple elements are equivalent, it is undefined which
     * will come first.
     *
     * @return an immutable {@code RandomAccess} list of the {@code k} greatest
     *         elements in <i>descending order</i>
     * @throws IllegalArgumentException
     *             if {@code k} is negative
     * @param iterable
     * @param k
     * @return
     */
    public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k) {
        return reverse().leastOf(iterable, k);
    }

    /**
     * Returns the {@code k} greatest elements from the given iterator according
     * to this ordering, in order from greatest to least. If there are fewer
     * than {@code k} elements present, all will be included.
     *
     * <p>
     * The implementation does not necessarily use a <i>stable</i> sorting
     * algorithm; when multiple elements are equivalent, it is undefined which
     * will come first.
     * 
     * @return an immutable {@code RandomAccess} list of the {@code k} greatest
     *         elements in <i>descending order</i>
     * @throws IllegalArgumentException
     *             if {@code k} is negative
     */

    public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k) {
        return reverse().leastOf(iterator, k);
    }

    /**
     * Returns a <b>mutable</b> list containing {@code elements} sorted by this ordering; use this
     * only when the resulting list may need further modification, or may contain {@code null}. The
     * input is not modified. The returned list is serializable and has random access.
     *
     * <p>Unlike {@link Sets#newTreeSet(Iterable)}, this method does not discard elements that are
     * duplicates according to the comparator. The sort performed is <i>stable</i>, meaning that such
     * elements will appear in the returned list in the same order they appeared in {@code elements}.
     *
     * <p><b>Performance note:</b> According to our
     * benchmarking
     * on Open JDK 7, {@link #immutableSortedCopy} generally performs better (in both time and space)
     * than this method, and this method in turn generally performs better than copying the list and
     * calling {@link Collections#sort(List)}.
     */
    // TODO(kevinb): rerun benchmarks including new options
    public <E extends T> List<E> sortedCopy(Iterable<E> elements){
        @SuppressWarnings("unchecked")
        E[] array = (E[])Iterables.toArray(elements);
        Arrays.sort(array,this);
        return Lists.newArrayList(Arrays.asList(array));
    }
    
    
    /**
     * Returns an immutable list containing elements sorted by this ordering.
     * The input is not modified. Unlike Sets/newTreeSet(Iterable), this method
     * does not discard elements that are duplicates accroding to the
     * comparator. The sort preformed is stable , meaning that such elements
     * will appear in the returned list in the same order they appeared in
     * elements
     * 
     * Performance note : According to our benchmarking on open JDK 7 , this
     * method is the most effecient way to make a sorted copy of a collection.
     * 
     * @throws NullPointerException
     *             if any element of elements is null
     * 
     * @param elements
     * @return
     */
    public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> elements) {
        return ImmutableList.sortedCopyOf(this, elements);
    }

    /**
     * Returns true if each element in iterable after the first is greater than
     * or equal to the element that preceded it, according to this ordering.
     * Note that this is always true when the iterable has fewer than two
     * elements.
     * 
     * @param iterable
     * @return
     */
    public boolean isOrdered(Iterable<? extends T> iterable) {
        Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            T prev = it.next();
            while (it.hasNext()) {
                T next = it.next();
                if (compare(prev, next) > 0) {
                    return false;
                }
                prev = next;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if each element in {@code iterable} after the first
     * is <i>strictly</i> greater than the element that preceded it, according
     * to this ordering. Note that this is always true when the iterable has
     * fewer than two elements.
     *
     * <p>
     * <b>Java 8 users:</b> Use the equivalent
     * {@link Comparators#isInStrictOrder(Iterable, Comparator)} instead, since
     * the rest of {@code Ordering} is mostly obsolete (as explained in the
     * class documentation).
     */
    public boolean isStrictlyOrdered(Iterable<? extends T> iterable) {
        Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            T prev = it.next();
            while (it.hasNext()) {
                T next = it.next();
                if (compare(prev, next) >= 0) {
                    return false;
                }
                prev = next;
            }
        }
        return true;
    }

    /**
     * Searches for key using the binary search algorithm. The list must be
     * sorted using this ordering.
     * 
     * @param sortedList
     *            the list to be searched.
     * @param key
     *            the key to be searched for.
     * @return
     */
    public int binarySearch(List<? extends T> sortedList, @Nullable T key) {
        return Collections.binarySearch(sortedList, key, this);
    }

    static class IncomparableValueException extends ClassCastException {
        final Object value;

        IncomparableValueException(Object value) {
            super("Cannot compare  value: " + value);
            this.value = value;
        }

        private static final long serialVersionUID = 0;
    }

    //
    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;
}
