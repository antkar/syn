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
package org.antkar.syn.binder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.StringSourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.SynNode;
import org.antkar.syn.SynParser;
import org.antkar.syn.SynResult;
import org.antkar.syn.TokenStream;
import org.antkar.syn.internal.CommonUtil;
import org.antkar.syn.internal.binder.BinderConfiguration;
import org.antkar.syn.internal.binder.BinderConfigurator;
import org.antkar.syn.internal.binder.BinderConfiguratorResult;
import org.antkar.syn.internal.binder.BinderEngine;
import org.antkar.syn.internal.ebnf.EbnfGrammar;
import org.antkar.syn.internal.grammar.SynGrammarParser;

/**
 * <p>Binder - an add-on to {@link SynParser} that maps nonterminals and grammar attributes to client-supplied
 * Java classes and fields, correspondingly.</p>
 *
 * <p>Consider a fragment of a SYN grammar:
 * <pre>
 * IfStatement
 *     :   "if" "(" condition=Expression ")" statement=Statement
 *         ("else" elseStatement=Statement)?
 *     ;</pre>
 * and a Java class:
 * <pre>
 * public class IfStatement {
 *     {@link SynField &#64;SynField}
 *     private Expression condition;
 *
 *     {@link SynField &#64;SynField}
 *     private Statement statement;
 *
 *     {@link SynField &#64;SynField}
 *     private Statement elseStatement;
 * }</pre>
 * {@link SynBinder} will map the <code>IfStatement</code> nonterminal to the Java class with the same name,
 * mapping its attributes to corresponding class fields. The result of parsing will then be
 * a tree consisting of client-supplied classes instead of less convenient {@link SynNode}s.
 * </p>
 *
 * @param <T> the class of the start nonterminal.
 *
 * @see SynField
 * @see SynInit
 */
public final class SynBinder<T> {

    private SynParser synParser;
    private String startNonterminal;
    private BinderConfiguration<T> binderConfig;

    /**
     * Constructs a binder for the specified root class and the grammar provided via a reader.
     *
     * @param classToBind the class of the start nonterminal. There must be a nonterminal with the same name
     * defined in the grammar. Classes for other nonterminals must be located in the same package as the
     * passed class.
     * @param grammarReader the grammar reader.
     *
     * @throws IllegalArgumentException if one of parameters is <code>null</code>.
     * @throws SynBinderException if the grammar does not satisfy Binder limitations.
     * @throws SynException if the grammar is invalid.
     */
    public SynBinder(Class<T> classToBind, Reader grammarReader) throws SynException {
        this(classToBind, grammarReader, new StringSourceDescriptor("<grammar>"));
    }

