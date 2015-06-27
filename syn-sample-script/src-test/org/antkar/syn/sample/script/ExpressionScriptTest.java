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

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.TextSynsException;
import org.junit.Test;

/**
 * JUnit tests for different types of Script Language expressions.
 */
public class ExpressionScriptTest extends ScriptTest {
    @Test
    public void testAssignment() throws Exception, SynsException {
        execute("var x = 5; print(x = 10); print(x);");
        chkOut("10 10 ");
    }

    @Test
    public void testAssignmentAdd() throws Exception, SynsException {
        execute("var x = 5; print(x += 10); print(x);");
        chkOut("15 15 ");
    }
    
    @Test
    public void testAssignmentSub() throws Exception, SynsException {
        execute("var x = 5; print(x -= 10); print(x);");
        chkOut("-5 -5 ");
    }

    @Test
    public void testAssignmentMul() throws Exception, SynsException {
        execute("var x = 5; print(x *= 10); print(x);");
        chkOut("50 50 ");
    }

    @Test
    public void testAssignmentDiv() throws Exception, SynsException {
        execute("var x = 50; print(x /= 10); print(x);");
        chkOut("5 5 ");
    }

    @Test
    public void testAssignmentMod() throws Exception, SynsException {
        execute("var x = 13; print(x %= 10); print(x);");
        chkOut("3 3 ");
    }
    
    @Test
    public void testAssignmentAnd() throws Exception, SynsException {
        execute("var x = true; print(x &= false); print(x);");
        chkOut("false false ");
    }

    @Test
    public void testAssignmentOr() throws Exception, SynsException {
        execute("var x = false; print(x |= true); print(x);");
        chkOut("true true ");
    }

    @Test
    public void testConditionalTrue() throws Exception, SynsException {
        execute("function fn(x){ print('fn(' + x + ')'); return x;} print(true ? fn(5) : fn(33));");
        chkOut("fn(5) 5 ");
    }

    @Test
    public void testConditionalFalse() throws Exception, SynsException {
        execute("function fn(x){ print('fn(' + x + ')'); return x;} print(false ? fn(5) : fn(33));");
        chkOut("fn(33) 33 ");
    }

    @Test
    public void testOrTrueTrue() throws Exception, SynsException {
        execute("function fn(x, y){ print('fn(' + x + ')'); return y;} print(fn(1, true) || fn(2, true));");
        chkOut("fn(1) true ");
    }

    @Test
    public void testOrTrueFalse() throws Exception, SynsException {
        execute("function fn(x, y){print('fn(' + x + ')');return y;} print(fn(1, true) || fn(2, false));");
        chkOut("fn(1) true ");
    }

    @Test
    public void testOrFalseTrue() throws Exception, SynsException {
        execute("function fn(x, y){print('fn(' + x + ')');return y;} print(fn(1, false) || fn(2, true));");
        chkOut("fn(1) fn(2) true ");
    }

    @Test
    public void testOrFalseFalse() throws Exception, SynsException {
        execute("function fn(x, y){print('fn(' + x + ')');return y;} print(fn(1, false) || fn(2, false));");
        chkOut("fn(1) fn(2) false ");
    }

    @Test
    public void testAndTrueTrue() throws Exception, SynsException {
        execute("function fn(x, y){ print('fn(' + x + ')'); return y;} print(fn(1, true) && fn(2, true));");
        chkOut("fn(1) fn(2) true ");
    }

    @Test
    public void testAndTrueFalse() throws Exception, SynsException {
        execute("function fn(x, y){print('fn(' + x + ')');return y;} print(fn(1, true) && fn(2, false));");
        chkOut("fn(1) fn(2) false ");
    }

    @Test
    public void testAndFalseTrue() throws Exception, SynsException {
        execute("function fn(x, y){print('fn(' + x + ')');return y;} print(fn(1, false) && fn(2, true));");
        chkOut("fn(1) false ");
    }

    @Test
    public void testAndFalseFalse() throws Exception, SynsException {
        execute("function fn(x, y){print('fn(' + x + ')');return y;} print(fn(1, false) && fn(2, false));");
        chkOut("fn(1) false ");
    }

