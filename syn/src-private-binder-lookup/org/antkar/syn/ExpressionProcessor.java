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
package org.antkar.syn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antkar.syn.ArrayNode;
import org.antkar.syn.CommonUtil;
import org.antkar.syn.ObjectNode;
import org.antkar.syn.SynBinderException;
import org.antkar.syn.SynField;
import org.antkar.syn.SynLookup;
import org.antkar.syn.SynNode;

/**
 * This class is responsible for creation of a {@link LookupExpression} object from an
 * Abstract Syntax Tree of a lookup expression.
 */
class ExpressionProcessor {

    private final Class<?> clsOfThis;
    private final Class<?> clsOfObj;
    private final Map<Class<?>, Map<String, Field>> clsToSynFldMap;
    private final Map<Class<?>, Map<String, Field>> clsToLookupFldMap;
    private final Map<Class<?>, Class<?>> clsToOwnerMap;

    private ExpressionProcessor(
            Class<?> clsOfThis,
            Class<?> clsOfObj,
            Map<Class<?>, Map<String, Field>> clsToSynFldMap,
            Map<Class<?>, Map<String, Field>> clsToLookupFldMap,
            Map<Class<?>, Class<?>> clsToOwnerMap)
    {
        this.clsOfThis = clsOfThis;
        this.clsOfObj = clsOfObj;
        this.clsToSynFldMap = clsToSynFldMap;
        this.clsToLookupFldMap = clsToLookupFldMap;
        this.clsToOwnerMap = clsToOwnerMap;
    }

    /**
     * Creates a {@link LookupExpression} object from the specified AST. 
     */
    static LookupExpression processExpression(
            Class<?> clsOfThis,
            Class<?> clsOfObj,
            Map<Class<?>, Map<String, Field>> clsToSynFldMap,
            Map<Class<?>, Map<String, Field>> clsToLookupFldMap,
            Map<Class<?>, Class<?>> clsToOwnerMap,
            SynNode rootNode) throws SynBinderException
    {
        ExpressionProcessor processor = new ExpressionProcessor(
                clsOfThis,
                clsOfObj,
                clsToSynFldMap,
                clsToLookupFldMap,
                clsToOwnerMap);
        LookupExpression expression = processor.processAndExpression(rootNode);
        return expression;
    }

    /**
     * Processes a lookup AND expression. 
     */
    private LookupExpression processAndExpression(SynNode rootNode) throws SynBinderException {
        ObjectNode rootObjNode = (ObjectNode) rootNode;
        ArrayNode relExpressionsNode = (ArrayNode) rootObjNode.get("relExpressions");
        
        List<LookupRelExpression> relExpressions = new ArrayList<>();
        for (SynNode relExpressionNode : relExpressionsNode) {
            LookupRelExpression relExpression = processRelExpression(relExpressionNode);
            relExpressions.add(relExpression);
        }
        
        LookupExpression andExpression = new LookupAndExpression(relExpressions);
        return andExpression;
    }

    /**
     * Processes a lookup relative expression.
     */
    private LookupRelExpression processRelExpression(SynNode relExpressionNode)
            throws SynBinderException
    {
        ObjectNode relExpressionObjNode = (ObjectNode) relExpressionNode;
        String op = relExpressionObjNode.getString("op");
        ObjectNode leftNode = (ObjectNode) relExpressionObjNode.get("left");
        ObjectNode rightNode = (ObjectNode) relExpressionObjNode.get("right");
        
        LookupTermExpression left = processTermExpression(leftNode);
        LookupTermExpression right = processTermExpression(rightNode);
        
        LookupEqualityChecker equalityChecker = getEqualityChecker(left, right);
        LookupRelExpression relExpression = createRelExpression(op, left, right, equalityChecker);
        
        return relExpression;
    }

    /**
     * Creates a lookup relation expression object.
     */
    private LookupRelExpression createRelExpression(
            String op,
            LookupTermExpression left,
            LookupTermExpression right,
            LookupEqualityChecker equalityChecker)
    {
        if ("==".equals(op)) {
            return new LookupEqExpression(equalityChecker, left, right);
        } else if ("!=".equals(op)) {
            return new LookupNeExpression(equalityChecker, left, right);
        } else {
            throw new IllegalStateException(op);
        }
    }
    
    /**
     * Returns an equality checker for specified expression nodes.
     */
    private LookupEqualityChecker getEqualityChecker(
            LookupTermExpression left,
            LookupTermExpression right) throws SynBinderException
    {
        Class<?> leftCls = left.getClassOfValue();
        Class<?> rightCls = right.getClassOfValue();
        
        boolean leftBound = clsToSynFldMap.containsKey(leftCls);
        boolean rightBound = clsToSynFldMap.containsKey(rightCls);
        
        if (leftBound && rightBound) {
            if (leftCls.isAssignableFrom(rightCls) || rightCls.isAssignableFrom(leftCls)) {
                return BindingObjectsEqualityChecker.INSTANCE;
            }
        } else if (!leftBound && !rightBound) {
            if (leftCls.equals(rightCls)) {
                if (String.class.equals(leftCls)) {
                    return StringEqualityChecker.INSTANCE;
                }
            }
        }
        
        throw new SynBinderException(String.format(
                "Cannot compare %s with %s",
                leftCls.getCanonicalName(), rightCls.getCanonicalName()));
    }

