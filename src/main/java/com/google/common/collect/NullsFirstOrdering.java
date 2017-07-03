
/*
 * Copyright (C) 2007 The guava_learn Authors galen
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

import java.io.Serializable;

import com.google.common.annotations.GwtCompatible;

/** An ordering that treats null as less than all other values**/
@GwtCompatible(serializable=true)
final class NullsFirstOrdering<T> extends Ordering<T> implements Serializable {
	final Ordering<? super T> ordering;
	
	NullsFistOrdering(Ordering<? super T> ordering){
		this.ordering = ordering;
	}
}
