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
package org.antkar.syn.sample.script.schema;

import java.util.Arrays;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;

/**
 * Import declaration syntax node.
 */
public class Import {
    /** The chain of names to be imported. */
    @SynField
    private StringToken[] synNames;
    
    /** <code>true</code> if this is an on-demand import. */
    @SynField
    private boolean synOnDemand;

    public Import(){}
    
    /**
     * Adds this import to the specified scope.
     */
    void addToScope(ScriptScope scope) throws SynsException {
        if (synOnDemand) {
            scope.addOnDemandImport(synNames);
        } else {
            scope.addSingleImport(synNames);
        }
    }
    
    @Override
    public String toString() {
        return "import " + Arrays.toString(synNames);
    }
}
