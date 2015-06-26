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
package org.antkar.syn;

import org.antkar.syn.SynException;

/**
 * An interface used to process each particular value type in a specific way. Similar to the Visitor pattern.
 *
 * @param <T> the type of the value returned by processing methods.
 */
interface ValueTypeProcessor<T> {

    T processBooleanValue() throws SynException;

    T processIntegerValue() throws SynException;

    T processFloatValue() throws SynException;

    T processStringValue() throws SynException;

    T processObjectValue() throws SynException;

}
