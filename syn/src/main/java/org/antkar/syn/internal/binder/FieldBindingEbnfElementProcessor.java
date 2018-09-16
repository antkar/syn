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
package org.antkar.syn.internal.binder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.antkar.syn.SynException;
import org.antkar.syn.SynValueType;
import org.antkar.syn.TerminalNode;
import org.antkar.syn.TextPos;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.TokenType;
import org.antkar.syn.ValueNode;
import org.antkar.syn.binder.DoubleToken;
import org.antkar.syn.binder.FloatToken;
import org.antkar.syn.binder.IntToken;
import org.antkar.syn.binder.LongToken;
import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynBinderException;
import org.antkar.syn.internal.CommonUtil;
import org.antkar.syn.internal.TokenTypeProcessor;
import org.antkar.syn.internal.ValueTypeProcessor;
import org.antkar.syn.internal.ebnf.EbnfElement;
import org.antkar.syn.internal.ebnf.EbnfElementProcessor;
import org.antkar.syn.internal.ebnf.EbnfNestedElement;
import org.antkar.syn.internal.ebnf.EbnfNonterminal;
import org.antkar.syn.internal.ebnf.EbnfNonterminalElement;
import org.antkar.syn.internal.ebnf.EbnfOptionalElement;
import org.antkar.syn.internal.ebnf.EbnfProduction;
import org.antkar.syn.internal.ebnf.EbnfProductions;
import org.antkar.syn.internal.ebnf.EbnfRepetitionElement;
import org.antkar.syn.internal.ebnf.EbnfTerminalElement;
import org.antkar.syn.internal.ebnf.EbnfValueElement;

/**
 * EBNF element processor used to determine the {@link BoundType} of an {@link EbnfElement}.
 */
final class FieldBindingEbnfElementProcessor implements EbnfElementProcessor<BoundType> {

    private final String ntName;
    private final Field field;
    private final Class<?> fieldType;

    private final Map<String, Class<?>> ntNameToNtClassMap;
    private final Map<String, List<Class<?>>> ntNameToAllowedClssMap;
    private final Map<Class<?>, Map<String, Class<?>>> ownerClsToOwnedClsMap;

    FieldBindingEbnfElementProcessor(
            String ntName,
            Field field,
            Map<String, Class<?>> ntNameToNtClassMap,
            Map<String, List<Class<?>>> ntNameToAllowedClssMap,
            Map<Class<?>, Map<String, Class<?>>> ownerClsToOwnedClsMap)
    {
        this.ntName = ntName;
        this.field = field;
        this.ntNameToNtClassMap = ntNameToNtClassMap;
        this.ntNameToAllowedClssMap = ntNameToAllowedClssMap;
        this.ownerClsToOwnedClsMap = ownerClsToOwnedClsMap;

        fieldType = field.getType();
    }

    @Override
    public BoundType processValueElement(EbnfValueElement element) throws SynException {
        final ValueNode valueNode = element.getValueNode();
        if (valueNode == null) {
            //Null value.
            return bindNullValue();
        }

        //Check the value type.
        SynValueType valueType = valueNode.getValueType();
        BoundType boundType = valueType.invokeProcessor(new ValueTypeProcessor<BoundType>() {
            @Override
            public BoundType processStringValue() throws SynException {
                checkType(fieldType, false, String.class);
                return StringBoundType.INSTANCE;
            }

            @Override
            public BoundType processObjectValue() throws SynException {
                return getBoundTypeForObjectValue(valueNode);
            }

            @Override
            public BoundType processIntegerValue() throws SynException {
                return getBoundTypeForIntegerValue(valueNode);
            }

            @Override
            public BoundType processFloatValue() throws SynException {
                return getBoundTypeForFloatValue();
            }

            @Override
            public BoundType processBooleanValue() throws SynException {
                return getBoundTypeForBooleanValue();
            }
        });

        return boundType;
    }

    /**
     * Returns a bound type for an arbitrary object value.
     */
    private BoundType getBoundTypeForObjectValue(ValueNode valueNode) throws SynException {
        Object value = valueNode.getValue();
        if (value == null) {
            return bindNullValue();
        }

        Class<?> actualType = value.getClass();
        checkType(fieldType, false, actualType);
        return ObjectBoundType.INSTANCE;
    }

