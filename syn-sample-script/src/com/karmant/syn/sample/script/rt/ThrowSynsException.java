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
package com.karmant.syn.sample.script.rt;

import com.karmant.syn.TextPos;

/**
 * A script exception that is thrown internally when the Script Language <code>throw</code> statement is
 * executed.
 */
public class ThrowSynsException extends TextSynsException {
    private static final long serialVersionUID = -9033015625799555584L;

    public ThrowSynsException(Throwable cause, TextPos textPos) {
        super(cause.getMessage(), cause, textPos);
    }
}
