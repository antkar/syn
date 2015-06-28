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
package org.antkar.syn.sample.script;

import org.antkar.syn.sample.script.rt.TextSynsException;
import org.junit.Test;

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
            assertEquals("Invalid operation for int", e.getOriginalMessage());
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
    public void testFunctionExpressionBody() throws Exception {
        execute("function foo(a, b) = a * b;" +
                "print(foo(5, 7));");
        chkOut("35 ");
    }
    
    @Test
    public void testFunctionExpressionBodyWithBlock() throws Exception {
        execute("function foo(a, b) = { return a * b; };" +
                "print(100 * foo(5, 7));");
        chkOut("3500 ");
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
        execute("class C { public var x = 123; } var c = new C(); print(c.x);");
        chkOut("123 ");
    }
    
    @Test
    public void testClassMemberVariableChangeValue() throws Exception {
        execute("class C { public var x = 123; } var c = new C(); c.x = 987; print(c.x); ");
        chkOut("987 ");
    }
    
    @Test
    public void testClassMemberVariableFunction() throws Exception {
        execute("class C { var x = 123; public function f() {print(x++);} } "
                + "var c = new C(); c.f(); c.f();");
        chkOut("123 124 ");
    }
    
    @Test
    public void testClassMemberConstant() throws Exception {
        execute("class C { public const X = 123; } print(C.X); var c = new C(); print(c.X);");
        chkOut("123 123 ");
    }
    
    @Test
    public void testClassMemberFunction() throws Exception {
        execute("class C { public function fn(a) { print('C.fn(' + a + ')'); } } "
                + "var c = new C(); c.fn(123);");
        chkOut("C.fn(123) ");
    }
    
    @Test
    public void testClassMemberVariablePrivateAccessInside() throws Exception {
        execute("class X { var x = 123; public function f() = x; } print(X().f());");
        chkOut("123 ");
    }
    
    @Test
    public void testClassMemberVariablePrivateAccessOutside() throws Exception {
        try {
            execute("class X { var x = 123; } print(X().x);");
            fail();
        } catch (TextSynsException e) {
            assertEquals("Unknown name: x", e.getOriginalMessage());
        }
        chkOut("");
    }
    
    @Test
    public void testClassMemberVariablePrivateAccessAnotherObject() throws Exception {
        execute("class X { var x; function X(x){ this.x = x; } public function f(obj) = obj.x; }"
                + "print(X(123).f(X(456)));");
        chkOut("456 ");
    }
    
    @Test
    public void testClassMemberVariablePrivateAccessFromNestedBlock() throws Exception {
        execute("class X { var x = 123; public function f() { function g() = x; return g(); } }"
                + "print(X().f());");
        chkOut("123 ");
    }
    
    @Test
    public void testClassMemberVariablePublicAccessOutside() throws Exception {
        execute("class X { public var x = 123; } print(X().x);");
        chkOut("123 ");
    }

    @Test
    public void testClassMemberConstantPrivateAccessInside() throws Exception {
        execute("class X { const x = 123; public function f() = x; } print(X().f());");
        chkOut("123 ");
    }

    @Test
    public void testClassMemberConstantPrivateAccessInsideConstant() throws Exception {
        execute("class X { const x = 123; public const y = x; } print(X.y);");
        chkOut("123 ");
    }

    @Test
    public void testClassMemberConstantPrivateAccessOutside() throws Exception {
        try {
            execute("class X { const x = 123; } print(X.x);");
            fail();
        } catch (TextSynsException e) {
            assertEquals("Unknown name: x", e.getOriginalMessage());
        }
        chkOut("");
    }

    @Test
    public void testClassMemberConstantPublicAccessOutside() throws Exception {
        execute("class X { public const x = 123; } print(X.x);");
        chkOut("123 ");
    }
    
    @Test
    public void testClassMemberCosntructorPrivateAccessOutside() throws Exception {
        execute("class X { public var x = 123; function X(){} } print(X().x); ");
        chkOut("123 ");
    }
    
    @Test
    public void testClassMemberConstructorPublicAccessOutside() throws Exception {
        execute("class X { public var x = 123; public function X(){} } print(X().x); ");
        chkOut("123 ");
    }
    
    @Test
    public void testClassVariableInitializerPassingAnotherVariableToFunction() throws Exception {
        execute("function f(x) = x * 2; class X { var k = 123; public var copy = f(k); }"
                + "print(X().copy);");
        chkOut("246 ");
    }
    
    @Test
    public void testClassVariableInitializerPassingAnotherVariableToConstructor() throws Exception {
        execute("class Z { public var z; function Z(z){ this.z = z; } }"
                + "class X { var k = 123; public var copy = Z(k); }"
                + "print(X().copy.z);");
        chkOut("123 ");
    }
    
    @Test
    public void testClassVariableInitializerChainDependency() throws Exception {
        // This test tests that member variables are initialized in the order in which they are
        // declared.
        execute("function f(x) = x + 1;"
                + "class X { var v1=1; var v2=f(v1); var v3=f(v2); var v4=f(v3); var v5=f(v4);"
                + "var v6=f(v5); var v7=f(v6); var v8=f(v7); var v9=f(v8); var vA=f(v9);"
                + "public var s = [v1, v2, v3, v4, v5, v6, v7, v8, v9, vA]; }"
                + "print(X().s);");
        chkOut("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10] ");
    }
    
    @Test
    public void testClassVariableInitializerUsesClassFunctionDeclaredBelow() throws Exception {
        execute("class X { public var x = f; function f(a) = a * 2; }"
                + "print(X().x(123));");
        chkOut("246 ");
    }
}
