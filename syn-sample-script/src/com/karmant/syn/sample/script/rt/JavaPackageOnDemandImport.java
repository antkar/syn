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
package com.karmant.syn.sample.script.rt;

import com.karmant.syn.sample.script.rt.value.Value;

/**
 * An on-demand import looking for a Java class in a particular Java package.
 */
class JavaPackageOnDemandImport extends OnDemandImport {
    private final String packagePrefix;
    
    JavaPackageOnDemandImport(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    @Override
    Value getValueOpt(String name) {
        String className = packagePrefix + name;
        Value value = ScriptScope.getJavaClassValueOpt(className);
        return value;
    }
    
    @Override
    public String toString() {
        return "package:" + packagePrefix + "*";
    }
}