    /**
     * Processes a terminal lookup expression.
     */
    private LookupTermExpression processTermExpression(ObjectNode node) throws SynBinderException {
        String type = node.getString("type");
        
        if ("id".equals(type)) {
            return processIdExpression(node);
        } else if ("field".equals(type)) {
            return processFieldExpression(node);
        } else {
            throw new IllegalStateException(type);
        }
    }

    /**
     * Processes an identifier lookup expression.
     */
    private LookupTermExpression processIdExpression(ObjectNode node) throws SynBinderException {
        final String sThis = "this";
        final String sObj = "obj";
        
        String name = node.getString("name");
        if (sThis.equals(name)) {
            return new LookupThisExpression(clsOfThis);
        } else if (sObj.equals(name)) {
            return new LookupObjExpression(clsOfObj);
        } else {
            throw new SynBinderException(String.format(
                    "Name '%s' cannot be used as an origin. Only '%s' or '%s' can",
                    name, sThis, sObj));
        }
    }

    /**
     * Processes a field access lookup expression.
     */
    private LookupTermExpression processFieldExpression(ObjectNode node) throws SynBinderException {
        ObjectNode baseExpressionNode = (ObjectNode) node.get("baseExpression");
        String name = node.getString("name");
        
        LookupTermExpression baseExpression = processTermExpression(baseExpressionNode);
        
        Class<?> baseCls = baseExpression.getClassOfValue();
        if (!clsToSynFldMap.containsKey(baseCls)) {
            throw new SynBinderException(String.format(
                    "Cannot get field '%s' from expression '%s', because its type is %s",
                    name, baseExpression.toSourceString(), baseCls.getCanonicalName()));
        }
        
        LookupTermExpression expression;
        if ("owner".equals(name)) {
            expression = processOwnerExpression(baseExpression, baseCls, name);
        } else {
            expression = processRegularFieldExpression(baseExpression, baseCls, name);
        }
        return expression;
    }
    
    /**
     * Processes an "owner" lookup expression.
     */
    private LookupTermExpression processOwnerExpression(
            LookupTermExpression baseExpression,
            Class<?> baseCls,
            String kwOwner) throws SynBinderException
    {
        verifyOwnerFieldConflict(baseCls, kwOwner);
        
        Class<?> ownerCls = clsToOwnerMap.get(baseCls);
        if (ownerCls == null) {
            throw new SynBinderException(String.format(
                    "Class %s has no owner class",
                    baseCls.getCanonicalName()));
        }
        
        LookupTermExpression expression = new LookupOwnerExpression(ownerCls, baseExpression);
        return expression;
    }

    /**
     * Checks whether there is a field "owner" in the specified class, so that that field conflicts
     * with the "owner" special name.
     */
    private void verifyOwnerFieldConflict(Class<?> baseCls, String kwOwner) throws SynBinderException {
        Class<?> curCls = baseCls;
        while (curCls != null) {
            Field fld = CommonUtil.getFromMapMap(clsToLookupFldMap, curCls, kwOwner);
            if (fld == null) {
                fld = CommonUtil.getFromMapMap(clsToSynFldMap, curCls, kwOwner);
            }
            if (fld != null) {
                throw new SynBinderException(String.format(
                        "Cannot get owner of class %s, because there is a field %s",
                        curCls.getCanonicalName(), fld));
            }
            curCls = curCls.getSuperclass();
        }
    }

    /**
     * Processes a regular field access expression.
     */
    private LookupTermExpression processRegularFieldExpression(
            LookupTermExpression baseExpression,
            Class<?> baseCls,
            String name) throws SynBinderException
    {
        verifyRegularField(baseCls, name);
        
        Field synFld = findSynField(baseCls, name);
        Class<?> type = synFld.getType();
        
        LookupTermExpression expression;
        if (clsToSynFldMap.containsKey(type)) {
            expression = new LookupReferenceExpression(type, baseExpression, name);
        } else {
            expression = new LookupFieldExpression(type, baseExpression, synFld);
        }
        
        return expression;
    }

    /**
     * Finds a {@link SynField} field by its name.
     */
    private Field findSynField(Class<?> baseCls, String name) throws SynBinderException {
        Class<?> curCls = baseCls;
        while (curCls != null) {
            Field fld = CommonUtil.getFromMapMap(clsToSynFldMap, curCls, name);
            if (fld != null) {
                return fld;
            }
            curCls = curCls.getSuperclass();
        }
        
        throw new SynBinderException(String.format(
                "Cannot get field '%s' of class %s: no such %s field",
                name, baseCls.getCanonicalName(), SynField.class.getSimpleName()));
    }

    /**
     * Checks whether the Java field with the specified name can be used in a lookup expression.
     * A field cannot be used, for instance, if it is annotated with {@link SynLookup}. 
     */
    private void verifyRegularField(Class<?> baseCls, String name) throws SynBinderException {
        Class<?> curCls = baseCls;
        while (curCls != null) {
            Field fld = CommonUtil.getFromMapMap(clsToLookupFldMap, curCls, name);
            if (fld != null) {
                throw new SynBinderException(String.format(
                        "Cannot get field '%s' of class %s, because the field has %s annotation",
                        name, curCls.getCanonicalName(), SynLookup.class.getSimpleName()));
            }
            curCls = curCls.getSuperclass();
        }
    }
}
