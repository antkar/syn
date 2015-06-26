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
package com.karmant.syn;

/**
 * Token value of type <code>double</code>.
 */
public class DoubleToken extends AbstractToken {
    private final double value;

    DoubleToken(TextPos pos, double value) {
        super(pos);
        this.value = value;
    }
    
    /**
     * Returns the <code>double</code> value of this object.
     * @return the value.
     */
    public double getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value + "";
    }
}
