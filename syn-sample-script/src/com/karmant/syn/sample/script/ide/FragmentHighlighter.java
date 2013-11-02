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
package com.karmant.syn.sample.script.ide;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

/**
 * Highlights consequent fragments of a {@link StyledDocument} with specified text styles.
 * If the same style has to be used for two or more consequent fragments, it is set
 * in the {@link StyledDocument} by a single operation for the whole range occupied by the fragments.
 */
class FragmentHighlighter {
	private final StyledDocument doc;
	private int startPos;
	private int curPos;
	private AttributeSet curAttrs;
	
	FragmentHighlighter(StyledDocument doc, int pos) {
		this.doc = doc;
		startPos = pos;
		curPos = pos;
		curAttrs = null;
	}
	
	/**
	 * Highlights the specified number of characters with the specified style, starting from the
	 * current position. The current position is then moved forward by the same number of characters.
	 */
	void highlight(AttributeSet attrs, int length) {
		if (attrs == curAttrs) {
			curPos += length;
			return;
		}
		
		finish();
		
		curAttrs = attrs;
		startPos = curPos;
		curPos += length;
	}
	
	/**
	 * Called in the end of the highlighting process. Highlights the last fragment.
	 */
	void finish() {
		if (curAttrs != null && startPos < curPos) {
			doc.setCharacterAttributes(startPos, curPos - startPos, curAttrs, true);
		}
	}
}
