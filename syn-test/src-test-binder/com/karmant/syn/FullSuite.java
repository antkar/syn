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

import javax.script.ScriptEngine;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.karmant.syn.sample.script.ScriptSuite;

/**
 * Full unit test suite. Includes tests for {@link SynParser}, {@link SynBinder}
 * and {@link ScriptEngine}.
 */
@RunWith(Suite.class)
@SuiteClasses({
	SynSuite.class,
	BinderSuite.class,
	ScriptSuite.class,
})
public class FullSuite {
	private FullSuite(){}
}
