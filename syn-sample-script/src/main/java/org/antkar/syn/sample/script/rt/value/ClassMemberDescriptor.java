/*
 * Copyright 2015 Anton Karmanov
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
import org.antkar.syn.sample.script.schema.ClassDeclaration;
import org.antkar.syn.sample.script.schema.ClassMemberDeclaration;
import org.antkar.syn.sample.script.schema.Declaration;

/**
 * Provides access to a value of a class member, taking access modifiers into account.
 */
public final class ClassMemberDescriptor {
    private final ClassMemberDeclaration memberDeclaration;
    private final boolean instanceMember;
    private final int index;

    public ClassMemberDescriptor(
            ClassMemberDeclaration memberDeclaration,
            boolean instanceMember,
            int index)
    {
        this.memberDeclaration = memberDeclaration;
        this.instanceMember = instanceMember;
        this.index = index;
    }

    public Declaration getDeclaration() {
        return memberDeclaration.getDeclaration();
    }

    public int getIndex() {
        return index;
    }

    Value read(
            ClassValue classValue,
            ObjectValue objectValue,
            ScriptScope readerScope)
    {
        if (!memberDeclaration.isPublic() && !canAccessPrivateMember(classValue, readerScope)) {
            return null;
        }

        return instanceMember ? objectValue.readValue(index) : classValue.readValue(index);
    }

    private boolean canAccessPrivateMember(ClassValue classValue, ScriptScope readerScope) {
        if (readerScope == null) {
            return false;
        }

        ObjectValue thisValue = readerScope.getThisValueOpt();
        if (thisValue == null) {
            return false;
        }

        ClassDeclaration readerClassDeclaration = thisValue.getClassDeclaration();
        ClassDeclaration targetClassDeclaration = classValue.getClassDeclaration();
        return targetClassDeclaration.equals(readerClassDeclaration);
    }

    @Override
    public String toString() {
        return getDeclaration().getName() + ":" + index;
    }
}
