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
package org.antkar.syn;

import org.antkar.syn.SynNode;
import org.antkar.syn.TerminalNode;

/**
 * {@link TerminalNode} bound type.
 */
class TerminalNodeBoundType extends AbstractBoundType {
    static final BoundType INSTANCE = new TerminalNodeBoundType();

    private TerminalNodeBoundType() {
        super(TerminalNode.class);
    }

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key) {
        TerminalNode terminalNode = (TerminalNode) synNode;
        return terminalNode;
    }
}