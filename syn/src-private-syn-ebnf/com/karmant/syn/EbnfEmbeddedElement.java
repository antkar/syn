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
 * Embedded EBNF element. Embedded means that this element's attributes may belong to the outer element's
 * scope. Example:
 * 
 * <pre>
 * X : foo=Foo (bar=Bar)? ;</pre>
 * 
 * Here the <code>bar</code> attribute is defined inside an optional element, but that element does not
 * produce an independent object. Instead, both <code>foo</code> and <code>bar</code> attributes will be
 * included into the object returned by the <code>X</code> nonterminal.
 */
abstract class EbnfEmbeddedElement extends EbnfCompoundElement {

    EbnfEmbeddedElement(String key, TextPos keyPos, EbnfProductions body) {
        super(key, keyPos, body);
    }

    @Override
    boolean hasEmbeddedObject() {
        if (getAttribute() == null) {
            //If the attribute is specified, the element must return an independent value.
            //An element may have an embedded object only if the attribute name is not specified.
            EbnfProductions body = getBody();
            for (EbnfProduction production : body.asList()) {
                if (production.hasEmbeddedObject()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    EbnfProductions getEmbeddedProductions() {
        EbnfProductions body = getBody();
        return body;
    }
}
