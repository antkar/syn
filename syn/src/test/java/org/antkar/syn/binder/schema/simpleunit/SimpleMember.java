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
package org.antkar.syn.binder.schema.simpleunit;

import org.antkar.syn.TextPos;
import org.antkar.syn.binder.SynField;
import org.antkar.syn.binder.SynInit;
import org.antkar.syn.binder.SynLookup;


public final class SimpleMember {
    @SynField
    private String sfName;

    @SynField
    private String sfType;

    @SynField
    private TextPos sfPos;

    @SynField("sfName")
    private TextPos sfNamePos;

    @SynField("sfType")
    private TextPos sfTypePos;

    @SynLookup("obj == this.owner")
    private SimpleEntity entity;

    @SynLookup("obj.sfName == this.sfType")
    private SimpleEntity type;

    @SynLookup("obj != this && obj.owner == this.owner && obj.sfName == this.sfName")
    private SimpleMember[] membersWithSameName;

    public SimpleMember() {
        super();
    }

    public String getName() {
        return sfName;
    }

    public SimpleEntity getType() {
        return type;
    }

    public String getSfType() {
        return sfType;
    }

    public TextPos getPos() {
        return sfPos;
    }

    public TextPos getNamePos() {
        return sfNamePos;
    }

    public TextPos getTypePos() {
        return sfTypePos;
    }

    @SynInit
    private void init() {
        if (membersWithSameName.length > 0) {
            throw new IllegalStateException(String.format("There is more than one member %s.%s",
                    entity.getName(), sfName));
        }
        if (type == null) {
            throw new IllegalStateException(String.format(
                    "Name '%s' is used as the type of field %s.%s, but it is not defined",
                    sfType, entity.getName(), sfName));
        }
    }
}
