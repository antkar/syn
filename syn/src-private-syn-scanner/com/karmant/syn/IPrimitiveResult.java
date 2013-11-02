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
 * A result returned by a primitive scanner. Describes the scanned token. A primitive scanner does not
 * create a new instance of {@link IPrimitiveResult}. Instead, it returns the same instance, but modifies its
 * state to reflect the actual token. This allows to avoid an overhead when it is not necessary to create an
 * instance of {@link TerminalNode} for a token.   
 */
interface IPrimitiveResult {
	/**
	 * Returns the token descriptor of the token.
	 * @return the token descriptor.
	 */
	TokenDescriptor getTokenDescriptor();
	
	/**
	 * Creates a {@link TerminalNode} for the token.
	 * @param pos the text position of the first character of the token to be put into the node. The position
	 * is tracked by {@link DefaultTokenStream}, not by primitive scanners. 
	 * 
	 * @return the text position.
	 */
	TerminalNode createTokenNode(PosBuffer pos);
}
