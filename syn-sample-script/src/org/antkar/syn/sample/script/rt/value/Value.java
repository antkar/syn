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
package org.antkar.syn.sample.script.rt.value;

import java.util.List;
import java.util.Map;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.TextSynsException;
import org.antkar.syn.sample.script.rt.javacls.JavaClass;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.schema.Block;
import org.antkar.syn.sample.script.schema.Declaration;
import org.antkar.syn.sample.script.schema.FunctionDeclaration;

import org.antkar.syn.StringToken;

/**
 * A value. Subclasses represent values of various types used to hold variable values, function
 * arguments, etc.
 */
public abstract class Value {
    Value(){}
    
    /**
     * Returns a void value.
     */
    public static Value forVoid() {
        return VoidValue.INSTANCE;
    }
    
    /**
     * Returns a null value.
     */
    public static RValue forNull() {
        return NullValue.INSTANCE;
    }
    
    /**
     * Returns a value for Java <code>boolean</code>.
     */
    public static RValue forBoolean(boolean value) {
        return BooleanValue.valueOf(value);
    }

    /**
     * Returns a value for Java <code>int</code>.
     */
    public static RValue forInt(int value) {
        return new IntValue(value);
    }

    /**
     * Returns a value for Java <code>long</code>.
     */
    public static RValue forLong(long value) {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            return forInt((int)value);
        }
        return new LongValue(value);
    }

    /**
     * Returns a value for Java <code>double</code>.
     */
    public static RValue forDouble(double value) {
        return new DoubleValue(value);
    }

    /**
     * Returns a value for Java {@link String}.
     */
    public static RValue forString(String value) {
        return new StringValue(value);
    }
    
    /**
     * Returns a value for a Script Language class.
     */
    public static Value forClass(
            String className,
            ScriptScope classScope,
            FunctionDeclaration constructor,
            Map<String, RValue> constants,
            List<Declaration> instanceMembers)
    {
        return new ClassValue(className, classScope, constructor, constants, instanceMembers);
    }
    
    /**
     * Returns a value for a Java array.
     */
    public static RValue forJavaArray(Object[] array) throws SynsException {
        RValue[] values = new RValue[array.length];
        for (int i = 0; i < values.length; ++i) {
            values[i] = forJavaObject(array[i]);
        }
        return new ArrayValue(values);
    }
    
    /**
     * Returns a value for a Java {@link Object}. The type of the returned value depends on the
     * actual type of the object.
     */
    public static RValue forJavaObject(Object value) throws SynsException {
        if (value == null) {
            return forNull();
        } else if (value instanceof Boolean) {
            return forBoolean((Boolean)value);
        } else if (value instanceof Integer) {
            return forInt((Integer)value);
        } else if (value instanceof Long) {
            return forLong((Long)value);
        } else if (value instanceof Double) {
            return forDouble((Double)value);
        } else if (value instanceof Float) {
            return forDouble((Float)value);
        } else if (value instanceof String) {
            return forString((String)value);
        } else if (value instanceof RValue) {
            return (RValue)value;
        } else if (value instanceof Value) {
            return ((Value)value).toRValue();
        } else if (value instanceof RValue[]) {
            return newArray((RValue[]) value);
        } else if (value instanceof Object[]) {
            return forJavaArray((Object[])value);
        } else {
            return new JavaObjectValue(value);
        }
    }
    
    /**
     * Returns a value for a Script Language block construction.
     */
    public static Value forBlock(ScriptScope scope, Block block) {
        return new BlockValue(scope, block);
    }
    
    /**
     * Returns a value for a Java class.
     */
    public static Value forJavaClass(JavaClass javaClass) {
        return new JavaClassValue(javaClass);
    }
    
    /**
     * Returns a value for a Script Language function.
     */
    public static Value forFunction(ScriptScope scope, FunctionDeclaration function) {
        return new FunctionValue(scope, function);
    }

    /**
     * Creates a new variable value with the specified initial value.
     */
    public static LValue newVariable(Value initialValue) throws SynsException {
        assert initialValue != null;
        return new VariableValue(initialValue.toRValue());
    }
    
    /**
     * Creates a new Script Language array value with the specified element values.
     */
    public static RValue newArray(RValue[] values) {
        return new ArrayValue(values);
    }
    
    /**
     * Returns the type of this value.
     */
    public abstract ValueType getValueType(); 
    
    /**
     * Returns the human-readable name of the type of this value. Used in exception messages and
     * for debugging purposes.
     */
    public String getTypeMessage() {
        return getValueType().getTypeName();
    }
    
    /**
     * Constructs a compound type message.
     */
    final String getCompoundTypeMessage(String description) {
        ValueType type = getValueType();
        return type.getTypeName() + "[" + description + "]";
    }

    /**
     * Returns <code>true</code> if this is a void value.
     */
    public boolean isVoid() {
        return false;
    }
    
    /**
     * Calls this value, considering it a function or another entity that can be called.
     * If the value cannot be called, an exception is thrown.
     * @throws SynsException if call fails.
     */
    public Value call(RValue[] arguments) throws SynsException {
        throw errInvalidOperation();
    }
    
    /**
     * Returns the value of the member with the specified name. Throws an exception if the value
     * does not support members or there is no member with such name.
     */
    public Value getMember(StringToken nameTk) throws SynsException {
        String name = nameTk.getValue();
        Value value = getMemberOpt(name);
        if (value == null) {
            throw new TextSynsException("Unknown name: " + name, nameTk.getPos());
        }
        return value;
    }

    /**
     * Returns the value of the member with the specified name, or <code>null</code> if there is
     * no such member. Throws an exception if the value does not support members at all.
     */
    public Value getMemberOpt(String name) throws SynsException {
        throw errInvalidOperation();
    }

    /**
     * Constructs a new object value from this value. This value must be a class value. Otherwise,
     * an exception is thrown.
     * @throws SynsException - if construction fails.
     */
    public Value newObject(RValue[] arguments) throws SynsException {
        throw errInvalidOperation();
    }

    /**
     * Treats this value as an l-value. Throws an exception if this value is not an l-value. 
     */
    public LValue toLValue() throws SynsException {
        throw errInvalidOperation();
    }
    
    /**
     * Returns the r-value associated with this value. Throws an exception this type of value does
     * not have an associated r-value.
     */
    public RValue toRValue() throws SynsException {
        throw errInvalidOperation();
    }
    
    /**
     * Converts this value to an arithmetical operator operand. Throws an exception if a value
     * of this type cannot be used as an operand.
     */
    public Operand toOperand() throws SynsException {
        throw errInvalidOperation();
    }

    @Override
    public String toString() {
        return getTypeMessage();
    }

    /**
     * Throws an invalid operation exception.
     */
    RuntimeException errInvalidOperation() throws SynsException {
        throw SynsException.format("Invalid operation for %s", getTypeMessage());
    }
}