    @Test
    public void testEqIntInt() throws Exception, SynsException {
        execute("print(5 == 5); print(5 == 10);");
        chkOut("true false ");
    }

    @Test
    public void testEqIntDouble() throws Exception, SynsException {
        execute("print(5 == 5.0); print(5 == 10.0);");
        chkOut("true false ");
    }

    @Test
    public void testEqDoubleDouble() throws Exception, SynsException {
        execute("print(5.123 == 5.123); print(5.123 == 10.123);");
        chkOut("true false ");
    }

    @Test
    public void testEqBooleanBoolean() throws Exception {
        execute("print(true == true); print(true == false);");
        chkOut("true false ");
    }
    
    @Test
    public void testEqStringStringDiffObjs() throws Exception {
        execute("print('aaa' == 'aaa'); print('aaa' == 'bbb');");
        chkOut("false false ");
    }
    
    @Test
    public void testEqStringStringSameObj() throws Exception {
        execute("var a = 'aaa'; var b = 'aaa'; print(a == a); print(a == b);");
        chkOut("true false ");
    }
    
    @Test
    public void testEqNullNull() throws Exception {
        execute("print(null == null);");
        chkOut("true ");
    }
    
    @Test
    public void testEqStringNull() throws Exception {
        execute("print('aaa' == null);");
        chkOut("false ");
    }
    
    @Test
    public void testEqObjectObject() throws Exception {
        execute("var a = new Object(); var b = new Object(); print(a == a); print(a == b);");
        chkOut("true false ");
    }
    
    @Test
    public void testEqObjectNull() throws Exception {
        execute("var a = new Object(); print(a == null);");
        chkOut("false ");
    }
    
    @Test
    public void testNeIntInt() throws Exception, SynsException {
        execute("print(5 != 5); print(5 != 10);");
        chkOut("false true ");
    }

    @Test
    public void testNeIntDouble() throws Exception, SynsException {
        execute("print(5 != 5.0); print(5 != 10.0);");
        chkOut("false true ");
    }

    @Test
    public void testNeDoubleDouble() throws Exception, SynsException {
        execute("print(5.123 != 5.123); print(5.123 != 10.123);");
        chkOut("false true ");
    }
    
    @Test
    public void testNeBooleanBoolean() throws Exception {
        execute("print(true != true); print(true != false);");
        chkOut("false true ");
    }
    
    @Test
    public void testNeStringStringDiffObjs() throws Exception {
        execute("print('aaa' != 'aaa'); print('aaa' != 'bbb');");
        chkOut("true true ");
    }
    
    @Test
    public void testNeStringStringSameObj() throws Exception {
        execute("var a = 'aaa'; var b = 'aaa'; print(a != a); print(a != b);");
        chkOut("false true ");
    }
    
    @Test
    public void testNeNullNull() throws Exception {
        execute("print(null != null);");
        chkOut("false ");
    }
    
    @Test
    public void testNeStringNull() throws Exception {
        execute("print('aaa' != null);");
        chkOut("true ");
    }
    
    @Test
    public void testNeObjectObject() throws Exception {
        execute("var a = new Object(); var b = new Object(); print(a != a); print(a != b);");
        chkOut("false true ");
    }
    
    @Test
    public void testNeObjectNull() throws Exception {
        execute("var a = new Object(); print(a != null);");
        chkOut("true ");
    }

    @Test
    public void testLtIntInt() throws Exception, SynsException {
        execute("print(5 < 6); print(6 < 5); print(5 < 5);");
        chkOut("true false false ");
    }

    @Test
    public void testLtDoubleDouble() throws Exception, SynsException {
        execute("print(5.001 < 5.002); print(5.002 < 5.001); print(5.001 < 5.001);");
        chkOut("true false false ");
    }

    @Test
    public void testLtIntDouble() throws Exception, SynsException {
        execute("print(5 < 5.001); print(5.001 < 5); print(5 < 5.0);");
        chkOut("true false false ");
    }

