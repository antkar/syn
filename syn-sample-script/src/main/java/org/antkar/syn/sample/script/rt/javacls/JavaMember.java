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
package org.antkar.syn.sample.script.rt.javacls;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Wraps a Java class member.
 */
abstract class JavaMember {
    JavaMember(){}

    /**
     * Returns the value of a static member. If this member is not static, an exception is thrown.
     */
    abstract Value getStaticValue() throws SynsException;

    /**
     * Returns the value of an instance member.
     */
    abstract Value getInstanceValue(Object obj);
}
