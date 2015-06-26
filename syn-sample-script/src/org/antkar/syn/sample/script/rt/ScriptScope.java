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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.antkar.syn.sample.script.rt.javacls.JavaClass;
import org.antkar.syn.sample.script.rt.value.Value;
import org.antkar.syn.sample.script.util.MiscUtil;

import org.antkar.syn.StringToken;

/**
 * A scope. Contains a set of names visible in a particular point of a script.
 */
public class ScriptScope {

    /** Parent scope. If a name is not found in this scope, it is looked for in the parent one. */
    private final ScriptScope parentScope;
    
    /** Human-readable description. Used for debugging purposes. */
    private final String description;
    
    /** When <code>true</code>, it is not allowed to add a name into this scope if the same name is
     * defined in the parent scope (and its parent scope, if the field is <code>true</code> for the
     * parent scope, and so on.). Used, for example, in <code>for</code> Script Language statement,
     * which involves two scopes: a header scope (where the loop counter variable is defined) and
     * a body scope. Though those are two different scopes, it is not allowed to define a variable
     * in the body scope if a variable with the same name is defined in the header scope. */
    private final boolean isSharingParentNamespace;
    
    /** <code>true</code> if this scope is inside a loop, and therefore <code>break</code> and
     * <code>continue</code> statements are allowed. */
    private final boolean isLoop;
    
    /** <code>true</code> if this scope is a function scope, and therefore <code>return</code>
     * statement is allowed. */
    private final boolean isFunction;
    
    /** Collection of on-demand imports. */
    private final Collection<OnDemandImport> onDemandImports;
    
    /** Map of names defined directly in this scope. */
    private final Map<String, Value> nameMap;
    
    private ScriptScope(
            ScriptScope parentScope,
            String description,
            boolean shareNamespace,
            boolean loop,
            boolean function)
    {
        this.parentScope = parentScope;
        this.description = description;
        
        isSharingParentNamespace = shareNamespace;
        isLoop = loop;
        isFunction = function;
        
        onDemandImports = new ArrayList<>();
        nameMap = new HashMap<>();
    }
    
    /**
     * Creates the root scope - the top level scope which does not have a parent scope.
     * The scope includes <code>java.lang.*</code> on-demand import.
     */
    public static ScriptScope createRootScope() {
        ScriptScope scope = new ScriptScope(null, "root", false, false, false);
        scope.onDemandImports.add(new JavaPackageOnDemandImport("java.lang."));
        return scope;
    }
    
    /**
     * Creates a derived scope for a function.
     */
    public ScriptScope deriveFunctionScope(String descr) {
        return deriveScope(descr, false, false, true);
    }
    
    /**
     * Creates a derived scope for a loop.
     */
    public ScriptScope deriveLoopScope(String descr) {
        return deriveScope(descr, true, true, isFunction);
    }
    
    /**
     * Creates a derived nested scope.
     */
    public ScriptScope deriveNestedScope(String descr) {
        return deriveScope(descr, true, isLoop, isFunction);
    }
    
    /**
     * Creates a derived scope for a class.
     */
    public ScriptScope deriveClassScope(String descr, boolean sharingParentNamespace) {
        return deriveScope(descr, sharingParentNamespace, false, false);
    }

    /**
     * Creates a derived scope with the specified properties.
     */
    private ScriptScope deriveScope(
            String descr,
            boolean shareNamespace,
            boolean loop,
            boolean function)
    {
        return new ScriptScope(this, descr, shareNamespace, loop, function);
    }

    /**
     * Returns the value associated with the specified name. The name is first looked up in the
     * names table of this scope, then in this scope's on-demand imports, then in the parent scope,
     * and so on.
     * If the name is not resolved, an exception is thrown.
     */
    public Value getValue(StringToken nameTk) throws SynsException {
        String name = nameTk.getValue();
        Value value = getValueOpt(name);
        if (value == null) {
            throw new TextSynsException("Name not found: " + name, nameTk.getPos());
        }
        return value;
    }
    
    /**
     * Returns the value associated with the specified name. Throws an exception if the name is undefined.
     * 
     * @see #getValue(StringToken)
     */
    public Value getValue(String name) throws SynsException {
        Value value = getValueOpt(name);
        if (value == null) {
            throw new SynsException("Name not found: " + name);
        }
        return value;
    }

    /**
     * Looks for a value with the specified name in this and all parent scopes.
     * If the value is not found, <code>null</code> is returned.
     */
    private Value getValueOpt(String name) throws SynsException {
        ScriptScope scope = this;
        while (scope != null) {
            Value value = scope.getLocalValueOpt(name);
            if (value != null) {
                return value;
            }
            scope = scope.parentScope;
        }
        
        return null;
    }
    
    /**
     * Looks for a value defined directly in this scope.
     */
    private Value getLocalValueOpt(String name) throws SynsException {
        //Look in the names table.
        Value value = nameMap.get(name);
        if (value != null) {
            return value;
        }
        
        //Look in on-demand imports.
        for (OnDemandImport onDemand : onDemandImports) {
            value = onDemand.getValueOpt(name);
            if (value != null) {
                return value;
            }
        }
    
        //Not found.
        return null;
    }
    