    @Test
    public void testGtIntInt() throws Exception, SynsException {
        execute("print(5 > 6); print(6 > 5); print(5 > 5);");
        chkOut("false true false ");
    }

    @Test
    public void testGtDoubleDouble() throws Exception, SynsException {
        execute("print(5.001 > 5.002); print(5.002 > 5.001); print(5.001 > 5.001);");
        chkOut("false true false ");
    }

    @Test
    public void testGtIntDouble() throws Exception, SynsException {
        execute("print(5 > 5.001); print(5.001 > 5); print(5 > 5.0);");
        chkOut("false true false ");
    }

    @Test
    public void testLeIntInt() throws Exception, SynsException {
        execute("print(5 <= 6); print(6 <= 5); print(5 <= 5);");
        chkOut("true false true ");
    }

    @Test
    public void testLeDoubleDouble() throws Exception, SynsException {
        execute("print(5.001 <= 5.002); print(5.002 <= 5.001); print(5.001 <= 5.001);");
        chkOut("true false true ");
    }

    @Test
    public void testLeIntDouble() throws Exception, SynsException {
        execute("print(5 <= 5.001); print(5.001 <= 5); print(5 <= 5.0);");
        chkOut("true false true ");
    }

    @Test
    public void testGeIntInt() throws Exception, SynsException {
        execute("print(5 >= 6); print(6 >= 5); print(5 >= 5);");
        chkOut("false true true ");
    }

    @Test
    public void testGeDoubleDouble() throws Exception, SynsException {
        execute("print(5.001 >= 5.002); print(5.002 >= 5.001); print(5.001 >= 5.001);");
        chkOut("false true true ");
    }

    @Test
    public void testGeIntDouble() throws Exception, SynsException {
        execute("print(5 >= 5.001); print(5.001 >= 5); print(5 >= 5.0);");
        chkOut("false true true ");
    }

    @Test
    public void testAddIntInt() throws Exception, SynsException {
        execute("print(5 + 10);");
        chkOut("15 ");
    }

    @Test
    public void testAddIntDouble() throws Exception, SynsException {
        execute("print(5 + 10.123);");
        chkOut("15.123 ");
    }

    @Test
    public void testAddDoubleDouble() throws Exception, SynsException {
        execute("print(5.123 + 10.456);");
        chkOut("15.579 ");
    }

    @Test
    public void testAddIntString() throws Exception, SynsException {
        execute("print(5 + 'aaa');");
        chkOut("5aaa ");
    }

    @Test
    public void testAddDoubleString() throws Exception, SynsException {
        execute("print(5.123 + 'aaa');");
        chkOut("5.123aaa ");
    }

    @Test
    public void testAddStringString() throws Exception, SynsException {
        execute("print('aaa' + 'bbb');");
        chkOut("aaabbb ");
    }

    @Test
    public void testAddStringInt() throws Exception, SynsException {
        execute("print('aaa' + 10);");
        chkOut("aaa10 ");
    }

    @Test
    public void testAddStringDouble() throws Exception, SynsException {
        execute("print('aaa' + 10.123);");
        chkOut("aaa10.123 ");
    }

    @Test
    public void testAddStringNull() throws Exception, SynsException {
        execute("print('aaa' + null);");
        chkOut("aaanull ");
    }

    @Test
    public void testAddNullString() throws Exception, SynsException {
        execute("print(null + 'aaa');");
        chkOut("nullaaa ");
    }

    @Test
    public void testAddStringObject() throws Exception, SynsException {
        execute("print('aaa' + new java.util.ArrayList());");
        chkOut("aaa[] ");
    }

    @Test
    public void testAddObjectString() throws Exception, SynsException {
        execute("print(new java.util.ArrayList() + 'aaa');");
        chkOut("[]aaa ");
    }

    @Test
    public void testSubIntInt() throws Exception, SynsException {
        execute("print(10 - 5);");
        chkOut("5 ");
    }

    @Test
    public void testSubIntDouble() throws Exception, SynsException {
        execute("print(10 - 5.25);");
        chkOut("4.75 ");
    }

