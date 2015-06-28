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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.StatementResult;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

import org.antkar.syn.SynField;
import org.antkar.syn.SynInit;

/**
 * Block syntax node.
 */
public class Block {
    /** Import declarations. */
    @SynField
    private Import[] synImports;
    
    /** Declarations. Variables, functions and other entities declared at the beginning of a
     * block are visible from outside and to each other. Local variables are not. */
    @SynField
    private Declaration[] synDeclarations;

    /** The first statement. Can be <code>null</code>. The first statement is separated from
     * all the other statements, because its syntax is limited. The first statement cannot be a
     * declaration statement, because otherwise there would be an ambiguity in the grammar,
     * since it would not be possible to exactly distinguish the declaration part of the block from
     * the statements part. */
    @SynField
    private Statement synFirstStatement;

    /** Other statements. Can contain variable declaration statements. */
    @SynField
    private Statement[] synNextStatements;
    
    /** List of all statements. */
    private List<Statement> statements;

    /** Names of all declared members. */
    private Set<String> members;
    
    /** Names of all declared functions. */
    private Set<String> functions;

    public Block(){}
    
    @SynInit
    private void init() {
        //Initialize the list of statements.
        statements = new ArrayList<>();
        if (synFirstStatement != null) {
            statements.add(synFirstStatement);
            for (Statement statement : synNextStatements) {
                statements.add(statement);
            }
        }
        
        //Initialize member names.
        members = new HashSet<>();
        functions = new HashSet<>();
        for (Declaration decl : synDeclarations) {
            String name = decl.getName();
            members.add(name);
            if (decl.isFunction()) {
                functions.add(name);
            }
        }
    }
    
    /**
     * Executes statements of this block in the specified scope. Declarations defined in this
     * block are added directly to the specified scope.
     */
    public StatementResult execute(ScriptScope declarationsScope) throws SynsException {
        registerDeclarations(declarationsScope);
        return executeWithoutDeclarations(declarationsScope);
    }

    /**
     * Executes statements of this block in the specified scope. Declarations are not added to
     * the scope, so the scope calling code must care about them.
     */
    StatementResult executeWithoutDeclarations(ScriptScope declarationsScope) throws SynsException {
        ScriptScope codeScope = declarationsScope.nestedBlockScope("block");
        
        for (Statement statement : statements) {
            StatementResult result = statement.execute(codeScope);
            if (result.isReturn() || result.isBreak() || result.isContinue()) {
                return result;
            }
        }
        
        return StatementResult.NONE;
    }
    
    /**
     * Registers declarations declared in this block, putting them to the specified scope.
     */
    void registerDeclarations(ScriptScope scope) throws SynsException {
        for (Import imp : synImports) {
            imp.addToScope(scope);
        }
        for (Declaration decl : synDeclarations) {
            decl.addToScope(scope);
        }
    }
    
    /**
     * Calls this block as a function in the specified scope.
     */
    public Value call(ScriptScope scope, RValue[] arguments) throws SynsException {
        ScriptScope declarationsScope = scope.nestedFunctionScope("block");
        StatementResult result = execute(declarationsScope);
        return result.isReturn() ? result.getReturnValue() : Value.forVoid();
    }
    
    /**
     * Calls a function declared in this block.
     */
    public Value callFunction(ScriptScope scope, String name, RValue[] arguments) throws SynsException {
        if (!functions.contains(name)) {
            //Must not get here.
            throw new IllegalStateException("Unknown function name: " + name);
        }

        //First, create a new scope for the block and put the declarations there. 
        ScriptScope declarationsScope = scope.nestedFunctionScope("block " + name);
        registerDeclarations(declarationsScope);
        
        //Then, find the function in the scope and call it.
        Value functionValue = declarationsScope.getValue(name);
        return functionValue.call(arguments);
    }
    
    /**
     * Checks whether a function with the specified name is declared in the block.
     */
    public boolean hasFunction(String name) {
        return functions.contains(name);
    }
    
    /**
     * Returns the member declared in this scope with the specified name, or <code>null</code>
     * if there is no such member.
     */
    public Value getMemberOpt(ScriptScope declarationsScope, String name) throws SynsException {
        if (!members.contains(name)) {
            return null;
        }
        //Must not return null.
        return declarationsScope.getValue(name);
    }
    
    /**
     * Converts this block to a script value.
     */
    Value toValue(ScriptScope scope) {
        return Value.forBlock(scope, this);
    }
    
    @Override
    public String toString() {
        return "{...}";
    }
}
