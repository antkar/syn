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
package com.karmant.syn.sample.script.schema;

import com.karmant.syn.StringToken;
import com.karmant.syn.SynField;
import com.karmant.syn.TextPos;

/**
 * A binary expression syntax node.
 */
public abstract class BinaryExpression extends Expression {
	/** Left operand. */
	@SynField
	private Expression synLeft;
	
	/** Right operand. */
	@SynField
	private Expression synRight;
	
	/** Operator. */
	@SynField
	private StringToken synOp;
	
	public BinaryExpression(){}
	
	@Override
	final TextPos getStartTextPos() {
		return synLeft.getStartTextPos();
	}

	/**
	 * Returns the left operand.
	 */
	final Expression getLeft() {
		return synLeft;
	}
	
	/**
	 * Returns the right operand.
	 */
	final Expression getRight() {
		return synRight;
	}
	
	/**
	 * Returns the operator literal.
	 */
	final String getOp() {
		String literal = synOp.getValue();
		return literal;
	}
	
	@Override
	public String toString() {
		return String.format("(%s %s %s)", synLeft, getOp(), synRight);
	}
}
