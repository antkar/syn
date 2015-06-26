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
package com.karmant.syn.sample.script.schema;

import com.karmant.syn.TextPos;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.StatementResult;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.TextSynsException;

/**
 * Script statement syntax node.
 */
public abstract class Statement {
    public Statement(){}
    
    /**
     * Returns the text position of the first token of the statement. Used for errors reporting.
     */
    abstract TextPos getStartTextPos();

    /**
     * Executes this statement in the specified scope. Takes care about exceptions, adding the
     * text position.
     */
    final StatementResult execute(ScriptScope scope) throws SynsException {
        try {
            return execute0(scope);
        } catch (TextSynsException e) {
            throw e;
        } catch (SynsException e) {
            throw new TextSynsException(e, getStartTextPos());
        } catch (Throwable e) {
            throw new TextSynsException(e, getStartTextPos());
        }
    }
    
    /**
     * Executes the statement in the specified scope. Must not be called directly.
     */
    abstract StatementResult execute0(ScriptScope scope) throws SynsException;
}
