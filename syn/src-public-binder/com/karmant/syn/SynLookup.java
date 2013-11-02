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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates that an object or a set of objects instantiated by {@link SynBinder} have to be bound to
 * the annotated field, if that objects satisfy the specified filter.</p>
 * 
 * <p>Example:
 * <pre>
 * public class SimpleEntity {
 *	&#64;SynField
 *	private String sfName;
 *	
 *	&#64;SynLookup("obj != this && obj.sfName == this.sfName")
 *	private SimpleEntity[] entitiesWithSameName;
 * }</pre>
 * In each object of this class the field <code>entitiesWithSameName</code> will be initialized by an array
 * of other <code>SimpleEntity</code> objects whose <code>sfName</code> field has the same value.</p>
 * 
 * <p>A filter can include:
 * <ol>
 * <li>Predefined variable <code>this</code> - the object whose field if being initialized.</li>
 * <li>Predefined variable <code>obj</code> - the object which is being filtered.</li>
 * <li>Field access expression: <code>this.fieldName</code>, <code>obj.fieldName</code>.</li>
 * <li>Equality check operators: <code>==</code>, <code>!=</code>.</li>
 * <li>Logical and operator: <code>&&</code>.</li>
 * </ol
 * </p>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SynLookup {
	/** Filter expression. */
	String value();
}
