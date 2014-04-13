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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;


/**
 * <p>A parser for a particular grammar. Parses a text and returns an Abstract Syntax Tree (AST).
 * The parser uses the GLR algorithm and thus supports any context-free grammars. However, it may fail
 * to parse some ambiguous grammars.</p>
 * 
 * <p>Example:
 * <pre>
 * SynParser parser = new SynParser("@E : E '+' T | T; T : INTEGER;");
 * SynResult result = parser.parse("E", "1 + 2 + 3");</pre></p>
 * 
 * <h2>Grammar Structure</h2>
 * 
 * <p>The grammar is specified in an adapted EBNF format. It contains a set of <i>nonterminal definitions</i>:
 * <pre>
 * &#64;Foo : <i>SyntaxExpression1</i> ;
 * Bar : <i>SyntaxExpression2</i> ;</pre></p>
 * 
 * <p>A nonterminal definition consists of a nonterminal name and a <i>syntax expression</i>.
 * If "<code>@</code>" character precedes the nonterminal name, that nonterminal is considered a start
 * nonterminal symbol. More than one start nonterminal symbol can be defined in a single grammar.</p>
 * 
 * <p>Syntax expression consists of one or more alternative productions separated by "<code>|</code>" character:
 * <pre>
 * Foo : <i>Production1</i> | <i>Production2</i> | <i>Production3</i> ;</pre></p>
 * 
 * <p>Production is a sequence of <i>syntax elements</i>:
 * <pre>
 * Foo : <i>Element1</i> <i>Element2</i> <i>Element3</i> ;</pre>
 * 
 * Each element may be associated with an attribute:
 * <pre>
 * Foo : attr1=<i>Element1</i> attr2=<i>Element2</i> attr3=<i>Element3</i> ;</pre></p>
 * 
 * <h2>Return Value</h2>
 * 
 * <p>A nonterminal, production or syntax element may return a {@link SynNode syntax node}. The result value of
 * the parser is the value returned by the start nonterminal. The return value of a nonterminal
 * is the value returned by the production of that nonterminal which matched the input.</p> 
 * 
 * <p>The return value of a production is defined in the following way:
 * <ol>
 * <li>If the production contains no elements, it returns <code>null</code>.</li>
 * <li>If the production contains one or more elements, and if one of the elements is assigned
 * to a special <code>result</code> attribute, the production returns the value of that element.</li>
 * <li>If no elements are assigned to the <code>result</code> attribute, but at least one element is
 * assigned to any other attribute, the production returns an {@link ObjectNode} with values of attributed
 * elements mapped to corresponding keys.</li>
 * <li>If no elements are assigned to attributes, and there is only one element which is not a
 * <i>String literal element</i> (see below), the value of that element is returned.</li>
 * <li>Otherwise, the return value of the production is undefined.</li>
 * </ol>
 * </p>
 * 
 * <h2>Syntax Elements</h2>
 * 
 * <p>There are several kinds of syntax elements. Each syntax element has its own notation and returns a
 * particular type of syntax node.</p>
 *
 * <h3><i>Name element</i></h3>
 * 
 * <p>Can be either a name of a nonterminal symbol, or a name of a literal terminal symbol.
 * The syntax of literal terminal symbols is similar to Java, defined in
 * <a href="http://docs.oracle.com/javase/specs/jls/se5.0/html/lexical.html#3.10">
 * The Java Language Specification, Third Edition</a>. The return value is a {@link ValueNode}
 * of a corresponding type. Literal terminal symbols are:</p>
 * 
 * <table border="1">
 * <tr><th>Name</th><th>Description</th><th>Text Example</th></tr>
 * 
 * <tr>
 * <td><code>ID</code></td>
 * <td>Identifier, a sequence of letters and digits which does not start with a digit.</td>
 * <td><code>MyVariableName123</code></td>
 * </tr>
 * 
 * <tr>
 * <td><code>INTEGER</code></td>
 * <td>Integer number. Can be decimal, hexadecimal or octal.</td>
 * <td><code>
 * 12345<br/>
 * 0x12EF<br/>
 * 0765
 * </code></td>
 * </tr>
 * 
 * <tr>
 * <td><code>FLOAT</code></td>
 * <td>Floating-point number. Can be decimal or hexadecimal.</td>
 * <td><code>
 * 123.456<br/>
 * 123.4e10<br/>
 * 0xABC.DEFp5
 * </code></td>
 * </tr>
 * 
 * <tr>
 * <td><code>STRING</code></td>
 * <td>String literal enclosed in single or double quotes. Can contain escape sequences.</td>
 * <td><code>
 * 'Hello World!'<br/>
 * "Hello World!\n"
 * </code></td>
 * </tr>
 * </table>
 * 
 * <h3><i>String literal element</i></h3>
 * 
 * <p>A sequence of characters enclosed in single or double quotes. Defines either a custom keyword or
 * a key-character:</p>
 *
 * <table border="1">
 * <tr><th>Grammar Example</th><th>Meaning</th></tr>
 * 
 * <tr><td><code>"return"</code></td><td>Defines a keyword <code>return</code>.</td></tr>
 * <tr><td><code>"+="</code></td><td>Defines an operator <code>+=</code>.</td></tr>
 * </table>
 * 
 * <p>The sequence must be either an identifier (in which case the element defines a keyword),
 * or it must not contain identifier characters (letters, digits, etc.) at all.</p>
 * 
 * <h3><i>Nested element</i></h3>
 * 
 * <p>Syntax: <code>(</code> <i>syntax_expression</i> <code>)</code></p>
 * 
 * <p>Has the same effect as the inner expression. Useful for placing a set of alternative productions inside
 * of a single element:
 * <pre>
 * Nt : 'aaa' ( Foo | Bar ) 'bbb';</pre></p>
 * 
 * <h3><i>Optional element</i></h3>
 * 
 * <p>Syntax: <code>(</code> <i>syntax_expression</i> <code>)?</code></p>
 * 
 * <p>Matches zero or one occurrence of the specified syntax expression. Returns either the result of
 * the expression or <code>null</code>.</p>
 * 
 * <h3><i>Repetition element</i></h3>
 * 
 * <p>Syntax: <code>(</code> <i>syntax_expression</i> <code>)*</code></p>
 * 
 * <p>Matches zero or more occurrences of the specified syntax expression. Returns an {@link ArrayNode}
 * containing nodes returned by inner expressions.</p>
 * 
 * <p>Syntax: <code>(</code> <i>syntax_expression</i> <code>:</code> <i>separator_expression</i>
 * <code>)*</code></p>
 * 
 * <p>An optional separator expression may be specified. In this case the element matches zero or more
 * occurrences of <i>syntax_expression</i> separated by <i>separator_expression</i>. Examples:</p>
 * 
 * <table border="1">
 * <tr><th>Grammar Example</th><th>Text Example</th></tr>
 * 
 * <tr><td><code>(INTEGER)*</code></td><td><code>123 456 789</code></td></tr>
 * <tr><td><code>(ID : ",")*</code></td><td><code>aaa , bbb , ccc</code></td></tr>
 * </table>
 * 
 * <p>Syntax: <code>(</code> <i>syntax_expression</i> <code>)+</code><br/>
 * Syntax: <code>(</code> <i>syntax_expression</i> <code>:</code> <i>separator_expression</i> <code>)+</code></p>
 * 
 * <p>If "<code>+</code>" is used instead of "<code>*</code>" in a repetition element, the element requires
 * at least one occurrence of the expression.</p>
 * 
 * <h3><i>Constant element</i></h3>
 * 
 * <p>Syntax: <code>&lt;</code> <i>constant_value</i> <code>&gt;</code></p>
 * 
 * <p>The element matches nothing in the text, but returns a value that can be associated with a syntax tree
 * attribute. Possible values are:</p>
 * 
 * <table border="1">
 * <tr><th>Example</th><th>Description</th><th>Returns</th></tr>
 * <tr><td><code>&lt;12345&gt;</code></td><td>Integer value</td><td>{@link ValueNode} of integer type</td></tr>
 * <tr>
 * <td><code>&lt;123.456&gt;</code></td>
 * <td>Floating-point value</td>
 * <td>{@link ValueNode} of floating-point type</td>
 * </tr>
 * <tr><td><code>&lt;"Hello!"&gt;</code></td><td>String value</td><td>{@link ValueNode} of string type</td></tr>
 * <tr>
 * <td><code>&lt;true&gt;</code></td>
 * <td><code>true</code> boolean value</td>
 * <td>{@link ValueNode} of boolean type</td>
 * </tr>
 * <tr>
 * <td><code>&lt;false&gt;</code></td>
 * <td><code>false</code> boolean value</td>
 * <td>{@link ValueNode} of boolean type</td>
 * </tr>
 * <tr><td><code>&lt;null&gt;</code></td><td><code>null</code> value</td><td><code>null</code></td></tr>
 * 
 * <tr>
 * <td><code>&lt;java.lang.Thread.NORM_PRIORITY&gt;</code></td>
 * <td>Value of a Java class static field</td>
 * <td>{@link ValueNode} of either the corresponding type or of the object type</td>
 * </tr>
 * </table>
 */
