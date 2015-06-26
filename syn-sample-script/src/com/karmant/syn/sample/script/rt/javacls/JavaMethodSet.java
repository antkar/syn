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
package com.karmant.syn.sample.script.rt.javacls;

import java.util.List;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.JavaInstanceMethodValue;
import com.karmant.syn.sample.script.rt.value.JavaStaticMethodValue;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * A set of Java methods. Contains overloaded methods - methods with the same name, but different
 * signatures.
 */
public class JavaMethodSet extends JavaMember {
    private final String name;
    private final List<? extends JavaAbstractMethod> methods;

    JavaMethodSet(String name, List<? extends JavaAbstractMethod> methods) {
        this.name = name;
        this.methods = methods;
    }
    
    @Override
    public String toString() {
        return name;
    }

    /**
     * Invokes a static method.
     */
    public Value callStatic(RValue[] arguments) throws SynsException {
        return call(null, arguments);
    }
    
    /**
     * Invokes an instance method.
     */
    public Value callInstance(Object obj, RValue[] arguments) throws SynsException {
        return call(obj, arguments);
    }
    
    @Override
    Value getStaticValue() {
        return new JavaStaticMethodValue(this);
    }

    @Override
    Value getInstanceValue(Object obj) {
        return new JavaInstanceMethodValue(this, obj);
    }

    /**
     * Calls an appropriate method.
     */
    private Value call(Object obj, RValue[] arguments) throws SynsException {
        JavaAbstractMethod method = findMethod(arguments);
        if (method == null) {
            throw new SynsException("Invalid arguments for method " + name);
        }
        if (obj == null && !method.isStatic()) {
            throw new SynsException("Cannot statically call instance method " + name);
        }
        
        Object result = method.invoke(obj, arguments);
        if (method.isVoid()) {
            return Value.forVoid();
        } else {
            return Value.forJavaObject(result);
        }
    }

    /**
     * Finds a method that matches the specified arguments.
     */
    private JavaAbstractMethod findMethod(RValue[] arguments) throws SynsException {
        JavaAbstractMethod bestMethod = null;
        int bestPrecision = 0;
        
        for (JavaAbstractMethod method : methods) {
            int precision = method.matchArguments(arguments);
            if (precision != -1) {
                if (bestMethod == null || isMethodBetter(method, precision, bestMethod, bestPrecision)) {
                    bestMethod = method;
                    bestPrecision = precision;
                }
            }
        }
        
        return bestMethod;
    }
    
    /**
     * Determines which of two methods has to be preferred.
     */
    private static boolean isMethodBetter(
            JavaAbstractMethod method1,
            int precision1,
            JavaAbstractMethod method2,
            int precision2)
    {
        //Methods comparison is done in a simplified way compared to the way how Java compiler
        //chooses the best method. The main difference is in the comparison of methods with variable
        //numbers of arguments, but such methods are not used very often, so the solution seems
        //to be suitable.
        
        //If one method has a variable number of arguments, and the other one does not, the
        //method with the fixed number of arguments is preferred.
        boolean varArgs1 = method1.isVarArgs();
        boolean varArgs2 = method2.isVarArgs();
        if (varArgs1 != varArgs2) {
            return varArgs2;
        }
        
        //Otherwise - check parameter types.
        Class<?>[] params1 = method1.getParameterTypes();
        Class<?>[] params2 = method2.getParameterTypes();
        int len = Math.min(params1.length, params2.length);
        
        for (int i = 0; i < len; ++i) {
            Class<?> type1 = params1[i];
            Class<?> type2 = params2[i];
            
            if (!type1.equals(type2)) {
                if (precision1 > precision2) {
                    return true;
                }
                
                return isTypeBetter(type1, type2);
            }
            
        }
        
        return false;
    }

    /**
     * Determines which of two parameter types is more precise and therefore preferable.
     */
    private static boolean isTypeBetter(Class<?> type1, Class<?> type2) {
        //Primitive arguments defeats a non-primitive one.
        boolean primitive1 = type1.isPrimitive();
        boolean primitive2 = type2.isPrimitive();
        if (primitive1 != primitive2) {
            return primitive1;
        }
        
        return type2.isAssignableFrom(type1);
    }
}
