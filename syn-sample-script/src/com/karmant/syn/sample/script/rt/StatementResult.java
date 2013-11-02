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

import com.karmant.syn.sample.script.rt.value.Value;

/**
 * A result of a Script Language statement execution. Can be <code>break</code>,
 * <code>continue</code>, <code>return</code> (with an arbitrary return value, or without one),
 * or none.
 */
public class StatementResult {
	
	public static final StatementResult NONE = new StatementResult();
	public static final StatementResult BREAK = new BreakStatementResult();
	public static final StatementResult CONTINUE = new ContinueStatementResult();

	private StatementResult(){}
	
	/**
	 * Returns <code>true</code> if this is a <code>continue</code> result.
	 */
	public boolean isContinue() {
		return false;
	}

	/**
	 * Returns <code>true</code> if this is a <code>break</code> result.
	 */
	public boolean isBreak() {
		return false;
	}
	
	/**
	 * Returns <code>true</code> if this is a <code>return</code> result.
	 */
	public boolean isReturn() {
		return false;
	}
	
	/**
	 * Returns the return value. Throws an exception if this is not a <code>return</code> result.
	 */
	public Value getReturnValue() {
		throw new IllegalStateException();
	}
	
	@Override
	public String toString() {
		return "<none>";
	}
	
	/**
	 * Creates a <code>return</code> statement result object.
	 * @param returnValue the return value (can be a void value, but not <code>null</code>).
	 * @return the result object.
	 */
	public static StatementResult forReturn(Value returnValue) {
		assert returnValue != null;
		return new ReturnStatementResult(returnValue);
	}
	
	private static final class BreakStatementResult extends StatementResult {
		BreakStatementResult(){}
		
		@Override
		public boolean isBreak() {
			return true;
		}
		
		@Override
		public String toString() {
			return "break";
		}
	}
	
	private static final class ContinueStatementResult extends StatementResult {
		ContinueStatementResult(){}
		
		@Override
		public boolean isContinue() {
			return true;
		}
		
		@Override
		public String toString() {
			return "continue";
		}
	}
	
	private static final class ReturnStatementResult extends StatementResult {
		private final Value value;
		
		ReturnStatementResult(Value value) {
			this.value = value;
		}
		
		@Override
		public boolean isReturn() {
			return true;
		}
		
		@Override
		public Value getReturnValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return "return " + value;
		}
	}
}
