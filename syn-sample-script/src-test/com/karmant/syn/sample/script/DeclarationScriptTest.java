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
package com.karmant.syn.sample.script;

import org.junit.Test;

import com.karmant.syn.sample.script.rt.TextSynsException;

/**
 * JUnit tests for different types of Script Language declarations.
 */
public class DeclarationScriptTest extends ScriptTest {
    @Test
    public void testScriptEmpty() throws Exception {
        execute("");
        chkOut("");
    }
    
    @Test
    public void testScriptSingleStatement() throws Exception {
        execute("print('Aaa');");
        chkOut("Aaa ");
    }
    
    @Test
    public void testImportJavaClass() throws Exception {
        execute("import java.util.ArrayList; print(new ArrayList());");
        chkOut("[] ");
    }
    
    @Test
    public void testImportJavaMethod() throws Exception {
        execute("import java.lang.Math.min; print(min(10, 20));");
        chkOut("10 ");
    }
    
    @Test
    public void testImportJavaField() throws Exception {
        execute("import java.lang.System.out; out.print('Aaa');");
        chkOut("Aaa");
    }
    
    @Test
    public void testImportOnDemandJavaPackage() throws Exception {
        execute("import java.util.*; print(new ArrayList());");
        chkOut("[] ");
    }
    
    @Test
    public void testImportOnDemandJavaClass() throws Exception {
        execute("import java.lang.Math.*; print(min(10, 20));");
        chkOut("10 ");
    }
    
    @Test
    public void testVariableNoInitialValue() throws Exception {
        execute("var x; print(x);");
        chkOut("null ");
    }
    
    @Test
    public void testVariableInitialValue() throws Exception {
        execute("var x = 123; print(x);");
        chkOut("123 ");
    }
    
    @Test
    public void testConstant() throws Exception {
        execute("const x = 123; print(x);");
        chkOut("123 ");
    }
    
    @Test
    public void testConstantWrite() throws Exception {
        try {
            execute("const C = 123; C = 456;");
            fail();
        } catch (TextSynsException e) {
            assertEquals("Invalid operation for int", e.getCause().getMessage());
        }
        chkOut("");
    }
    
    @Test
    public void testFunctionNoParameters() throws Exception {
        execute("function fn() { print('fn()'); } fn();");
        chkOut("fn() ");
    }
    
    @Test
    public void testFunctionOneParameter() throws Exception {
        execute("function fn(a) { print('fn(' + a + ')'); } fn(123);");
        chkOut("fn(123) ");
    }
    
    @Test
    public void testFunctionTwoParameters() throws Exception {
        execute("function fn(a, b) { print('fn(' + a + ', ' + b + ')'); } fn(123, 987);");
        chkOut("fn(123, 987) ");
    }
    
    @Test
    public void testFunctionIndirectRecursion() throws Exception {
        execute("function foo(level) { print('foo ' + level); if (level > 0) bar(level - 1); }" +
                "function bar(level) { print('bar ' + level); if (level > 0) foo(level - 1); }" +
                "print('foo call:'); foo(3); print('bar call:'); bar(3);");
        chkOut("foo call: foo 3 bar 2 foo 1 bar 0 bar call: bar 3 foo 2 bar 1 foo 0 ");
    }
    
    @Test
    public void testClassNoMembers() throws Exception {
        execute("class C {} new C();");
        chkOut("");
    }
    
    @Test
    public void testClassConstructor() throws Exception {
        execute("class C { function C() { print('C()'); } } new C();");
        chkOut("C() ");
    }
    
    @Test
    public void testClassMemberVariable() throws Exception {
        execute("class C { var x = 123; } var c = new C(); print(c.x);");
        chkOut("123 ");
    }
    
    @Test
    public void testClassMemberVariableChangeValue() throws Exception {
        execute("class C { var x = 123; } var c = new C(); c.x = 987; print(c.x); ");
        chkOut("987 ");
    }
    
    @Test
    public void testClassMemberVariableFunction() throws Exception {
        execute("class C { var x = 123; function f() {print(x++);} } var c = new C(); c.f(); c.f();");
        chkOut("123 124 ");
    }
    
    @Test
    public void testClassMemberConstant() throws Exception {
        execute("class C { const X = 123; } print(C.X); var c = new C(); print(c.X);");
        chkOut("123 123 ");
    }
    
    @Test
    public void testClassMemberFunction() throws Exception {
        execute("class C { function fn(a) { print('C.fn(' + a + ')'); } } var c = new C(); c.fn(123);");
        chkOut("C.fn(123) ");
    }
}
