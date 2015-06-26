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
package org.antkar.syn.sample.script.rt;

import org.antkar.syn.sample.script.rt.value.Value;

/**
 * An on-demand import using an arbitrary {@link Value} for looking for names.
 */
class ValueOnDemandImport extends OnDemandImport {
    private final Value value;
    
    ValueOnDemandImport(Value value) {
        this.value = value;
    }

    @Override
    Value getValueOpt(String name) throws SynsException {
        return value.getMemberOpt(name);
    }
    
    @Override
    public String toString() {
        return "value:" + value;
    }
}
