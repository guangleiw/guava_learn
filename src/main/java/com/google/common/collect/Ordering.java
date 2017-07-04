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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
     * Returns and ordering that compares objects according to the order in
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
    public static <T> Ordering<T> explicit(List<T> valusInOrder) {
        return new ExplicitOrdering<T>(valuesInOrder);
    }

    public static <T> Ordering<T> explicit(T leastValue, T... remainingValuesInOrder) {
        return explicit(Lists.asList(leastValue, remainingValuesInOrder));
    }

    // Ordering<Object> singletons

    /**
     * Returns an ordering which treats all values as equal,indicating
     * "no ordering." Passing this ordering to any stable sort algorithm results
     * in no change to the order of elements.Note especially thata sortedCopy
     * and immutableSortedCopy are stable, and in the returned instance these
     * are implemented by simply copying the source list.
     * 
     * Example:
     * Ordering.allEqual().nullsLast().sortedCopy(asList(t,null,e,s,null,t,null)
     * );
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
     * function to them then comparing those results using this.
     * For example, to compare objects by their string forms, in a case-insensitive manner, use:
     * 
     *  Ordering.from(String.CASE_INSENSITIVE_ORDER).onResultOf(Functions.toStringFunction())
     * @param function
     * @return
     */
    @GwtCompatible
    public <F> Ordering<F> onResultOf(Function<F,? extends T> function){
        return new ByFunctionOrdering<F,T>(function,this);
    }
    
    <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys(){
        return onResultOf(Maps.<T2>keyFunction());
    }
    
    

}