    /**
     * <p>Returns a value denoted by a chain of names. This operation cannot be replaced by a sequence
     * of {@link #getValue(StringToken)} operations, because of Java reflection API limitations.
     * The API does not allow to determine if there is a package with a particular name. Therefore,
     * it is not possible to resolve a name chain like <code>javax.swing.text.StyledDocument</code>
     * by resolving all the names one by one. But if the entire name chain is known, it is possible
     * to determine if that chain denotes a Java class.
     */
    public Value getValue(StringToken[] nameChain) throws SynsException {
        Value value = getValueOpt(nameChain[0].getValue());
        if (value != null) {
            //The first name of the chain is defined in the scope. The other names can be resolved
            //one-by-one.
            value = getValueByNameChain(value, nameChain, 1);
            return value;
        }
        
        //The first name is undefined. Try to find a Java class by a fully qualified name.
        value = resolveJavaValueByNameChainOpt(nameChain);
        if (value == null) {
            String name = nameChainToString(nameChain);
            throw new TextSynsException("Name not found: " + name, nameChain[0].getPos());
        }
        
        return value;
    }

    /**
     * Converts a name chain to string.
     */
    private static String nameChainToString(StringToken[] nameChain) {
        return MiscUtil.arrayToString(nameChain, ".");
    }
    
    /**
     * Adds a single import item. Throws an exception if the name chain cannot be resolved.
     */
    public void addSingleImport(StringToken[] nameChain) throws SynsException {
        Value value = resolveJavaValueByNameChainOpt(nameChain);
        if (value == null) {
            String name = nameChainToString(nameChain);
            throw new TextSynsException("Cannot resolve import: " + name, nameChain[0].getPos());
        }
        
        //Simply add the last name to the scope.
        StringToken lastName = nameChain[nameChain.length - 1];
        addLocalValue(lastName, value);
    }

    /**
     * Adds a new variable to the scope. 
     */
    private void addLocalValue(StringToken nameTk, Value value) throws TextSynsException {
        String name = nameTk.getValue();
        if (isNameConflicting(name)) {
            throw new TextSynsException("Name conflict: " + name, nameTk.getPos());
        }
        nameMap.put(name, value);
    }
    
    /**
     * Adds an on-demand import.
     */
    public void addOnDemandImport(StringToken[] nameChain) throws SynsException {
        OnDemandImport imp = createOnDemandImport(nameChain);
        onDemandImports.add(imp);
    }

    /**
     * Creates an on-demand import object.
     */
    private OnDemandImport createOnDemandImport(StringToken[] nameChain) throws SynsException {
        Value value = resolveJavaValueByNameChainOpt(nameChain);
        if (value != null) {
            //The name chain denotes a value, e. g. a Java class fully qualified name. In this case,
            //members of the value must be accessible on-demand.
            return new ValueOnDemandImport(value);
        } else {
            //There is no value denoted by the name chain. Consider the chain a Java package name.
            //It is impossible to check whether the package exists by the means of Java reflection API.
            String packagePrefix = nameChainToPackagePrefix(nameChain, nameChain.length);
            return new JavaPackageOnDemandImport(packagePrefix);
        }
    }
    
    private static String nameChainToPackagePrefix(StringToken[] nameChain, int end) {
        StringBuilder bld = new StringBuilder();
        MiscUtil.appendArray(bld, nameChain, ".");
        bld.append(".");
        return bld.toString();
    }

    /**
     * Resolves a Java value denoted by a name chain. 
     */
    private static Value resolveJavaValueByNameChainOpt(StringToken[] nameChain) throws SynsException {
        //The beginning of the chain must name a Java class. The rest (which can be empty) may
        //name a chain of the class' members.
        
        String sep = "";
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < nameChain.length; ++i) {
            bld.append(sep);
            bld.append(nameChain[i]);
            sep = ".";
            
            String className = bld.toString();
            Value value = getJavaClassValueOpt(className);
            if (value != null) {
                //Java class found. Use the rest of the chain to get the value.
                value = getValueByNameChain(value, nameChain, i + 1);
                return value;
            }
        }
        
        return null;
    }

    /**
     * Gets a value by a name chain.
     */
    private static Value getValueByNameChain(Value value, StringToken[] nameChain, int start)
            throws SynsException
    {
        for (int i = start; i < nameChain.length; ++i) {
            value = value.getMember(nameChain[i]);
        }
        return value;
    }

    /**
     * Returns the value describing a Java class with the specified fully qualified name.
     * If no Java class with such name exists, <code>null</code> is returned.
     */
    static Value getJavaClassValueOpt(String className) {
        try {
            Class<?> cls = Class.forName(className);
            JavaClass javaClass = JavaClass.getInstance(cls);
            return Value.forJavaClass(javaClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    /**
     * Adds a name with the specified value to this scope. Throws an exception is a name conflict
     * is detected.
     */
    public void addValue(StringToken name, Value value) throws TextSynsException {
        addLocalValue(name, value);
    }

    /**
     * Returns <code>true</code> if this is a loop scope.
     */
    public boolean isLoop() {
        return isLoop;
    }

    /**
     * Returns <code>true</code> if this is a function scope.
     */
    public boolean isFunction() {
        return isFunction;
    }
    
    /**
     * Returns <code>true</code> if the specified name is in a conflict with an already defined name.
     */
    private boolean isNameConflicting(String name) {
        ScriptScope scope = this;
        
        while (scope != null) {
            if (scope.nameMap.containsKey(name)) {
                return true;
            }
            if (!scope.isSharingParentNamespace) {
                break;
            }
            scope = scope.parentScope;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        String sep = "";
        
        ScriptScope scope = this;
        while (scope != null) {
            bld.append(sep);
            bld.append(scope.description);
            scope = scope.parentScope;
            sep = " - ";
        }

        return bld + "";
    }
}
