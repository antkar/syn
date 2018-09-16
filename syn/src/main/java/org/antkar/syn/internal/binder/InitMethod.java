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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.antkar.syn.binder.SynBinderException;
import org.antkar.syn.binder.SynInit;

/**
 * Describes a {@link SynInit} method.
 */
final class InitMethod {

    private final Method method;

    InitMethod(Method method) {
        this.method = method;
    }

    /**
     * Invokes the associated initialization method on the passed object.
     */
    void invoke(Object obj) throws SynBinderException {
        boolean accessible = method.isAccessible();
        method.setAccessible(true);
        try {
            invokeMethod(obj);
        } finally {
            method.setAccessible(accessible);
        }
    }

    /**
     * Invokes the method, wrapping Java reflection API exceptions.
     */
    private void invokeMethod(Object obj) throws SynBinderException {
        try {
            method.invoke(obj);
        } catch (IllegalAccessException e) {
            throw new SynBinderException(String.format(
                    "Ivocation of method %s failed", method), e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            } else if (targetException instanceof SynBinderException) {
                throw (SynBinderException) targetException;
            } else {
                throw new SynBinderException(String.format(
                        "Ivocation of method %s failed", method), targetException);
            }
        }
    }

    @Override
    public String toString() {
        return method + "";
    }
}
