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
 * A link between two key-character tree nodes.
 */
class KeycharTreeLink {
	private final char ch;
	private final KeycharTreeNode state;
	
	KeycharTreeLink(char ch, KeycharTreeNode state) {
		assert state != null;
		this.ch = ch;
		this.state = state;
	}
	
	/**
	 * Returns the character associated with this link.
	 */
	char getCh() {
		return ch;
	}
	
	/**
	 * Returns the destination node.
	 */
	KeycharTreeNode getDestinationNode() {
		return state;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("'");
		if (ch == '\'' || ch == '\\') {
			bld.append('\\');
			bld.append(ch);
		} else if (ch >= 0x20 && ch < 0x80) {
			bld.append(ch);
		} else {
			bld.append("\\u");
			bld.append(String.format("%04x", (int)ch));
		}
		bld.append("'");
		bld.append(" -> ");
		bld.append(state);
		return bld.toString();
	}
	
}
