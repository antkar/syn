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
 * <p>Indicates that a field has to be bound to a value of a grammar attribute. The type of the field must be
 * compatible with the type of the attribute. The field may have any access, but it must not be static or
 * final.</p>
 * 
 * <p>An optional {@link String} parameter specifies the name of the grammar attribute which has to be bound
 * to the annotated field. If not specified, the attribute name is considered the same as the field name.</p>
 * 
 * <p>It is allowed to bind the same grammar attribute to different fields. For example, this can be useful to
 * bind the text position and the value of an attribute to two different fields of corresponding types.</p>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SynField {
    /** An optional name of the bound grammar attribute. */
    String value() default "";
}
