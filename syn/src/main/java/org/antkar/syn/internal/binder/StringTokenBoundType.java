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
import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynBinderException;

/**
 * {@link StringToken} bound type.
 */
class StringTokenBoundType extends AbstractBoundType {
    private final BoundType innerType;

    StringTokenBoundType(BoundType innerType) {
        super(StringToken.class);
        this.innerType = innerType;
    }

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        TextPos pos = ((TerminalNode)synNode).getPos();
        String str = (String)innerType.convertNode(engine, synNode, bObjOwner, key);
        StringToken token = new StringToken(pos, str);
        return token;
    }
}
