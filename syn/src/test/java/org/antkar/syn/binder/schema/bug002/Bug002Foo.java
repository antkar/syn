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
package org.antkar.syn.binder.schema.bug002;

import org.antkar.syn.binder.SynField;

public final class Bug002Foo {
    @SynField
    private String name;

    @SynField
    private int cardinality;

    public Bug002Foo() {
        super();
    }

    public String getName() {
        return name;
    }

    public int getCardinality() {
        return cardinality;
    }
}