    /**
     * Constructs a binder for the specified root class and the grammar provided via a reader, specifying
     * additionally the grammar source descriptor.
     *
     * @param classToBind the class of the start nonterminal.
     * @param grammarReader the grammar reader.
     * @param grammarDescriptor the grammar source descriptor.
     *
     * @throws NullPointerException if the class or the reader is <code>null</code>.
     * @throws SynBinderException if the grammar does not satisfy Binder limitations.
     * @throws SynException if the grammar is invalid.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(Class<T> classToBind, Reader grammarReader, SourceDescriptor grammarDescriptor)
            throws SynException
    {
        if (classToBind == null) {
            throw new NullPointerException("classToBind");
        }
        if (grammarReader == null) {
            throw new NullPointerException("grammarReader");
        }

        grammarDescriptor = CommonUtil.getSourceDescriptor("<grammar>", grammarDescriptor);

        init(classToBind, grammarReader, grammarDescriptor);
    }

    /**
     * Constructs a binder, reading the grammar from the specified {@link File}.
     *
     * @param classToBind the class of the start nonterminal.
     * @param grammarFile the grammar file.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(Class<T> classToBind, File grammarFile) throws SynException {
        this(classToBind, grammarFile, null);
    }

    /**
     * Constructs a binder, reading the grammar from the specified {@link File}.
     * Grammar source descriptor is specified.
     *
     * @param classToBind the class of the start nonterminal.
     * @param grammarFile the grammar file.
     * @param grammarDescriptor the grammar source descriptor.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(Class<T> classToBind, File grammarFile, SourceDescriptor grammarDescriptor)
            throws SynException
    {
        grammarDescriptor = CommonUtil.getSourceDescriptor(grammarFile, grammarDescriptor);
        try {
            try (Reader reader = CommonUtil.openFileReader(grammarFile)) {
                init(classToBind, reader, grammarDescriptor);
            }
        } catch (IOException e) {
            throw new SynException(e);
        }
    }

    /**
     * Constructs a binder, reading the grammar from the specified class loader resource.
     *
     * @param classToBind the class of the start nonterminal.
     * @param resourceOrigin the class which the specified resource path is relative to.
     * @param grammarResourcePath the grammar resource path.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(Class<T> classToBind, Class<?> resourceOrigin, String grammarResourcePath)
            throws SynException
    {
        this(classToBind, resourceOrigin, grammarResourcePath, null);
    }

    /**
     * Constructs a binder, reading the grammar from the specified class loader resource.
     * Grammar source descriptor is specified.
     *
     * @param classToBind the class of the start nonterminal.
     * @param resourceOrigin the class which the specified resource path is relative to.
     * @param grammarResourcePath the grammar resource path.
     * @param grammarDescriptor the grammar source descriptor.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(
            Class<T> classToBind,
            Class<?> resourceOrigin,
            String grammarResourcePath,
            SourceDescriptor grammarDescriptor) throws SynException
    {
        grammarDescriptor = CommonUtil.getSourceDescriptor(grammarResourcePath, grammarDescriptor);
        try {
            try (Reader reader =
                    CommonUtil.openResourceReader(classToBind, grammarResourcePath))
            {
                init(classToBind, reader, grammarDescriptor);
            }
        } catch (IOException e) {
            throw new SynException(e);
        }
    }

    /**
     * Constructs a binder, reading the grammar from the specified {@link String}.
     *
     * @param classToBind the class of the start nonterminal.
     * @param grammar the grammar.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(Class<T> classToBind, String grammar) throws SynException {
        this(classToBind, grammar, null);
    }

    /**
     * Constructs a binder, reading the grammar from the specified {@link String}.
     * Grammar source desriptor is specified.
     *
     * @param classToBind the class of the start nonterminal.
     * @param grammar the grammar.
     * @param grammarDescriptor the grammar source descriptor.
     *
     * @see #SynBinder(Class, Reader)
     */
    public SynBinder(Class<T> classToBind, String grammar, SourceDescriptor grammarDescriptor)
            throws SynException
    {
        grammarDescriptor = CommonUtil.getSourceDescriptor("<grammar>", grammarDescriptor);
        Reader grammarReader = new StringReader(grammar);
        init(classToBind, grammarReader, grammarDescriptor);
    }

    /**
     * Initializes the binder.
     */
    private void init(
            Class<T> classToBind,
            Reader grammarReader,
            SourceDescriptor grammarDescriptor) throws SynException
    {
        //Use the class' name as the start nonterminal name.
        this.startNonterminal = classToBind.getSimpleName();

        //First, load the EBNF representation of the passed grammar.
        EbnfGrammar eGrammar = SynGrammarParser.parseGrammar(grammarReader, grammarDescriptor);

        //Create a binder configuration.
        BinderConfiguratorResult<T> configResult = BinderConfigurator.makeConfiguration(classToBind, eGrammar);
        this.binderConfig = configResult.getConfig();

        //Create a SynParser instance for the modified EBNF grammar.
        EbnfGrammar genGrammar = configResult.getGenGrammar();
        this.synParser = new SynParser(genGrammar);
    }

    /**
     * Parses a text and maps it to Java objects of client-provided classes.
     * The text is read from the specified {@link Reader}.
     *
     * @param textReader the text reader.
     *
     * @return the root node of the tree. Can be <code>null</code>, depending on the grammar.
     *
     * @throws SynBinderException if Java objects instantiation or initialization fails.
     * @throws SynException if parsing fails.
     */
    public T parse(Reader textReader) throws SynException {
        return parse(textReader, null);
    }

