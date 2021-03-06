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
package org.antkar.syn.internal.binder.lookup;

import org.antkar.syn.internal.binder.BoundObject;

/**
 * Environment for a lookup expression evaluation.
 */
final class LookupEnv {
    private final BoundObject vThis;
    private final BoundObject vObj;

    LookupEnv(BoundObject vThis, BoundObject vObj) {
        this.vThis = vThis;
        this.vObj = vObj;
    }

    /**
     * Returns the object associated with "this" special name.
     */
    BoundObject getVThis() {
        return vThis;
    }

    /**
     * Returns the object associated with "obj" special name.
     */
    BoundObject getVObj() {
        return vObj;
    }
}
