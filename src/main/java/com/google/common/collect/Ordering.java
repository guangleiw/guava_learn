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

import java.util.Comparator;

import com.google.common.annotations.GwtCompatible;

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
     * Compator just to pass it in here. Instead , simply subclass Ordering and
     * implement its compare method directly.
     * 
     * @param comparator
     *            the comparator that defines the order
     * @return comparator itself if it is already an Ordering;otherwise an
     *         ordering that wraps that comparator
     */
    @GwtCompatible(serializable=true)
    public static <T> Ordering<T> from(Comparator<T> comparator) {
        return (comparator instanceof Ordering)?(Ordering<T>) comparator: new ComparatorOrdering<T>(comparator);
    }

    public Ordering() {
        // TODO Auto-generated constructor stub
    }

}
