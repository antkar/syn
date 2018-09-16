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

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.javacls.TypeMatchPrecision;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.schema.ClassDeclaration;

/**
 * Script object value.
 */
public final class ObjectValue extends RValue {
    private final ClassValue classValue;
    private final Value[] memberValues;

    ObjectValue(ClassValue classValue, Value[] memberValues) {
        this.classValue = classValue;
        this.memberValues = memberValues;
    }

    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forObject(this, this);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }

    @Override
    public String getTypeMessage() {
        String className = classValue.getClassDeclaration().getName();
        return getCompoundTypeMessage(className);
    }

    @Override
    public Value getMemberOpt(String name, ScriptScope readerScope) {
        ClassDeclaration classDeclaration = classValue.getClassDeclaration();
        ClassMemberDescriptor descriptor = classDeclaration.getMemberDescriptorOpt(name);
        Value result = descriptor == null ? null : descriptor.read(classValue, this, readerScope);
        return result;
    }

    @Override
    public Object toJava(Class<?> type, TypeMatchPrecision precision) {
        if (!type.equals(Object.class)) {
            return INVALID;
        }
        return this;
    }

    public ClassDeclaration getClassDeclaration() {
        return classValue.getClassDeclaration();
    }

    Value readValue(int index) {
        return memberValues[index];
    }

    @Override
    public String toString() {
        return getTypeMessage();
    }
}