public class SynParser {
	
	private ParserConfiguration parserConfig;
	private ScannerConfiguration scannerConfig;
	private boolean failOnAmbiguity = false;
	
	/**
	 * Constructs a parser, reading the grammar from the specified {@link Reader}.
	 * 
	 * @param grammarReader the grammar reader.
	 * @throws SynException if grammar processing fails.
	 * 
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(Reader grammarReader) throws SynException {
		this(grammarReader, null);
	}
	
	/**
	 * Constructs a parser, reading the grammar from the specified {@link Reader}.
	 * Grammar source descriptor is specified. 
	 * 
	 * @param grammarReader the grammar reader.
	 * @param grammarDescriptor the grammar descriptor. Can be <code>null</code>.
	 * @throws SynException if grammar processing fails.
	 */
	public SynParser(Reader grammarReader, SourceDescriptor grammarDescriptor) throws SynException {
		if (grammarReader == null) {
			throw new NullPointerException("grammarReader");
		}
		
		grammarDescriptor = CommonUtil.getSourceDescriptor("<grammar>", grammarDescriptor);
		init(grammarDescriptor, grammarReader);
	}
	
	/**
	 * Constructs a parser, reading the grammar from the specified {@link File}.
	 * 
	 * @param grammarFile the grammar file.
	 * @throws SynException if grammar processing fails.
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(File grammarFile) throws SynException {
		this(grammarFile, null);
	}

	/**
	 * Constructs a parser, reading the grammar from the specified {@link File}.
	 * Grammar source descriptor is specified.
	 * 
	 * @param grammarFile the grammar file.
	 * @param grammarDescriptor the grammar descriptor.
	 * @throws SynException if grammar processing fails.
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(File grammarFile, SourceDescriptor grammarDescriptor) throws SynException
	{
		if (grammarFile == null) {
			throw new NullPointerException("grammarFile");
		}
		
		grammarDescriptor = CommonUtil.getSourceDescriptor(grammarFile, grammarDescriptor);
		try {
			try (Reader reader = CommonUtil.openFileReader(grammarFile)) {
				init(grammarDescriptor, reader);
			}
		} catch (IOException e) {
			throw new SynException(e);
		}
	}

	/**
	 * Constructs a parser, reading the grammar from the specified class loader resource.
	 *
	 * @param resourceOrigin the class which the specified resource path is relative to.
	 * @param grammarResourcePath the path to the grammar resource.
	 * @throws SynException if grammar processing fails.
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(Class<?> resourceOrigin, String grammarResourcePath) throws SynException {
		this(resourceOrigin, grammarResourcePath, null);
	}
	
	/**
	 * Constructs a parser, reading the grammar from the specified class loader resource.
	 * Grammar source descriptor is specified.
	 *
	 * @param resourceOrigin the class which the specified resource path is relative to.
	 * @param grammarResourcePath the path to the grammar resource.
	 * @param grammarDescriptor the grammar descriptor.
	 * @throws SynException if grammar processing fails.
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(
			Class<?> resourceOrigin,
			String grammarResourcePath,
			SourceDescriptor grammarDescriptor) throws SynException
	{
		if (resourceOrigin == null) {
			throw new NullPointerException("resourceOrigin");
		}
		if (grammarResourcePath == null) {
			throw new NullPointerException("grammarResourcePath");
		}
		
		grammarDescriptor = CommonUtil.getSourceDescriptor(grammarResourcePath, grammarDescriptor);
		try {
			try (Reader reader = CommonUtil.openResourceReader(resourceOrigin, grammarResourcePath)) {
				init(grammarDescriptor, reader);
			}
		} catch (IOException e) {
			throw new SynException(e);
		}
	}

	/**
	 * Constructs a parser, reading the grammar from the specified {@link String}.
	 *
	 * @param grammar the grammar.
	 * @throws SynException if grammar processing fails.
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(String grammar) throws SynException {
		this(grammar, null);
	}

	/**
	 * Constructs a parser, reading the grammar from the specified {@link String}.
	 * Grammar source descriptor is specified.
	 *
	 * @param grammar the grammar.
	 * @param grammarDescriptor the grammar descriptor.
	 * @throws SynException if grammar processing fails.
	 * @see #SynParser(Reader, SourceDescriptor)
	 */
	public SynParser(String grammar, SourceDescriptor grammarDescriptor) throws SynException {
		if (grammar == null) {
			throw new NullPointerException("grammar");
		}
		
		grammarDescriptor = CommonUtil.getSourceDescriptor("<grammar>", grammarDescriptor);
		
		Reader reader = new StringReader(grammar);
		init(grammarDescriptor, reader);
	}
	
