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
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.TokenNode;

/**
 * A bound type that returns a {@link TokenNode}'s literal.
 */
final class LiteralBoundType extends AbstractBoundType {
    static final BoundType INSTANCE = new LiteralBoundType();
    static final BoundType STRING_TOKEN_INSTANCE = new StringTokenBoundType(INSTANCE);

    private LiteralBoundType() {
        super(String.class);
    }

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key) {
        TokenNode tokenNode = (TokenNode) synNode;
        TokenDescriptor tokenDesc = tokenNode.getTokenDescriptor();
        String string = tokenDesc.getLiteral();
        return string;
    }
}
