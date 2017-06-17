/*
 * Copyright (C) 2017 The Guava_galen Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * 一个可以包含另一个对象非空有引用的对象。该类的实例要么包含非空引用要么什么都没有（Absent），绝不会包含null。
 * 
 * 一个non-null的引用可被用来替换null引用。
 * 
 * 常有以下用途：
 * 
 * 作为方法的返回类型，作为return null的替代来代指返回的对象为absent 来区分未知 和 没有映射到（not present in a map
 * or present in the map with value absent） 用来封装不支持null的集合
 * 
 * 该类并不兼容java 8的 java.util.Optional
 * 
 * 该类是可序列化的(java.util.Optional并不能)
 * 
 * @author wangguanglei
 *
 */

@GwtCompatible(serializable = true)
public abstract class Optional<T> implements Serializable {

    /**
     * Returns an Optional instance with no contained reference.
     * 
     * This method is equivalent to Java 8's Optional.empty.
     * 
     * @return
     */
    public static <T> Optional<T> absent() {
        return Absent.withType();
    }

    /**
     * Returns an Optional instance containing the given non-null reference. To
     * have null treated as absent use fromNullable instead.
     * 
     * Compared to java.util.Optional: no differences.
     * 
     * @throws nullPointerException
     *             if reference is null;
     * @param reference
     * @return
     */
    public static <T> Optional<T> of(T reference) {
        return new Present<T>(checkNotNull(reference));
    }

    /**
     * If nullableReference is non-null , returns an Optional instance
     * containing that reference; otherwise returns Optional.absent
     * 
     * Comparison to java.util.Optional: this method is equivalent to Java 8's
     * Optional.ofNullable
     * 
     * @param nullableReference
     * @return
     */
    public static <T> Optional<T> fromNullable(@Nullable T nullableReference) {
        return (nullableReference == null) ? Optional.<T> absent() : new Present<T>(nullableReference);
    }

    /**
     * Returns the equivalent value to the given java.util.Optional , or null if
     * the argument is null
     * 
     * @param javaUtilOptional
     * @return
     */
    @Nullable
    public static <T> Optional<T> fromJavaUtil(@Nullable java.util.Optional<T> javaUtilOptional) {
        return (javaUtilOptional == null) ? null : fromNullable(javaUtilOptional.orElse(null));
    }

    /*
     * Return the equivalent java.util.Optional value to the given
     * com.google.common.base.Optional , or null if the argument is null
     * 
     * 
     */
    public static <T> java.util.Optional<T> toJavaUtil(@Nullable Optional<T> googleOptional) {
        return googleOptional == null ? null : googleOptional.toJavaUtil();
    }

    Optional() {
    }

    /**
     * Returns true if this holder contains a non-null instance Comparison to
     * java.util.Optional: no differences.
     * 
     * @return
     */
    public abstract boolean isPresent();

    /**
     * Returns the contained instance, which must be present If the instance
     * might be absent, use or / orNull instead.
     * 
     * Comparison to java.util.Optional: when the value is absent, this method
     * throws IllegalStateException, whereas the Java 8 counterpart throws
     * java.util.NoSuchElementException.
     * 
     * @return
     */
    public abstract T get();

    /**
     * Returns the contained instance if it is present;defaultValue otherwise;
     * If no default value should be required because the instance is known to
     * be present,use get() instead. For a default value of null , use orNull.
     * 
     * Comparison to java.util.Optional: this method has no equivalent in Java
     * 8's Optional class;write thisOptional.isPresent()?thisOptional:
     * secondChoice instead.
     * 
     * @param defaultValue
     * @return
     */
    public abstract T or(T defaultValue);

    /**
     * Returns this Optional if it has a value present;secondChoice otherwise.
     * 
     * @param secondChoice
     * @return
     */
    public abstract Optional<T> or(Optional<? extends T> secondChoice);

    /**
     * 
     * Returns this Optional if it has a value present;secondChoice otherwise.
     * 
     */
    @Beta
    public abstract Optional<T> or(Supplier<? extends T> supplier);

    /**
     * Returns the contained instance if it is present; null otherwise.If the
     * instance is known to be present, use get() instead. Comparison to
     * java.util.Optional: this method is equivalent to Java 8's
     * Optional.orElse(null).
     * 
     * @return
     */
    @Nullable
    public abstract T orNull();

    /**
     * Returns an immutable singleton Set whose only element is the contained
     * instance if it is present; an empty immutable Set otherwise.
     * 
     * Comparison to java.util.Optional: this method has no equivalent in Java
     * 8's Optional class. However , this common usage:
     * 
     * for(Foo foo: possibleFoo.asSet()){ doSomethingWith(foo); }
     * 
     * @return
     */
    public abstract Set<T> asSet();

    /**
     * If the instance is present , it is transformed with the given Function
     * ;otherwise, Optional#absent is returned.
     * 
     * @throws NullPointerException
     * 
     * @param function
     * @return
     */
    public abstract <V> Optional<V> transform(Function<? super T, V> function);

    /**
     * Returns the equivalent java.util.Optional value to this optional.
     * 
     * Unfortunately , the method reference Optional::toJavaUtil will not work,
     * because it could refer to either the static or instance version of this
     * method. Write out the lambda expression o->o.toJavaUtil() instead.
     * 
     * @return
     */
    public java.util.Optional<T> toJavaUtil() {
        return java.util.Optional.ofNullable(orNull());
    }

    /**
     * Returns true if object is an optional instance , and either the contained
     * references are equal to each other or both are absent. Note that Optional
     * instances of differing parameterized types can be equal.
     * 
     * Comparison to java.util.Optional: no differences.
     * 
     */
    @Override
    public abstract boolean equals(@Nullable Object object);

    /**
     * Returns a hash code for this instance.
     * 
     * Comparison to java.util.Optional: this class leaves the specific choice
     * of hash code unspecified, unlike the Java 8 equivalent.
     */
    @Override
    public abstract int hashCode();

    /**
     * Returns a string representation for this instance.
     * 
     * Comparison to java.util.Optional: this class leaves the specific string
     * representation unspecified , unlike the Java 8 equivalent.
     */
    @Override
    public abstract String toString();

    /**
     * Returns the value of each present instace from the supplied optionals,in
     * order, skipping over occurrences of Optionalabsent. Iterators are
     * unmodifieable and are evaluated lazily
     * 
     * Comparison to java.util.Optional: this method has no equivalent in Java
     * 8's Optional class; use optionals.stream().filer() instead.
     * 
     * @param optionals
     * @return
     */
    @Beta
    public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> optionals) {
        checkNotNull(optionals);
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {
                    private final Iterator<? extends Optional<? extends T>> iterator = checkNotNull(
                            optionals.iterator());

                    @Override
                    protected T computeNext() {
                        while (iterator.hashNext()) {
                            Optional<? extends T> optional = iterator.next();
                            if (optional.isPresent()) {
                                return optional.get();
                            }
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    private static final long serialVersionUID = 0;
}
