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
package org.antkar.syn.internal.binder.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antkar.syn.binder.SynBinderException;
import org.antkar.syn.binder.SynLookup;
import org.antkar.syn.internal.binder.BinderEngine;
import org.antkar.syn.internal.binder.BoundObject;

/**
 * Contains all necessary information to bind a {@link SynLookup} field.
 */
public final class Lookup {

    private final Class<?> clsOfObj;
    private final LookupExpression expression;
    private final LookupBinder binder;

    Lookup(Class<?> clsOfObj, LookupExpression expression, LookupBinder binder) {
        this.clsOfObj = clsOfObj;
        this.expression = expression;
        this.binder = binder;
    }

    /**
     * Binds a value to the specified Java object's field.
     */
    public void bind(BoundObject bThis, BinderEngine<?> engine) throws SynBinderException {
        //Lookup objects.
        Collection<Object> oObjs = new ArrayList<>();
        for (Class<?> cls : engine.getClassesOfObjs()) {
            if (clsOfObj.isAssignableFrom(cls)) {
                List<BoundObject> objsForClass = engine.getObjsForClass(cls);
                selectObjects(bThis, objsForClass, oObjs);
            }
        }

        //Bind the objects.
        Object oThis = bThis.getJavaObject();
        binder.bind(oThis, oObjs);
    }

    /**
     * Searches for objects satisfying the filter.
     */
    private void selectObjects(BoundObject bThis, List<BoundObject> list, Collection<Object> result)
            throws SynBinderException
    {
        for (BoundObject bObj : list) {
            LookupEnv env = new LookupEnv(bThis, bObj);
            boolean eval = expression.eval(env);
            if (eval) {
                Object oObj = bObj.getJavaObject();
                result.add(oObj);
            }
        }
    }

    @Override
    public String toString() {
        return clsOfObj.getCanonicalName() + "(" + expression + ")";
    }
}
