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

/**
 * 一个可以包含另一个对象非空有引用的对象。该类的实例要么包含非空引用要么什么都没有（Absent），绝不会包含null。
 * 
 * 一个non-null的引用可被用来替换null引用。
 * 
 * 常有以下用途：
 * 
 * 作为方法的返回类型，作为return null的替代来代指返回的对象为absent
 * 来区分未知 和 没有映射到（not present in a map or present in the map with value absent）
 * 用来封装不支持null的集合
 * 
 *
 * @author wangguanglei
 *
 */


public class Optional {

}
