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

import org.antkar.syn.EbnfGrammar;

/**
 * Result returned by a {@link BinderConfigurator}.
 *
 * @param <T> the type of the start nonterminal.
 */
class BinderConfiguratorResult<T> {

    private final EbnfGrammar genGrammar;
    private final BinderConfiguration<T> config;
    
    BinderConfiguratorResult(EbnfGrammar genGrammar, BinderConfiguration<T> config) {
        this.genGrammar = genGrammar;
        this.config = config;
    }

    EbnfGrammar getGenGrammar() {
        return genGrammar;
    }

    BinderConfiguration<T> getConfig() {
        return config;
    }
}
