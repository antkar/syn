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
import java.util.Collection;

import org.antkar.syn.LookupBinder;
import org.antkar.syn.SynBinderException;

/**
 * Single object binder. Binds a single object to a field of a non-array type.
 */
class SingleObjectLookupBinder extends LookupBinder {

    SingleObjectLookupBinder(Field field) {
        super(field);
    }

    @Override
    Object createValue(Collection<Object> oObjs) throws SynBinderException {
        Object result;
        
        if (oObjs.isEmpty()) {
            result = null;
        } else if (oObjs.size() == 1) {
            result = oObjs.iterator().next();
        } else {
            throw new SynBinderException(String.format(
                    "%d objects satisfy expression bound to the field %s, which is not an array",
                    oObjs.size(), getField()));
        }
        
        return result;
    }

}
