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
 * Thrown if there is a semantical error in the grammar passed to a {@link SynParser}.
 */
public class SynGrammarException extends SynTextException {
	private static final long serialVersionUID = 3530779109153425091L;

	SynGrammarException(TextPos textPos, String message, Throwable cause) {
		super(textPos, message, cause);
	}

	SynGrammarException(TextPos textPos, String message) {
		super(textPos, message);
	}
}
