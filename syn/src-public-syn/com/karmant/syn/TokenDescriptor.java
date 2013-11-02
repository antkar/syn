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
 * Token descriptor. Exists for every terminal symbol used in a grammar. For each literal terminal
 * symbol (e. g. <code>ID</code>) there is a unique global token descriptor accessible via the
 * corresponding public static field (e. g. {@link TokenDescriptor#ID}).
 * Token descriptors for custom terminals (keywords, key-characters) are created when a {@link SynParser}
 * instance is created. 
 */
public class TokenDescriptor {
	private static final TokenDescriptor[] FOR_TYPE;
	
	static {
		TokenType[] values = TokenType.values();
		FOR_TYPE = new TokenDescriptor[values.length];
		for (int i = 0; i < values.length; ++i) {
			FOR_TYPE[i] = new TokenDescriptor(values[i]);
		}
	}

	/**
	 * End-of-file token descriptor. Used internally by the parser, but not included into the generated
	 * Abstract Syntax Tree which is returned to the client code.
	 */
	public static final TokenDescriptor END_OF_FILE = forType(TokenType.END_OF_FILE);
	
	/**
	 * Identifier token descriptor.
	 */
	public static final TokenDescriptor ID = forType(TokenType.ID);
	
	/**
	 * Integer literal token descriptor.
	 */
	public static final TokenDescriptor INTEGER = forType(TokenType.INTEGER);
	
	/**
	 * Floating-point literal token descriptor.
	 */
	public static final TokenDescriptor FLOAT = forType(TokenType.FLOAT);
	
	/**
	 * String literal token descriptor.
	 */
	public static final TokenDescriptor STRING = forType(TokenType.STRING);
	
	/**
	 * Token type. Cannot be <code>null</code>.
	 */
	private final TokenType type;
	
	/**
	 * Token's literal. Not <code>null</code> only for custom terminal symbols defined in a grammar.
	 */
	private final String literal;

	/**
	 * Constructs a token descriptor for a custom terminal symbol.
	 */
	private TokenDescriptor(TokenType type, String literal) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (literal == null) {
			throw new NullPointerException();
		}
		if (!type.isCustom()) {
			throw new IllegalArgumentException(
					"Cannot create a descriptor for not a custom token type: " + type);
		}
		this.type = type;
		this.literal = literal;
	}

	/**
	 * Constructs a token descriptor for a literal terminal symbol, specified by its token type.
	 */
	private TokenDescriptor(TokenType type) {
		this.type = type;
		this.literal = null;
	}
	
	/**
	 * Returns the token type of this token descriptor.
	 * @return the token type. Cannot be <code>null</code>.
	 */
	public TokenType getType() {
		return type;
	}
	
	/**
	 * Returns the literal of this token descriptor. Literal terminal symbols do not have literals,
	 * in that case the method returns <code>null</code>. For custom terminal symbols (keywords and
	 * key-characters), the literal is not <code>null</code>.
	 * 
	 * @return the literal.
	 */
	public String getLiteral() {
		return literal;
	}
	
	/**
	 * Returns a token descriptor for the passed token type. The type must describe a literal token
	 * (see {@link TokenType}). The method always returns the same instance of a token descriptor for
	 * the same token type.
	 *  
	 * @param tokenType the token type.
	 * @return the token descriptor.
	 * @throws NullPointerException if the passed token type is <code>null</code>.
	 * @throws IllegalArgumentException if the passed token type does not denote a literal token.
	 */
	public static TokenDescriptor forType(TokenType tokenType) {
		if (tokenType == null) {
			throw new NullPointerException("tokenType is null");
		}
		if (tokenType.isCustom()) {
			throw new IllegalArgumentException("The specified token type is custom: " + tokenType);
		}
		TokenDescriptor result = FOR_TYPE[tokenType.ordinal()];
		return result;
	}
	
	/**
	 * Returns a new token descriptor for a custom terminal symbol with the specified literal.
	 * 
	 * @param literal the terminal symbol literal. Cannot be <code>null</code>. Must be either an identifier,
	 * or a key-character, but not mixed.
	 * @return a new token descriptor instance for the passed literal.
	 * @throws NullPointerException if the passed literal is <code>null</code>.
	 * @throws IllegalArgumentException if the passed literal is not a valid literal.
	 */
	public static TokenDescriptor forLiteral(String literal) {
		if (literal == null) {
			throw new NullPointerException("literal");
		}
		if (literal.length() == 0) {
			throw new IllegalArgumentException("The specified literal has zero length");
		}
		TokenType tokenType;
		if (isKeywordLiteral(literal)) {
			tokenType = TokenType.KEYWORD;
		} else if (isKeycharLiteral(literal)) {
			tokenType = TokenType.KEYCHAR;
		} else {
			throw new IllegalArgumentException("Invalid literal: '" + literal + "'");
		}
		TokenDescriptor tokenDescriptor = new TokenDescriptor(tokenType, literal);
		return tokenDescriptor;
	}
	
	/**
	 * Different instances with equal types and literals are considered equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TokenDescriptor) {
			TokenDescriptor descriptor = (TokenDescriptor) obj;
			return descriptor.type == type && 
					(literal == null ? descriptor.literal == null : literal.equals(descriptor.literal));
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		int result = type.hashCode();
		if (literal != null) {
			result = result * 31 + literal.hashCode();
		}
		return result;
	}
	
	@Override
	public String toString() {
		String result;
		if (literal != null) {
			result = literalToString(literal);
		} else {
			result = type.toString();
		}
		return result;
	}
	
	/**
	 * Checks if the passed literal denotes a keyword token.
	 * 
	 * @param literal the literal.
	 * @return <code>true</code> if this is a keyword literal.
	 */
	static boolean isKeywordLiteral(String literal) {
		boolean result = literal.length() > 0;
		if (result) {
			result = Character.isJavaIdentifierStart(literal.charAt(0));
			for (int i = 1, n = literal.length(); i < n && result; ++i) {
				result = result && Character.isJavaIdentifierPart(literal.charAt(i));
			}
		}
		return result;
	}
	
	/**
	 * Checks if the passed literal denotes a key-character token.
	 * 
	 * @param literal the literal.
	 * @return <code>true</code> if this is a key-character literal.
	 */
	static boolean isKeycharLiteral(String literal) {
		boolean result = literal.length() > 0;
		if (result) {
			for (int i = 0, n = literal.length(); i < n && result; ++i) {
				char c = literal.charAt(i);
				result = result && !Character.isJavaIdentifierPart(c);
				result = result && !Character.isWhitespace(c);
			}
		}
		return result;
	}
	
	/**
	 * Converts a literal to a quoted string, replacing special and non-ASCII characters with escape
	 * sequences.
	 * 
	 * @param literal the literal.
	 * @return the string representation of the literal, enclosed in double quotes.
	 */
	static String literalToString(String literal) {
		StringBuilder bld = new StringBuilder();
		bld.append("\"");
		for (int i = 0, len = literal.length(); i < len; ++i) {
			char ch = literal.charAt(i);
			if (ch == '\n') {
				bld.append("\\n");
			} else if (ch == '\t') {
				bld.append("\\t");
			} else if (ch == '\b') {
				bld.append("\\b");
			} else if (ch == '\f') {
				bld.append("\\f");
			} else if (ch == '\r') {
				bld.append("\\r");
			} else if (ch == '\\') {
				bld.append("\\\\");
			} else if (ch == '"') {
				bld.append("\\\"");
			} else if (ch < 0x20) {
				bld.append("\\");
				bld.append(String.format("%03d", (int)ch));
			} else if (ch >= 0x100) {
				bld.append("\\u");
				bld.append(String.format("%04X", (int)ch));
			} else {
				bld.append(ch);
			}
		}
		bld.append("\"");
		String toString = bld.toString();
		return toString;
	}
}
