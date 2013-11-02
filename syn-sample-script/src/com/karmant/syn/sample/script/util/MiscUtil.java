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
package com.karmant.syn.sample.script.util;

/**
 * Helper utilities.
 */
public final class MiscUtil {
	private MiscUtil(){}

	/**
	 * Converts an array to a string using the specified separator string.
	 */
	public static String arrayToString(Object[] array, String sep) {
		StringBuilder bld = new StringBuilder();
		appendArray(bld, array, sep);
		return bld + "";
	}

	/**
	 * Appends an array to a string builder using the specified separator string.
	 */
	public static void appendArray(StringBuilder bld, Object[] array, String sep) {
		String curSep = "";
		for (Object obj : array) {
			bld.append(curSep);
			bld.append(obj);
			curSep = sep;
		}
	}
}
