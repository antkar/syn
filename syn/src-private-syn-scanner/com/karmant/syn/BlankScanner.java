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
 * A primitive scanner for white spaces. A white space is defined by the {@link Character#isWhitespace(int)}
 * method.
 */
class BlankScanner implements IPrimitiveScanner {
	BlankScanner(){}
	
	@Override
	public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
		if (!Character.isWhitespace(context.current)) {
			//No white spaces at all - return null.
			return null;
		}
		
		//Skip white spaces.
		context.next();
		while (Character.isWhitespace(context.current)) {
			context.next();
		}
		
		return NonePrimitiveResult.INSTANCE;
	}
}