    @Test
    public void testSubDoubleDouble() throws Exception, SynsException {
        execute("print(10.5 - 3.75);");
        chkOut("6.75 ");
    }

    @Test
    public void testMulIntInt() throws Exception, SynsException {
        execute("print(10 * 5);");
        chkOut("50 ");
    }

    @Test
    public void testMulIntDouble() throws Exception, SynsException {
        execute("print(10 * 5.25);");
        chkOut("52.5 ");
    }

    @Test
    public void testMulDoubleDouble() throws Exception, SynsException {
        execute("print(10.3 * 5.25);");
        chkOut("54.075 ");
    }

    @Test
    public void testDivIntInt() throws Exception, SynsException {
        execute("print(18 / 5);");
        chkOut("3 ");
    }

    @Test
    public void testDivIntDouble() throws Exception, SynsException {
        execute("print(18 / 5.0);");
        chkOut("3.6 ");
    }

    @Test
    public void testDivDoubleInt() throws Exception, SynsException {
        execute("print(18.0 / 5);");
        chkOut("3.6 ");
    }

    @Test
    public void testDivDoubleDouble() throws Exception, SynsException {
        execute("print(19.425 / 5.25);");
        chkOut("3.7 ");
    }

    @Test
    public void testModIntInt() throws Exception, SynsException {
        execute("print(18 % 5);");
        chkOut("3 ");
    }

    @Test
    public void testModIntDouble() throws Exception, SynsException {
        execute("print(18 % 5.25);");
        chkOut("2.25 ");
    }

    @Test
    public void testModDoubleInt() throws Exception, SynsException {
        execute("print(18.25 % 5);");
        chkOut("3.25 ");
    }

    @Test
    public void testModDoubleDouble() throws Exception, SynsException {
        execute("print(19.5 % 5.25);");
        chkOut("3.75 ");
    }

    @Test
    public void testPlusInt() throws Exception, SynsException {
        execute("print(+5);");
        chkOut("5 ");
    }

    @Test
    public void testPlusDouble() throws Exception, SynsException {
        execute("print(+5.25);");
        chkOut("5.25 ");
    }

    @Test
    public void testMinusInt() throws Exception, SynsException {
        execute("print(-5);");
        chkOut("-5 ");
    }

    @Test
    public void testMinusDouble() throws Exception, SynsException {
        execute("print(-5.25);");
        chkOut("-5.25 ");
    }

    @Test
    public void testPreIncrement() throws Exception, SynsException {
        execute("var x = 5; print(++x); print(x);");
        chkOut("6 6 ");
    }

    @Test
    public void testPreDecrement() throws Exception, SynsException {
        execute("var x = 5; print(--x); print(x);");
        chkOut("4 4 ");
    }

    @Test
    public void testLogicalNot() throws Exception, SynsException {
        execute("print(!true); print(!false);");
        chkOut("false true ");
    }

    @Test
    public void testCastIntToDouble() throws Exception, SynsException {
        execute("print((double)5);");
        chkOut("5.0 ");
    }

    @Test
    public void testCastDoubleToInt() throws Exception, SynsException {
        execute("print((int)5.25);");
        chkOut("5 ");
    }

    @Test
    public void testCastIntToLong() throws Exception, SynsException {
        execute("print((long)5);");
        chkOut("5 ");
    }

    @Test
    public void testCastDoubleToLong() throws Exception, SynsException {
        execute("print((long)5.25);");
        chkOut("5 ");
    }

    @Test
    public void testCastDoubleToDouble() throws Exception {
        execute("print((double)5.6789);");
        chkOut("5.6789 ");
    }

    @Test
    public void testCastStringToDouble() throws Exception {
        try {
            execute("print((double)'aaa');");
            fail();
        } catch (TextSynsException e) {
            assertEquals("Cannot cast String to double", e.getCause().getMessage());
        }
    }

    @Test
    public void testCastStringToInt() throws Exception {
        try {
            execute("print((int)'aaa');");
            fail();
        } catch (TextSynsException e) {
            assertEquals("Cannot cast String to int", e.getCause().getMessage());
        }
    }
    