	/**
	 * Constructs a parser for a grammar passed in form of {@link EbnfGrammar}.
	 * 
	 * @param eGrammar the EBNF grammar.
	 * @throws SynException if grammar processing fails.
	 */
	SynParser(EbnfGrammar eGrammar) throws SynException {
		initEBNF(eGrammar);
	}

	/**
	 * Initializes the parser, reading the grammar from the specified reader.
	 */
	private void init(SourceDescriptor grammarDescriptor, Reader grammarReader) throws SynException {
		EbnfGrammar eGrammar = SynGrammarParser.parseGrammar(grammarReader, grammarDescriptor);
		initEBNF(eGrammar);
	}

	/**
	 * Initializes the parser by the specified EBNF grammar.
	 */
	private void initEBNF(EbnfGrammar eGrammar) throws SynException {
		BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);
		
		parserConfig = ParserConfigurator.makeConfiguration(bGrammar);
		List<TokenDescriptor> tokenDescriptors = parserConfig.getTokenDescriptors();
		scannerConfig = ScannerConfigurator.makeConfiguration(tokenDescriptors);
	}

	/**
	 * <p>Parses the specified text starting with the specified nonterminal. The text is read from
	 * the specified {@link Reader}.</p>
	 * 
	 * <p>This method is thread-safe, meaning that different inputs can be safely parsed concurrently
	 * by the same parser instance. However, this method should not be called concurrently with
	 * {@link #setFailOnAmbiguity(boolean)}; in such case it is undefined whether the parser will take
	 * the option into account.</p>
	 * 
	 * <p>If an ambiguity is detected and the fail-on-ambiguity option is turned on,
	 * {@link SynAmbiguityException} is thrown. Otherwise, the parser chooses one of conflicting syntax trees
	 * and declines the others. The survivor tree is chosen in a way that correctly solves the dangling-else
	 * ambiguity, though the exact algorithm of choosing a tree is left undocumented.</p>
	 * 
	 * @param startNonterminal the name of the start nonterminal. The nonterminal must be defined in the
	 * grammar with a "<code>@</code>" mark.
	 * @param textReader the text reader.
	 * @return the Abstract Syntax Tree.
	 * @throws SynAmbiguityException if ambiguity is detected, while the fail-on-ambiguity option is on.
	 * @throws SynException if parsing fails.
	 */
	public SynResult parse(String startNonterminal, Reader textReader) throws SynException {
		return parse(startNonterminal, textReader, null);
	}

	/**
	 * Parses the specified text starting with the specified nonterminal. The text is read from
	 * the specified {@link Reader}. Text source descriptor is specified..
	 * 
	 * @param startNonterminal the name of the start nonterminal.
	 * @param textReader the text reader.
	 * @param textDescriptor the text descriptor. Can be <code>null</code>.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(
			String startNonterminal,
			Reader textReader,
			SourceDescriptor textDescriptor) throws SynException
	{
		if (textReader == null) {
			throw new NullPointerException("textReader");
		}
		if (startNonterminal == null) {
			throw new NullPointerException("startNonterminal");
		}
		
		ParserState startState = parserConfig.getStartState(startNonterminal);
		if (startState == null) {
			throw new SynException("Unknown start nonterminal: " + startNonterminal);
		}
		
		DefaultTokenStream tokenStream = createTokenStream(textReader, textDescriptor);

		ParserEngine parserEngine = new ParserEngine(tokenStream, parserConfig, startState, failOnAmbiguity);
		SynResult result = parserEngine.parse();
		return result;
	}
	
	/**
	 * Parses the text read from the specified class loader resource.
	 * 
	 * @param startNonterminal the start nonterminal name.
	 * @param resourceOrigin the class which the specified resource path is relative to.
	 * @param textResourcePath the text resource path.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(String startNonterminal, Class<?> resourceOrigin, String textResourcePath)
			throws SynException
	{
		return parse(startNonterminal, resourceOrigin, textResourcePath, null);
	}

	/**
	 * Parses the text read from the specified class loader resource.
	 * Text source descriptor is specified.
	 * 
	 * @param startNonterminal the start nonterminal name.
	 * @param resourceOrigin the class which the specified resource path is relative to.
	 * @param textResourcePath the text resource path.
	 * @param sourceDescriptor the text source descriptor.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(
			String startNonterminal,
			Class<?> resourceOrigin,
			String textResourcePath,
			SourceDescriptor sourceDescriptor) throws SynException
	{
		if (resourceOrigin == null) {
			throw new NullPointerException("resourceOrigin");
		}
		if (textResourcePath == null) {
			throw new NullPointerException("textResourcePath");
		}
		
		sourceDescriptor = CommonUtil.getSourceDescriptor(textResourcePath, sourceDescriptor);
		try {
			try (Reader reader = CommonUtil.openResourceReader(resourceOrigin, textResourcePath)) {
				return parse(startNonterminal, reader, sourceDescriptor);
			}
		} catch (IOException e) {
			throw new SynException(e);
		}
	}
	
	/**
	 * Parses the text read from the specified {@link File}.
	 * 
	 * @param startNonterminal the start nonterminal name.
	 * @param file the file to read the text from.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(String startNonterminal, File file) throws SynException {
		return parse(startNonterminal, file, null);
	}
	
	/**
	 * Parses the text read from the specified {@link File}.
	 * Text source descriptor is specified.
	 * 
	 * @param startNonterminal the start nonterminal name.
	 * @param file the file to read the text from.
	 * @param sourceDescriptor the text source descriptor.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(String startNonterminal, File file, SourceDescriptor sourceDescriptor)
			throws SynException
	{
		if (file == null) {
			throw new NullPointerException("file");
		}
		
		sourceDescriptor = CommonUtil.getSourceDescriptor(file, sourceDescriptor);
		try {
			try (Reader reader = CommonUtil.openFileReader(file)) {
				return parse(startNonterminal, reader, sourceDescriptor);
			}
		} catch (IOException e) {
			throw new SynException(e);
		}
	}
	
	/**
	 * Parses the text passed as a {@link String}.
	 * 
	 * @param startNonterminal the start nonterminal name.
	 * @param text the text.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(String startNonterminal, String text) throws SynException {
		return parse(startNonterminal, text, null);
	}
	
	/**
	 * Parses the text passed as a {@link String}.
	 * 
	 * @param startNonterminal the start nonterminal name.
	 * @param text the text.
	 * @param sourceDescriptor the text source descriptor.
	 * @return the Abstract Syntax Tree.
	 * @throws SynException if parsing fails.
	 * 
	 * @see #parse(String, Reader)
	 */
	public SynResult parse(
			String startNonterminal,
			String text,
			SourceDescriptor sourceDescriptor) throws SynException
	{
		if (text == null) {
			throw new NullPointerException("text");
		}
		
		sourceDescriptor = CommonUtil.getSourceDescriptor("<text>", sourceDescriptor);
		Reader reader = new StringReader(text);
		return parse(startNonterminal, reader, sourceDescriptor);
	}
	
	/**
	 * Sets the fail-on-ambiguity option. When the value is <code>true</code>, the parser throws
	 * {@link SynAmbiguityException} if it detects an ambiguity. The option is <code>false</code> by default.
	 * 
	 * @param failOnAmbiguity the value of the option.
	 */
	public void setFailOnAmbiguity(boolean failOnAmbiguity) {
		this.failOnAmbiguity = failOnAmbiguity;
	}
	
	/**
	 * Creates a token stream for the specified input. Except syntax analysis, the returned stream may be useful
	 * also for such tasks as syntax coloring, since it can recognize keywords and key-characters defined in the
	 * grammar.
	 * 
	 * @param textDescriptor the text descriptor. Can be <code>null</code>.
	 * @param reader the text reader.
	 * @return the token stream.
	 * @throws SynException if stream reading fails.
	 */
	public DefaultTokenStream createTokenStream(Reader reader, SourceDescriptor textDescriptor) throws SynException {
		textDescriptor = CommonUtil.getSourceDescriptor("<text>", textDescriptor);
		return new DefaultTokenStream(textDescriptor, scannerConfig, reader);
	}
}
