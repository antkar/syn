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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of EBNF productions.
 */
class EbnfProductions {
    private final List<EbnfProduction> productions;

    EbnfProductions(List<EbnfProduction> productions) {
        assert productions != null;
        this.productions = Collections.unmodifiableList(new ArrayList<>(productions));
    }
    
    /**
     * Returns the list of productions.
     */
    List<EbnfProduction> asList() {
        return productions;
    }
    
    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        
        String sep = "";
        for (EbnfProduction production : productions) {
            bld.append(sep);
            bld.append(production);
            sep = " | ";
        }
        
        String result = bld.toString();
        return result;
    }
}
