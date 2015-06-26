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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility method for finding the common superclass of a set of Java classes.
 */
final class CommonSuperclass {
    private CommonSuperclass() {}
    
    /**
     * Returns the most specific common superclass for the specified collection of classes.
     * Works with classes, not interfaces, enumerations or annotations.
     */
    static Class<?> getCommonSuperclass(Collection<Class<?>> clss) {
        //Calculate the minimum "height" of the classes. A "height" of a class is the length
        //of its superclass chain, including the class itself.
        int minHeight = calcMinClassHierarchyHeight(clss);
        
        //Make the height of all classes equal by replacing subclasses by their super-classes whose
        //height is equal to the minimum height.
        List<Class<?>> curClss = getSuperclassesForHeight(clss, minHeight);
        
        //Now replace all subclasses by their super-classes until all the classes are equal.
        for (;;) {
            Class<?> topCls = curClss.get(0);
            if (allEqual(curClss, topCls)) {
                //All the classes in the collection are equal. They are equal to the most specific
                //common superclass. In the worst case, the result will be java.lang.Object.
                return topCls;
            }
            //Go to super-classes.
            curClss = getSuperclasses(curClss);
        }
    }

    /**
     * Returns the list of direct super-classes for the specified list of classes.
     */
    private static List<Class<?>> getSuperclasses(List<Class<?>> classes) {
        List<Class<?>> superClasses = new ArrayList<>();
        for (Class<?> cls : classes) {
            Class<?> supCls = cls.getSuperclass();
            superClasses.add(supCls);
        }
        return superClasses;
    }

    /**
     * Returns the list of super-classes of the specified height for the given collection of classes.
     */
    private static List<Class<?>> getSuperclassesForHeight(Collection<Class<?>> classes, int height) {
        List<Class<?>> superClasses = new ArrayList<>();
        for (Class<?> cls : classes) {
            Class<?> supCls = getSuperclassForHeight(cls, height);
            superClasses.add(supCls);
        }
        return superClasses;
    }

    /**
     * For the given class, returns its superclass which has the specified height. The height must be
     * equal or less than the class' height.
     */
    private static Class<?> getSuperclassForHeight(Class<?> cls, int height) {
        int clsHeight = getClassHierarchyHeight(cls);
        int delta = clsHeight - height;
        Class<?> superCls = cls;
        for (int i = 0; i < delta; ++i) {
            superCls = superCls.getSuperclass();
        }
        return superCls;
    }

    /**
     * Returns <code>true</code> if all the classes in the passed list are equal to the given class.
     */
    private static boolean allEqual(List<Class<?>> classes, Class<?> topCls) {
        for (Class<?> cls : classes) {
            if (!cls.equals(topCls)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the minimum class height for the given collection of classes.
     */
    private static int calcMinClassHierarchyHeight(Collection<Class<?>> clss) {
        int minHeight = Integer.MAX_VALUE;
        for (Class<?> cls : clss) {
            int height = getClassHierarchyHeight(cls);
            minHeight = Math.min(minHeight, height);
        }
        return minHeight;
    }

    /**
     * Returns the class' height.
     */
    private static int getClassHierarchyHeight(final Class<?> cls) {
        int result = 0;
        Class<?> curCls = cls;
        while (curCls != null) {
            ++result;
            curCls = curCls.getSuperclass();
        }
        return result;
    }
}
