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

/**
 * An error in script execution.
 */
public class SynsException extends Exception {
	private static final long serialVersionUID = 2396092981958222183L;

	public SynsException() {
		super();
	}

	public SynsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SynsException(String message) {
		super(message);
	}

	public SynsException(Throwable cause) {
		super(cause);
	}
	
	public static SynsException format(String format, Object... params) {
		String message = String.format(format, params);
		return new SynsException(message);
	}
}