    /**
     * Parses a text and maps it to Java objects of client-provided classes.
     * The text is read from the specified {@link Reader}.
     * Text source descriptor is specified.
     *
     * @param textReader the text reader.
     * @param textDescriptor the input descriptor.
     *
     * @return the root node of the tree. Can be <code>null</code>, depending on the grammar.
     *
     * @throws SynBinderException if Java objects instantiation or initialization fails.
     * @throws SynException if parsing fails.
     *
     * @see #parse(Reader)
     */
    public T parse(Reader textReader, SourceDescriptor textDescriptor) throws SynException {
        if (textReader == null) {
            throw new NullPointerException("textReader");
        }

        SynResult synResult = synParser.parse(startNonterminal, textReader, textDescriptor);
        SynNode rootNode = synResult.getRootNode();
        T result = BinderEngine.createObjects(binderConfig, rootNode);

        return result;
    }

    /**
     * Parses the text read from the specified class loader resource.
     *
     * @param resourceOrigin the class which the specified resource path is relative to.
     * @param textResourcePath the text resource path.
     *
     * @return the root node.
     *
     * @see #parse(Reader)
     */
    public T parse(Class<?> resourceOrigin, String textResourcePath) throws SynException {
        return parse(resourceOrigin, textResourcePath, null);
    }

    /**
     * Parses the text read from the specified class loader resource.
     * Text source descriptor is specified.
     *
     * @param resourceOrigin the class which the specified resource path is relative to.
     * @param textResourcePath the text resource path.
     * @param sourceDescriptor the text source descriptor.
     *
     * @return the root node.
     *
     * @see #parse(Reader)
     */
    public T parse(
            Class<?> resourceOrigin,
            String textResourcePath,
            SourceDescriptor sourceDescriptor) throws SynException
    {
        sourceDescriptor = CommonUtil.getSourceDescriptor(textResourcePath, sourceDescriptor);
        try {
            try (Reader reader = CommonUtil.openResourceReader(resourceOrigin, textResourcePath)) {
                return parse(reader, sourceDescriptor);
            }
        } catch (IOException e) {
            throw new SynException(e);
        }
    }

    /**
     * Parses the text read from the specified {@link File}.
     *
     * @param file the text file.
     *
     * @return the root node.
     *
     * @see #parse(Reader)
     */
    public T parse(File file) throws SynException {
        return parse(file, null);
    }

    /**
     * Parses the text read from the specified {@link File}. Text source descriptor is specified.
     *
     * @param file the text file.
     * @param sourceDescriptor the text source descriptor.
     *
     * @return the root node.
     *
     * @see #parse(Reader)
     */
    public T parse(File file, SourceDescriptor sourceDescriptor) throws SynException {
        sourceDescriptor = CommonUtil.getSourceDescriptor(file, sourceDescriptor);
        try {
            try (Reader reader = CommonUtil.openFileReader(file)) {
                return parse(reader, sourceDescriptor);
            }
        } catch (IOException e) {
            throw new SynException(e);
        }
    }

    /**
     * Parses the text passed in the specified {@link String}.
     *
     * @param text the text.
     *
     * @return the root node.
     *
     * @see #parse(Reader)
     */
    public T parse(String text) throws SynException {
        return parse(text, null);
    }

    /**
     * Parses the text passed in the specified {@link String}.
     * Text source descriptor is specified.
     *
     * @param text the text.
     * @param sourceDescriptor the text source descriptor.
     *
     * @return the root node.
     *
     * @see #parse(Reader)
     */
    public T parse(String text, SourceDescriptor sourceDescriptor) throws SynException {
        sourceDescriptor = CommonUtil.getSourceDescriptor("<text>", sourceDescriptor);
        Reader reader = new StringReader(text);
        return parse(reader, sourceDescriptor);
    }

    /**
     * Creates a token stream for the specified input.
     *
     * @param textDescriptor a text descriptor. Can be <code>null</code>.
     * @param reader the text reader.
     * @return the token stream.
     * @throws SynException if stream reading fails.
     *
     * @see SynParser#createTokenStream(Reader, SourceDescriptor)
     */
    public TokenStream createTokenStream(SourceDescriptor textDescriptor, Reader reader) throws SynException {
        return synParser.createTokenStream(reader, textDescriptor);
    }
}