    /**
     * Returns a bound type for an integer value.
     */
    private BoundType getBoundTypeForIntegerValue(ValueNode valueNode) throws SynException {
        long value = valueNode.getLong();
        if (int.class.equals(fieldType) || Integer.class.isAssignableFrom(fieldType)) {
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                throw new SynBinderException(String.format(
                        "Cannot bind value %d to field %s",
                        value, field));
            }
            return IntBoundType.INSTANCE;
        } else if (long.class.equals(fieldType) || Long.class.isAssignableFrom(fieldType)) {
            return LongBoundType.INSTANCE;
        } else {
            throw typeMissmatchFail(false, int.class, long.class, Integer.class, Long.class);
        }
    }

    /**
     * Returns a bound type for a floating-point value.
     */
    private BoundType getBoundTypeForFloatValue() throws SynException {
        if (float.class.equals(fieldType) || Float.class.isAssignableFrom(fieldType)) {
            return FloatBoundType.INSTANCE;
        } else if (double.class.equals(fieldType) || Double.class.isAssignableFrom(fieldType)) {
            return DoubleBoundType.INSTANCE;
        } else {
            throw typeMissmatchFail(false, float.class, double.class, Float.class, Double.class);
        }
    }

    /**
     * Returns a bound type for a boolean value.
     */
    private BoundType getBoundTypeForBooleanValue() throws SynException {
        checkType(fieldType, false, boolean.class, Boolean.class);
        return BooleanBoundType.INSTANCE;
    }

    @Override
    public BoundType processTerminalElement(EbnfTerminalElement element) throws SynException {
        return getBoundTypeForTerminalElement(element, fieldType, false);
    }

    @Override
    public BoundType processRepetitionElement(EbnfRepetitionElement element) throws SynException {
        EbnfProductions separatorProductions = element.getSeparator();
        if (separatorProductions != null) {
            verifyRepetitionElementSeparatorProductions(separatorProductions);
        }

        BoundType boundType = null;

        //Only a single terminal or nonterminal EBNF element must be defined in the repetition
        //element's body - a Binder limitation.
        EbnfProductions bodyProductions = element.getBody();
        EbnfElement bodyElement = getSingleElement(bodyProductions);

        if (bodyElement != null && bodyElement.getAttribute() == null) {
            boundType = getBoundTypeForRepetitionElementBody(bodyElement);
        }

        if (boundType == null) {
            throw new SynBinderException(String.format(
                    "Nonterminal %s: repetition element must contain a single terminal " +
                    "or nonterminal element without an attribute", ntName));
        }

        return boundType;
    }

    /**
     * Returns a bound type for a repetition element.
     */
    private BoundType getBoundTypeForRepetitionElementBody(EbnfElement bodyElement)
            throws SynException
    {
        if (bodyElement instanceof EbnfTerminalElement) {
            //Repetition of a terminal element.
            EbnfTerminalElement terminalElement = (EbnfTerminalElement) bodyElement;
            return getBoundTypeForRepetitionElementTerminalBody(terminalElement);
        } else if (bodyElement instanceof EbnfNonterminalElement) {
            //Repetition of a nonterminal element.
            EbnfNonterminalElement ntElement = (EbnfNonterminalElement) bodyElement;
            return getBoundTypeForRepetitionElementNonterminalBody(ntElement);
        }

        return null;
    }

    /**
     * Returns a bound type for a repetition element whose body is a terminal element.
     */
    private BoundType getBoundTypeForRepetitionElementTerminalBody(EbnfTerminalElement terminalElement)
            throws SynException
    {
        TokenDescriptor tokenDesc = terminalElement.getTokenDescriptor();
        TokenType tokenType = tokenDesc.getType();

        //Only a few types are supported.
        if (tokenType != TokenType.ID
                && tokenType != TokenType.STRING
                && tokenType != TokenType.INTEGER)
        {
            throw new SynBinderException(String.format(
                    "Nonterminal %s: token of type %s cannot be used in a repetition element",
                    ntName, tokenType));
        }

        //Choose the bound type depending on the type of the Java field.
        if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            BoundType boundType = getBoundTypeForTerminalElement(terminalElement, componentType, true);
            return boundType.getArrayType(field);
        } else if (fieldType.isAssignableFrom(Collection.class)) {
            BoundType boundType = getBoundTypeForTerminalElement(terminalElement, Object.class, true);
            return boundType.getListType();
        } else {
            throw new SynBinderException(String.format(
                    "Nonterminal %s: cannot bind a repetition element to field %s",
                    ntName, field));
        }
    }

    /**
     * Returns a bound type for a repetition element whose body is a nonterminal element.
     */
    private BoundType getBoundTypeForRepetitionElementNonterminalBody(EbnfNonterminalElement ntElement)
            throws SynBinderException
    {
        //Choose the bound type depending on the type of the Java field.
        if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            BoundType boundType = getBoundTypeForNonterminalElement(ntElement, componentType, true);
            return boundType.getArrayType(field);
        } else if (fieldType.isAssignableFrom(Collection.class)) {
            BoundType boundType = getBoundTypeForNonterminalElement(ntElement, Object.class, true);
            return boundType.getListType();
        } else {
            throw new SynBinderException(String.format(
                    "Nonterminal %s: cannot bind a repetition element to field %s",
                    ntName, field));
        }
    }

    /**
     * Checks whether the separator of a repetition element satisfies Binder limitations.
     */
    private void verifyRepetitionElementSeparatorProductions(EbnfProductions separatorProductions)
            throws SynBinderException
    {
        EbnfElement separatorElement = getSingleElement(separatorProductions);
        if (separatorElement instanceof EbnfTerminalElement) {
            if (separatorElement.getAttribute() == null) {
                return;
            }
        }

        //Defining an attribute inside of a separator does not make sense - the result of the production
        //will not be linked with the parent node.

        throw new SynBinderException(String.format(
                "Nonterminal %s: separator in a repetition element must be " +
                "a single terminal without an attribute", ntName));
    }

    @Override
    public BoundType processNonterminalElement(EbnfNonterminalElement element) throws SynException {
        return getBoundTypeForNonterminalElement(element, fieldType, false);
    }

    @Override
    public BoundType processNestedElement(EbnfNestedElement element) throws SynException {
        throw new SynBinderException(String.format(
                "Nonterminal %s has a nested element in its production. " +
                "Nested elements are not supported", ntName));
    }

    @Override
    public BoundType processOptionalElement(EbnfOptionalElement element) throws SynException {
        BoundType boundType = null;

        //Only a single EBNF element must be specified inside of an optional element - Binder limitation.
        EbnfProductions bodyProductions = element.getBody();
        EbnfElement bodyElement = getSingleElement(bodyProductions);
        if (bodyElement != null) {
            boundType = getBoundTypeForOptionalElementBody(bodyElement);
        }

        if (boundType == null) {
            throw new SynBinderException(String.format(
                    "Nonterminal %s: only a single terminal or nonterminal element " +
                    "is supported inside of an optional element", ntName));
        }

        return boundType;
    }

    /**
     * Returns a bound type for an optional element with the specified body element.
     */
    private BoundType getBoundTypeForOptionalElementBody(EbnfElement bodyElement) throws SynException {
        if (bodyElement.getAttribute() != null) {
            throw new SynBinderException(String.format(
                    "Nonterminal %s: an attribute is defined inside of an optional element", ntName));
        }

        if (bodyElement instanceof EbnfTerminalElement) {
            EbnfTerminalElement terminalElement = (EbnfTerminalElement) bodyElement;
            return getBoundTypeForTerminalElement(terminalElement, fieldType, false);
        } else if (bodyElement instanceof EbnfNonterminalElement) {
            EbnfNonterminalElement ntElement = (EbnfNonterminalElement) bodyElement;
            return getBoundTypeForNonterminalElement(ntElement, fieldType, false);
        }

        return null;
    }

    /**
     * Returns a bound type for a terminal element.
     */
    private BoundType getBoundTypeForTerminalElement(
            EbnfTerminalElement terminalElement,
            Class<?> type,
            boolean array) throws SynException
    {
        //Special cases: for particular Java field types, the kind of the terminal element does not matter.
        if (TextPos.class.equals(type)) {
            return TextPosBoundType.INSTANCE;
        } else if (TerminalNode.class.equals(type)) {
            return TerminalNodeBoundType.INSTANCE;
        }

        //Choose the bound type depending on the kind of the terminal element.
        return getBoundTypeForToken(terminalElement, type, array);
    }

    /**
     * Returns a bound type for a terminal element depending on the element's type.
     */
    private BoundType getBoundTypeForToken(
            EbnfTerminalElement terminalElement,
            final Class<?> type,
            final boolean array) throws SynException
    {
        TokenDescriptor tokenDesc = terminalElement.getTokenDescriptor();
        final TokenType tokenType = tokenDesc.getType();

        BoundType boundType = tokenType.invokeProcessor(new TokenTypeProcessor<BoundType>() {
            @Override
            public BoundType processStringLiteral() throws SynException {
                return getBoundTypeForTerminalStringLiteral(type, array);
            }

            @Override
            public BoundType processKeyword() throws SynException {
                return getBoundTypeForTerminalKeyword(type, array);
            }

            @Override
            public BoundType processKeyChar() throws SynException {
                return getBoundTypeForTerminalKeyword(type, array);
            }

            @Override
            public BoundType processIntegerLiteral() throws SynException {
                return getBoundTypeForTerminalIntegerLiteral(type, array);
            }

            @Override
            public BoundType processIdentifier() throws SynException {
                return getBoundTypeForTerminalStringLiteral(type, array);
            }

            @Override
            public BoundType processFloatingPointLiteral() throws SynException {
                return getBoundTypeForTerminalFloatingPointLiteral(type, array);
            }

            @Override
            public BoundType processEndOfFile() {
                throw new IllegalStateException("End Of File is not expected here");
            }
        });

        return boundType;
    }

    /**
     * Returns a bound type for a string literal.
     */
    private BoundType getBoundTypeForTerminalStringLiteral(Class<?> type, boolean array)
            throws SynException
    {
        checkType(type, array, String.class, StringToken.class);

        if (type.isAssignableFrom(StringToken.class)) {
            return StringBoundType.STRING_TOKEN_INSTANCE;
        } else {
            return StringBoundType.INSTANCE;
        }
    }

    /**
     * Returns a bound type for a keyword.
     */
    private BoundType getBoundTypeForTerminalKeyword(Class<?> type, boolean array)
            throws SynException
    {
        checkType(type, array, String.class, StringToken.class);

        if (type.isAssignableFrom(StringToken.class)) {
            return LiteralBoundType.STRING_TOKEN_INSTANCE;
        } else {
            return LiteralBoundType.INSTANCE;
        }
    }

    /**
     * Returns a bound type for an integer literal.
     */
    private BoundType getBoundTypeForTerminalIntegerLiteral(Class<?> type, boolean array)
            throws SynException
    {
        if (int.class.equals(type)) {
            return IntBoundType.INSTANCE;
        } else if (type.isAssignableFrom(Integer.class)) {
            return IntegerBoundType.INSTANCE;
        } else if (long.class.equals(type)) {
            return LongBoundType.INSTANCE;
        } else if (type.isAssignableFrom(Long.class)) {
            return LongWrapperBoundType.INSTANCE;
        } else if (type.isAssignableFrom(IntToken.class)) {
            return IntTokenBoundType.INSTANCE;
        } else if (type.isAssignableFrom(LongToken.class)) {
            return LongTokenBoundType.INSTANCE;
        } else {
            throw typeMissmatchFail(array, int.class, Integer.class);
        }
    }

    /**
     * Returns a bound type for a floating-point literal.
     */
    private BoundType getBoundTypeForTerminalFloatingPointLiteral(Class<?> type, boolean array)
            throws SynException
    {
        if (float.class.equals(type)) {
            return FloatBoundType.INSTANCE;
        } else if (type.isAssignableFrom(Float.class)) {
            return FloatWrapperBoundType.INSTANCE;
        } else if (double.class.equals(type)) {
            return DoubleBoundType.INSTANCE;
        } else if (type.isAssignableFrom(Double.class)) {
            return DoubleWrapperBoundType.INSTANCE;
        } else if (type.isAssignableFrom(FloatToken.class)) {
            return FloatTokenBoundType.INSTANCE;
        } else if (type.isAssignableFrom(DoubleToken.class)) {
            return DoubleTokenBoundType.INSTANCE;
        } else {
            throw typeMissmatchFail(array, float.class, double.class, Float.class, Double.class);
        }
    }

    /**
     * Returns a bound type for a nonterminal element.
     */
    private BoundType getBoundTypeForNonterminalElement(
            EbnfNonterminalElement nonterminalElement,
            Class<?> type,
            boolean array) throws SynBinderException
    {
        //Lookup the Java class associated with the nonterminal.
        EbnfNonterminal subNt = nonterminalElement.getNonterminal();
        String subNtName = subNt.getName();
        Class<?> cls = getClassForNt(subNtName);

        //Ensure that the Java class of the nonterminal is compatible with the Java field type.
        verifyNonterminalElementClass(subNtName, type, array, cls);

        //Remember object owner information.
        putOwnerIntoMap(field.getDeclaringClass(), field.getName(), cls);

        return new NonterminalBoundType(cls);
    }

    /**
     * Checks whether the given Java class is a valid value for the specified Java field.
     */
    private void verifyNonterminalElementClass(
            String subNtName,
            Class<?> type,
            boolean array,
            Class<?> cls) throws SynBinderException
    {
        if (!type.isAssignableFrom(cls)) {
            List<Class<?>> allowedClss = getAllowedClassesForNt(subNtName);
            List<String> wrongAllowedClsNames = new ArrayList<>();
            for (Class<?> allowedCls : allowedClss) {
                if (!type.isAssignableFrom(allowedCls)) {
                    wrongAllowedClsNames.add(allowedCls.getCanonicalName());
                }
            }
            Collections.sort(wrongAllowedClsNames);

            String message = String.format(
                    "Field %s has type %s%s, which is not compatible with %s%s",
                    field,
                    type.getCanonicalName(),
                    array ? "[]" : "",
                    cls.getCanonicalName(),
                    array ? "[]" : "");
            if (!type.isArray()) {
                message += String.format(
                        ". Make %s a superclass of %s",
                        type.getCanonicalName(),
                        wrongAllowedClsNames);
            }

            throw new SynBinderException(message);
        }
    }

    /**
     * Binds a <code>null</code> value to the field.
     */
    private BoundType bindNullValue() throws SynBinderException {
        if (fieldType.isPrimitive()) {
            throw new SynBinderException(String.format(
                    "Cannot bind null value to field %s", field));
        }

        return ObjectBoundType.INSTANCE;
    }

    private void putOwnerIntoMap(Class<?> owner, String fieldName, Class<?> owned) {
        CommonUtil.putToMapMap(ownerClsToOwnedClsMap, owner, fieldName, owned);
    }

    /**
     * Returns the Java class associated with the given nonterminal.
     */
    private Class<?> getClassForNt(String aNtName) {
        Class<?> cls = ntNameToNtClassMap.get(aNtName);
        if (cls == null) {
            throw new IllegalStateException(String.format(
                    "Class not found for nonterminal %s", aNtName));
        }
        return cls;
    }

    private List<Class<?>> getAllowedClassesForNt(String aNtName) {
        return ntNameToAllowedClssMap.get(aNtName);
    }

    /**
     * Checks whether a Java class is compatible with one of the given classes.
     */
    private void checkType(Class<?> type, boolean array, Class<?>... expectedTypes)
            throws SynBinderException
    {
        for (Class<?> expectedType : expectedTypes) {
            if (type.isAssignableFrom(expectedType)) {
                return;
            }
        }
        throw typeMissmatchFail(array, expectedTypes);
    }

    /**
     * Throws a type mismatch exception.
     */
    private SynBinderException typeMissmatchFail(boolean array, Class<?>... expectedTypes)
            throws SynBinderException
    {
        if (expectedTypes.length == 1) {
            //Single expected type.
            Class<?> expectedType = expectedTypes[0];
            throw new SynBinderException(String.format(
                    "Type of field %s must be %s%s",
                    field,
                    expectedType.getCanonicalName(),
                    array ? "[]" : ""));
        }

        //Many expected types.
        String[] names = new String[expectedTypes.length];
        for (int i = 0; i < expectedTypes.length; ++i) {
            names[i] = expectedTypes[i].getCanonicalName() + (array ? "[]" : "");
        }

        throw new SynBinderException(String.format(
                "Type of field %s must be one of the following: %s",
                field, Arrays.toString(names)));
    }

    /**
     * If the set of productions contains only one production and that production contains only one
     * element, returns that element. Otherwise, returns <code>null</code>.
     */
    private static EbnfElement getSingleElement(EbnfProductions productions) {
        List<EbnfProduction> productionsList = productions.asList();
        if (productionsList.size() == 1) {
            EbnfProduction production = productionsList.get(0);
            List<EbnfElement> elements = production.getElements();
            if (elements.size() == 1) {
                return elements.get(0);
            }
        }

        return null;
    }
}