    @Test
    public void testPostIncrement() throws Exception, SynsException {
        execute("var x = 5; print(x++); print(x);");
        chkOut("5 6 ");
    }

    @Test
    public void testPostDecrement() throws Exception, SynsException {
        execute("var x = 5; print(x--); print(x);");
        chkOut("5 4 ");
    }

    @Test
    public void testParentheses() throws Exception, SynsException {
        execute("print((2 + 3) * 4);");
        chkOut("20 ");
    }

    @Test
    public void testMemberClassVariable() throws Exception, SynsException {
        execute("class C { var x = 123; } print(new C().x);");
        chkOut("123 ");
    }

    @Test
    public void testMemberStringLength() throws Exception {
        execute("print('aaa'.length());");
        chkOut("3 ");
    }
    
    @Test
    public void testMemberStringSubstring() throws Exception {
        execute("print('abcdef'.substring(1, 5));");
        chkOut("bcde ");
    }
    
    @Test
    public void testFunctionNoArguments() throws Exception, SynsException {
        execute("function fn(x, y, z){ print('fn(' + x + ',' + y + ',' + z + ')');} fn();");
        chkOut("fn(null,null,null) ");
    }

    @Test
    public void testFunctionOneArgument() throws Exception, SynsException {
        execute("function fn(x, y, z){ print('fn(' + x + ',' + y + ',' + z + ')');} fn(123);");
        chkOut("fn(123,null,null) ");
    }

    @Test
    public void testFunctionTwoArguments() throws Exception, SynsException {
        execute("function fn(x, y, z){ print('fn(' + x + ',' + y + ',' + z + ')');} fn(123,456);");
        chkOut("fn(123,456,null) ");
    }

    @Test
    public void testFunctionThreeArguments() throws Exception, SynsException {
        execute("function fn(x, y, z){ print('fn(' + x + ',' + y + ',' + z + ')');} fn(123,456,789);");
        chkOut("fn(123,456,789) ");
    }

    @Test
    public void testFunctionJavaOverloadedMethod1() throws Exception {
        execute("var list = new java.util.ArrayList(); list.add('Hello'); list.add('World');" +
                "list.remove(0); print(list); list.remove('World'); print(list);");
        chkOut("[World] [] ");
    }
    
    @Test
    public void testFunctionJavaOverloadedMethod2() throws Exception {
        execute("var list = new java.util.ArrayList(); list.add(2); list.add(1); list.add(0);" +
                "list.remove(2); print(list); list.remove(1); print(list); list.remove(0); print(list);");
        chkOut("[2, 1] [2] [] ");
    }
    
    @Test
    public void testFunctionJavaVoid() throws Exception {
        try {
            execute("var x = System.gc();");
            fail();
        } catch (SynsException e) {
            assertEquals("Invalid operation for void", e.getMessage());
        }
    }
    
    @Test
    public void testFunctionJavaVarArgs() throws Exception {
        execute("print(String.format(java.util.Locale.US, '%d %s %b', 123, 456, true));");
        chkOut("123 456 true ");
    }
    
    @Test
    public void testNewClass() throws Exception, SynsException {
        execute("class C { function C() { print('C()');} } new C();");
        chkOut("C() ");
    }

    @Test
    public void testNewJavaClass() throws Exception, SynsException {
        execute("print(new java.util.ArrayList());");
        chkOut("[] ");
    }

    @Test
    public void testNewArrayOneDim() throws Exception, SynsException {
        execute("print(new [3]);");
        chkOut("[null, null, null] ");
    }

    @Test
    public void testNewArrayTwoDims() throws Exception, SynsException {
        execute("print(new [3][2]);");
        chkOut("[[null, null], [null, null], [null, null]] ");
    }
    
    @Test
    public void testArrayNoElements() throws Exception, SynsException {
        execute("print([]);");
        chkOut("[] ");
    }

    @Test
    public void testArrayOneElement() throws Exception, SynsException {
        execute("print([33]);");
        chkOut("[33] ");
    }

    @Test
    public void testArrayManyElements() throws Exception, SynsException {
        execute("print([1, 2, 3, 4, 5]);");
        chkOut("[1, 2, 3, 4, 5] ");
    }

