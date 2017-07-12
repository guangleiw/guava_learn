/*
 * Copyright (C) 2007 The Guava Authors
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

import java.util.stream.Collector;

import com.google.common.annotations.GwtCompatible;

/**
 * Provides static utility methods for creating and working with
 * {@link Multiset} instances.
 *
 * <p>
 * See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#multisets">
 * {@code Multisets}</a>.
 * 
 * @since 2.0
 */
@GwtCompatible
public final class Multisets {

    private Multisets() {
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a multiset
     * created via the specified {@code Supplier}, whose elements are the result
     * of applying {@code elementFunction} to the inputs, with counts equal to
     * the result of applying {@code countFunction} to the inputs. Elements are
     * added in encounter order.
     *
     * <p>
     * If the mapped elements contain duplicates (according to
     * {@link Object#equals}), the element will be added more than once, with
     * the count summed over all appearances of the element.
     *
     * <p>
     * Note that {@code stream.collect(toMultiset(function, e -> 1, supplier))}
     * is equivalent to
     * {@code stream.map(function).collect(Collectors.toCollection(supplier))}.
     *
     * @since 22.0
     */
    public static <T, E, M extends Multiset<E>> Collector<T, ?, M> toMultiset(
            java.util.function.Function<? super T, E> elementFunction,
            java.util.function.ToIntFunction<? super T> countFunction,
            java.util.function.Supplier<M> multisetSupplier) {
        checkNotNull(elementFunction);
        checkNotNull(countFunction);
        checkNotNull(multisetSupplier);
        return Collector.of(multisetSupplier, (ms, t) -> ms.add(elementFunction.apply(t), countFunction.applyAsInt(t)),
                (ms1, ms2) -> {
                    ms1.addAll(ms2);
                    return ms1;
                });
    }
    
    

}
