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

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.StatementResult;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.ThrowSynsException;
import org.antkar.syn.sample.script.rt.value.Value;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;
import org.antkar.syn.TextPos;

/**
 * Script <code>try</code>-<code>catch</code>-<code>finally</code> statement syntax node.
 */
public class TryStatement extends Statement {
    /** The text position of the first token. */
    @SynField
    private TextPos synPos;

    /** The statement inside the <code>try</code> block. */
    @SynField
    private Statement synTryStatement;
    
    /** The name of the <code>catch</code> part variable, if any. */
    @SynField
    private StringToken synCatchVariable;
    
    /** The statement inside the <code>catch</code> part, if any. */
    @SynField
    private Statement synCatchStatement;
    
    /** The statement inside the <code>finally</code> part, if any. */
    @SynField
    private Statement synFinallyStatement;
    
    public TryStatement(){}

    @Override
    TextPos getStartTextPos() {
        return synPos;
    }

    @Override
    StatementResult execute0(ScriptScope scope) throws SynsException {
        try {
            StatementResult tryResult = synTryStatement.execute(scope);
            return tryResult;
        } catch (ThrowSynsException e) {
            //Exception thrown by a script "throw" statement. The original exception must be
            //passed to the catch block, but if there is no catch block, the wrapped exception has
            //to be thrown.
            return handleException(scope, e.getCause(), e);
        } catch (SynsException e) {
            //Script runtime exception. Has to be passed to the catch block directly, and thrown,
            //if there is no catch block.
            return handleException(scope, e, e);
        } catch (RuntimeException | Error e) {
            //Arbitrary Java exception. To be passed to the catch block directly, but if there is no
            //one, a new wrapped exception has to be thrown.
            return handleException(scope, e, new SynsException(e));
        } finally {
            //Execute the finally statement.
            if (synFinallyStatement != null) {
                StatementResult finallyResult = synFinallyStatement.execute(scope);
                if (finallyResult.isReturn() || finallyResult.isBreak() || finallyResult.isContinue()) {
                    //If the finally statement finished with a break, continue or return, the
                    //whole try-catch statement have to finish in the same way, overriding the
                    //result of the try and catch blocks. E. g. if the try statement finishes with
                    //return, but finally statement finishes with break, the result of the
                    //try-catch-finally statement will be break, not return.
                    return finallyResult;
                }
            }
        }
    }
    
    /**
     * Handles an exception occurred during <code>try</code> block execution.
     */
    private <E extends Exception> StatementResult handleException(
            ScriptScope scope,
            Throwable toCatch,
            E toThrow) throws E, SynsException
    {
        if (synCatchVariable == null) {
            //No catch block - throw the exception back.
            throw toThrow;
        }
        
        //Execute the catch block.
        StatementResult catchResult = executeCatchStatement(scope, toCatch);
        return catchResult;
    }

    /**
     * Executes the catch statement.
     */
    private StatementResult executeCatchStatement(ScriptScope scope, Throwable e) throws SynsException {
        //Create a scope and put the catch variable there.
        ScriptScope derivedScope = scope.deriveNestedScope("catch");
        Value value = Value.forJavaObject(e);
        Value variable = Value.newVariable(value);
        derivedScope.addValue(synCatchVariable, variable);
        
        //Execute the statement in the new scope.
        StatementResult catchResult = synCatchStatement.execute(derivedScope);
        return catchResult;
    }
    
    @Override
    public String toString() {
        return "try {...}";
    }
}

