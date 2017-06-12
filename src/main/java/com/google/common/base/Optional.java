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

}