    @Test
    public void testBlock() throws Exception, SynsException {
        execute("var b = { print('block'); }; print('b()'); b();");
        chkOut("b() block ");
    }
    
    @Test
    public void testJavaInterfaceVoidToVoidFromBlock() throws Exception {
        execute("voidToVoid({ print('block'); });");
        chkOut("block ");
    }

    @Test
    public void testJavaInterfaceVoidToVoidFromBlockInThread() throws Exception {
        execute("var block = { print('block'); };" +
                "var thread = new Thread(block); thread.start(); thread.join();");
        chkOut("block ");
    }
    
    @Test
    public void testJavaInterfaceVoidToVoidFromLambda() throws Exception {
        execute("voidToVoid(() -> print('block'));");
        chkOut("block ");
    }
    
    @Test
    public void testJavaInterfaceVoidToVoidFromFunction() throws Exception {
        execute("function f() = print('block'); voidToVoid(f);");
        chkOut("block ");
    }
    
    @Test
    public void testJavaInterfaceVoidToStringFromBlock() throws Exception {
        execute("print(voidToString({ return 'block'; }));");
        chkOut("block ");
    }
    
    @Test
    public void testJavaInterfaceVoidToStringFromLambda() throws Exception {
        execute("print(voidToString(() -> 'block'));");
        chkOut("block ");
    }
    
    @Test
    public void testJavaInterfaceVoidToStringFromFunction() throws Exception {
        execute("function f() = 'block'; print(voidToString(f));");
        chkOut("block ");
    }
    
    @Test
    public void testJavaInterfaceIntToStringFromLambda() throws Exception {
        execute("print(intToString(123, x -> 'x=' + x));");
        chkOut("x=123 ");
    }
    
    @Test
    public void testJavaInterfaceIntToStringFromFunction() throws Exception {
        execute("function f(x) = 'x=' + x; print(intToString(123, f));");
        chkOut("x=123 ");
    }
    
    @Test
    public void testBlockWithStateCall() throws Exception {
        execute("var block = { var z=0; print('block-' + z); ++z; }; block(); block();");
        chkOut("block-0 block-0 ");
    }
    
    @Test
    public void testLambdaExpressionNoParameters() throws Exception {
        execute("var f = () -> print(\"Lambda!\"); f();");
        chkOut("Lambda! ");
    }
    
    @Test
    public void testLambdaExpressionOneParameter() throws Exception {
        execute("var f = x -> x * x; print(f(5));");
        chkOut("25 ");
    }
    
    @Test
    public void testLambdaExpressionTwoParameters() throws Exception {
        execute("var f = (a, b) -> a * b; print(f(5, 7));");
        chkOut("35 ");
    }
    
    @Test
    public void testLambdaExpressionTwoParametersWithBlock() throws Exception {
        execute("var f = (a, b) -> { return a * b; }; print(100 * f(5, 7));");
        chkOut("3500 ");
    }
    
    @Test
    public void testSubscriptGet() throws Exception {
        execute("print((new [10])[9]);");
        chkOut("null ");
    }
    
    @Test
    public void testSubscriptSet() throws Exception {
        execute("var array = new [10]; array[9] = 123; print(array[9]);");
        chkOut("123 ");
    }
    
    @Test
    public void testName() throws Exception, SynsException {
        execute("var x = 123; print(x);");
        chkOut("123 ");
    }

    @Test
    public void testInteger() throws Exception, SynsException {
        execute("print(123);");
        chkOut("123 ");
    }

    @Test
    public void testFloat() throws Exception, SynsException {
        execute("print(123.456);");
        chkOut("123.456 ");
    }

    @Test
    public void testString() throws Exception, SynsException {
        execute("print('aaa');");
        chkOut("aaa ");
    }
    
    @Test
    public void testNull() throws Exception, SynsException {
        execute("print(null);");
        chkOut("null ");
    }

    @Test
    public void testBoolean() throws Exception, SynsException {
        execute("print(true); print(false);");
        chkOut("true false ");
    }
}
