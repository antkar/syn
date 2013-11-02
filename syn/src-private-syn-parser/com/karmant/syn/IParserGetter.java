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
 * Parser node getter. Used to get a parser node associated with a grammar attribute from a parser stack.
 */
interface IParserGetter {

	/**
	 * Returns the offset of the stack element where to read a value from.
	 */
	int offset();
	
	/**
	 * Gets a parser node from a stack element.
	 * @param element the stack element at the offset returned by {@link #offset()}.
	 * @return the parser node.
	 */
	IParserNode get(ParserStackElement element);
}
