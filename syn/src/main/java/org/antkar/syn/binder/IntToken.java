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
package org.antkar.syn.binder;

import org.antkar.syn.TextPos;

/**
 * Token value of type <code>int</code>.
 */
public final class IntToken extends AbstractToken {
    private final int value;

    public IntToken(TextPos pos, int value) {
        super(pos);
        this.value = value;
    }

    /**
     * Returns the <code>int</code> value of this object.
     * @return the value.
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
