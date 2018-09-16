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
package org.antkar.syn.internal.binder;

import org.antkar.syn.SynNode;
import org.antkar.syn.TerminalNode;
import org.antkar.syn.TextPos;
import org.antkar.syn.ValueNode;
import org.antkar.syn.binder.AbstractToken;
import org.antkar.syn.binder.SynBinderException;

/**
 * Common superclass for token bound types.
 */
abstract class AbstractTokenBoundType extends AbstractBoundType {
    AbstractTokenBoundType(Class<? extends AbstractToken> javaType) {
        super(javaType);
    }

    @Override
    final Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        ValueNode valueNode = (ValueNode) synNode;
        if (valueNode == null) {
            //Node is null - return null.
            return null;
        }
        
        //Extract text position and create a token value.
        TextPos pos = ((TerminalNode)synNode).getPos();
        AbstractToken token = createToken(pos, valueNode);
        return token;
    }
    
    /**
     * Creates a token value from the given text position and a SYN value node.
     */
    abstract AbstractToken createToken(TextPos pos, ValueNode valueNode) throws SynBinderException;
}
