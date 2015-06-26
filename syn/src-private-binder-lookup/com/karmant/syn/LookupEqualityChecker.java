/*
 * Copyright 2013 Anton Karmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.karmant.syn;

/**
 * Equality checker. Used by the {@link SynLookup} functionality to compare values.
 */
interface LookupEqualityChecker {
    /**
     * Returns <code>true</code> if the passed objects are equal. The returned value is used
     * as the result of the lookup expression equality operator <code>==</code>. 
     */
    boolean equal(Object a, Object b);
}
