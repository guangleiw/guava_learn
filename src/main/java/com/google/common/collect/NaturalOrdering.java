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
import java.util.Comparator;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable=true)
@SuppressWarnings("unchecked")
final class  NaturalOrdering  extends Ordering<Comparable> implements Serializable{

    static final NaturalOrdering INSTANCE = new NaturalOrdering();
    
    private transient Ordering<Comparable> nullsFirst;
    private transient Ordering<Comparable> nullsLast;
    
    
    
    public int compare(Comparable left, Comparable right) {
        // TODO Auto-generated method stub
        return 0;
    }


    public <S extends Comparable> Ordering<S> nullsFirst(){
        Ordering<Comparable> result = nullsFirst;
        if(result == null){
        	result = nullsFirst = super.nullsFirst();
        }
    }
    
    public <S extends Comparable> Ordering<S> nullsLast(){
        
    }
    

    public Comparator<Comparable> reversed() {
        // TODO Auto-generated method stub
        return Ordering.super.reversed();
    }



    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }



    public NaturalOrdering() {
        // TODO Auto-generated constructor stub
    }

}
