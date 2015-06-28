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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;
import org.antkar.syn.SynInit;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.TextSynsException;
import org.antkar.syn.sample.script.rt.value.ClassMemberDescriptor;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Class declaration syntax node.
 */
public class ClassDeclaration extends Declaration {
    /** Member declarations. */
    @SynField
    private ClassMemberDeclaration[] synMembers;
    
    private List<ConstantDeclaration> constants;
    private List<Declaration> instanceMembers;
    private FunctionDeclaration constructor;
    
    private Map<String, ClassMemberDescriptor> staticMemberDescriptors;
    private List<ClassMemberDescriptor> instanceMemberDescriptors;
    private Map<String, ClassMemberDescriptor> memberDescriptors;
    
    public ClassDeclaration(){}
    
    @SynInit
    private void synInit() throws SynsException {
        initDeclarations();
        initMemberDescriptors();
    }
    
    private void initDeclarations() throws SynsException {
        checkNameConflicts();
        
        MemberDeclarationClassifier classifier = new MemberDeclarationClassifier();
        for (ClassMemberDeclaration member : synMembers) {
            member.getDeclaration().visit(classifier);
        }
        
        constructor = findConstructor(classifier.functions);
        
        //Create the list of instance members declarations.
        List<Declaration> mutableInstanceMembers = new ArrayList<>();
        mutableInstanceMembers.addAll(classifier.variables);
        mutableInstanceMembers.addAll(classifier.functions);
        
        constants = Collections.unmodifiableList(classifier.constants);
        instanceMembers = Collections.unmodifiableList(mutableInstanceMembers);
    }
    
    private void initMemberDescriptors() {
        Map<Declaration, ClassMemberDeclaration> declarationsMap = new IdentityHashMap<>();
        for (ClassMemberDeclaration memberDeclaration : synMembers) {
            declarationsMap.put(memberDeclaration.getDeclaration(), memberDeclaration);
        }
        
        Map<String, ClassMemberDescriptor> staticMemberMap =
                createMemberDescriptors(declarationsMap, constants, false);
        Map<String, ClassMemberDescriptor> instanceMemberMap =
                createMemberDescriptors(declarationsMap, instanceMembers, true);
        
        staticMemberDescriptors = Collections.unmodifiableMap(staticMemberMap);
        
        List<ClassMemberDescriptor> instanceMemberList = new ArrayList<>(instanceMemberMap.values());
        instanceMemberDescriptors = Collections.unmodifiableList(instanceMemberList);

        Map<String, ClassMemberDescriptor> memberMap = new HashMap<>();
        memberMap.putAll(staticMemberMap);
        memberMap.putAll(instanceMemberMap);
        memberDescriptors = Collections.unmodifiableMap(memberMap);
    }
    
    private static Map<String, ClassMemberDescriptor> createMemberDescriptors(
            Map<Declaration, ClassMemberDeclaration> declarationsMap,
            List<? extends Declaration> declarations,
            boolean instanceMember)
    {
        // Important to use LinkedHashMap - the order of declarations must be preserved.
        Map<String, ClassMemberDescriptor> map = new LinkedHashMap<>();

        for (Declaration declaration : declarations) {
            ClassMemberDeclaration memberDeclaration =
                    declarationsMap.get(declaration);
            String name = declaration.getName();
            int index = map.size();
            map.put(name, new ClassMemberDescriptor(memberDeclaration, instanceMember, index));
        }
        
        return map;
    }

    @Override
    Value evaluateValue(ScriptScope scope) throws SynsException {
        //Create a class scope and put the declared constants there.
        RValue[] constantValues = new RValue[staticMemberDescriptors.size()];
        ScriptScope classScope = scope.nestedClassScope("class " + getName());
        setupClassScope(constantValues, classScope);
        
        //Create a class value.
        Value value = Value.forClass(this, classScope, constantValues);
        return value;
    }

    /**
     * Checks if there are member name conflicts.
     */
    private void checkNameConflicts() throws TextSynsException {
        Set<String> names = new HashSet<>();
        for (ClassMemberDeclaration member : synMembers) {
            StringToken nameTk = member.getDeclaration().getNameTk();
            String name = nameTk.getValue();
            if (!names.add(name)) {
                throw new TextSynsException("Duplicated class member: " + name, nameTk.getPos());
            }
        }
    }

    /**
     * Finds the constructor function declaration. A constructor is the function with the same
     * name as the name of the class.
     */
    private FunctionDeclaration findConstructor(List<FunctionDeclaration> functions) {
        FunctionDeclaration constructorFn = null;
        
        String className = getName();
        for (FunctionDeclaration function : functions) {
            if (className.equals(function.getName())) {
                constructorFn = function;
                functions.remove(function);
                break;
            }
        }
        
        return constructorFn;
    }

    /**
     * Initializes class scope by putting constants there.
     */
    private void setupClassScope(RValue[] constantValues, ScriptScope classScope) throws SynsException
    {
        for (ClassMemberDescriptor memberDescriptor : staticMemberDescriptors.values()) {
            Declaration declaration = memberDescriptor.getDeclaration();
            Value value = declaration.addToScope(classScope);
            RValue rvalue = value.toRValue(); 
            constantValues[memberDescriptor.getIndex()] = rvalue;
        }
    }
    
    @Override
    void visit(Visitor visitor) throws SynsException {
        visitor.visitClassDeclaration(this);
    }
    
    public FunctionDeclaration getConstructor() {
        return constructor;
    }
    
    public ClassMemberDescriptor getStaticMemberDescriptorOpt(String name) {
        return staticMemberDescriptors.get(name);
    }
    
    public Collection<ClassMemberDescriptor> getInstanceMemberDescriptors() {
        return instanceMemberDescriptors;
    }
    
    public ClassMemberDescriptor getMemberDescriptorOpt(String name) {
        return memberDescriptors.get(name);
    }
    
    @Override
    public String toString() {
        return "class " + getName();
    }
    
    private static final class MemberDeclarationClassifier implements Declaration.Visitor {
        final List<ConstantDeclaration> constants = new ArrayList<>();
        final List<VariableDeclaration> variables = new ArrayList<>();
        final List<FunctionDeclaration> functions = new ArrayList<>();

        @Override
        public void visitConstantDeclaration(ConstantDeclaration declaration) throws SynsException {
            constants.add(declaration);
        }

        @Override
        public void visitVariableDeclaration(VariableDeclaration declaration) throws SynsException {
            variables.add(declaration);
        }

        @Override
        public void visitFunctionDeclaration(FunctionDeclaration declaration) throws SynsException {
            functions.add(declaration);
        }

        @Override
        public void visitClassDeclaration(ClassDeclaration declaration) throws SynsException {
            throw new SynsException("Nested classes are not supported");
        }
    }
}
