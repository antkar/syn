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

import org.antkar.syn.AbstractToken;
import org.antkar.syn.LongToken;
import org.antkar.syn.SynBinderException;
import org.antkar.syn.TextPos;
import org.antkar.syn.ValueNode;

/**
 * {@link LongToken} bound type.
 */
class LongTokenBoundType extends AbstractTokenBoundType {
    static final BoundType INSTANCE = new LongTokenBoundType();

    private LongTokenBoundType() {
        super(LongToken.class);
    }

    @Override
    AbstractToken createToken(TextPos pos, ValueNode valueNode) throws SynBinderException {
        long value = valueNode.getLong();
        return new LongToken(pos, value);
    }
}
