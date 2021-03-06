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
import org.antkar.syn.sample.script.rt.ThrowSynsException;
import org.junit.Test;

/**
 * JUnit tests for different types of Script Language statements.
 */
public final class StatementScriptTest extends ScriptTest {
    @Test
    public void testEmpty() throws Exception, SynsException {
        execute(";;;");
        chkOut("");
    }

    @Test
    public void testVariableDeclaration() throws Exception, SynsException {
        execute("; var x = 5; print(x);");
        chkOut("5 ");
    }

    @Test
    public void testConstantDeclaration() throws Exception, SynsException {
        execute("; const C = 5; print(C);");
        chkOut("5 ");
    }

    @Test
    public void testExpression() throws Exception, SynsException {
        execute("function fn(x){ print('fn('+x+')'); return true; } fn(2) && fn(3);");
        chkOut("fn(2) fn(3) ");
    }

    @Test
    public void testIfTrue() throws Exception, SynsException {
        execute("if (true) print('True');");
        chkOut("True ");
    }

    @Test
    public void testIfFalse() throws Exception, SynsException {
        execute("if (false) print('True');");
        chkOut("");
    }

    @Test
    public void testIfElseTrue() throws Exception, SynsException {
        execute("if (true) print('True'); else print('False');");
        chkOut("True ");
    }

    @Test
    public void testIfElseFalse() throws Exception, SynsException {
        execute("if (false) print('True'); else print('False');");
        chkOut("False ");
    }

    @Test
    public void testIfDanglingElse() throws Exception, SynsException {
        execute("if (false) if (true) print('A'); else print('B');");
        chkOut("");
    }

    @Test
    public void testWhile() throws Exception, SynsException {
        execute("var x = 0; while (x < 3) print(x++);");
        chkOut("0 1 2 ");
    }

    @Test
    public void testWhileBreak() throws Exception, SynsException {
        execute("var x = 0; print('Begin'); " +
                "while (x++ < 3) { print('W-Begin-' + x); break; print('W-End-' + x); } " +
                "print('End');");
        chkOut("Begin W-Begin-1 End ");
    }

    @Test
    public void testWhileContinue() throws Exception, SynsException {
        execute("var x = 0; print('Begin'); " +
                "while (x++ < 3) { print('W-Begin-' + x); continue; print('W-End-' + x); } " +
                "print('End');");
        chkOut("Begin W-Begin-1 W-Begin-2 W-Begin-3 End ");
    }

    @Test
    public void testForRegular() throws Exception, SynsException {
        execute("for (var x = 0; x < 3; ++x) print(x);");
        chkOut("0 1 2 ");
    }

    @Test
    public void testForRegularExternalVar() throws Exception, SynsException {
        execute("var x; for (x = 0; x < 3; ++x) print(x);");
        chkOut("0 1 2 ");
    }

    @Test
    public void testForRegularNoVar() throws Exception, SynsException {
        execute("var x = 0; for (; x < 3; ++x) print(x);");
        chkOut("0 1 2 ");
    }

    @Test
    public void testForRegularNoCondition() throws Exception, SynsException {
        execute("for (var x = 0;; ++x) { if (x >= 3) break; print(x); }");
        chkOut("0 1 2 ");
    }

    @Test
    public void testForRegularNoExpression() throws Exception, SynsException {
        execute("for (var x = 0; x < 3;) print(x++);");
        chkOut("0 1 2 ");
    }

    @Test
    public void testForRegularNothing() throws Exception, SynsException {
        execute("var x = 0; for (;;) { if (x >= 3) break; print(x++); }");
        chkOut("0 1 2 ");
    }

    @Test
    public void testForRegularBreak() throws Exception, SynsException {
        execute("print('Begin'); " +
                "for (var x = 0; x < 3; ++x) { print('F-Begin-' + x); break; print('F-End-' + x); } " +
                "print('End');");
        chkOut("Begin F-Begin-0 End ");
    }

    @Test
    public void testForRegularContinue() throws Exception, SynsException {
        execute("print('Begin'); " +
                "for (var x = 0; x < 3; ++x) { print('F-Begin-' + x); continue; print('F-End-' + x); } " +
                "print('End');");
        chkOut("Begin F-Begin-0 F-Begin-1 F-Begin-2 End ");
    }

    @Test
    public void testForEachArray() throws Exception, SynsException {
        execute("for (var x : ['A','B','C']) print(x);");
        chkOut("A B C ");
    }

    @Test
    public void testForEachList() throws Exception {
        execute("var a = new java.util.ArrayList(); for(var i=0; i<5;++i) a.add(i); " +
                "for(var x : a) print(x);");
        chkOut("0 1 2 3 4 ");
    }

    @Test
    public void testForEachBreak() throws Exception, SynsException {
        execute("print('Begin'); " +
                "for (var x : ['A','B','C']) { print('F-Begin-' + x); break; print('F-End-' + x); } " +
                "print('End');");
        chkOut("Begin F-Begin-A End ");
    }

    @Test
    public void testForEachContinue() throws Exception, SynsException {
        execute("print('Begin'); " +
                "for (var x : ['A','B','C']) { print('F-Begin-' + x); continue; print('F-End-' + x); } " +
                "print('End');");
        chkOut("Begin F-Begin-A F-Begin-B F-Begin-C End ");
    }

    @Test
    public void testBlock() throws Exception, SynsException {
        execute("print('Begin'); { print('Block'); } print('End');");
        chkOut("Begin Block End ");
    }

    @Test
    public void testReturnNoExpression() throws Exception, SynsException {
        execute("function fn() { print('fn-Begin'); return; print('fn-End'); } fn();");
        chkOut("fn-Begin ");
    }

    @Test
    public void testReturnExpression() throws Exception, SynsException {
        execute("function fn() { print('fn-Begin'); return 123; print('fn-End'); } print(fn());");
        chkOut("fn-Begin 123 ");
    }

    @Test
    public void testReturnInBlockValue() throws Exception {
        execute("var b = { print('InBlock'); return 123; }; print(b());");
        chkOut("InBlock 123 ");
    }

    @Test
    public void testThrow() throws Exception {
        try {
            execute("print('start'); throw new RuntimeException('FromScript'); print('end');");
            fail();
        } catch (ThrowSynsException e) {
            RuntimeException cause = (RuntimeException) e.getCause();
            assertEquals("FromScript", cause.getMessage());
        }
        chkOut("start ");
    }

    /*
     * Tests for try-catch-finally block.
     * Not all possible combinations are tested. Every part (try, catch, finally) can result in one
     * of five ways: (1) normally; (2) exception; (3) return; (4) break; (5) continue.
     * This gives 125 combinations.
     */

    @Test
    public void testTryCatch() throws Exception {
        execute("print('start');" +
                "try { print('try'); } catch (e) { print('catch ' + e.getClass().getName()); }" +
                "print('end');");
        chkOut("start try end ");
    }

    @Test
    public void testTryCatchFinally() throws Exception {
        execute("print('start');" +
                "try { print('try'); } catch (e) { print('catch ' + e.getClass().getName()); }" +
                "finally { print('finally'); }" +
                "print('end');");
        chkOut("start try finally end ");
    }

    @Test
    public void testTryCatchFinallyBreak() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try'); } catch (e) { print('catch ' + e.getClass().getName()); }" +
                "finally { print('finally-start'); break; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try finally-start end ");
    }

    @Test
    public void testTryCatchFinallyContinue() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try'); } catch (e) { print('catch ' + e.getClass().getName()); }" +
                "finally { print('finally-start'); continue; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try finally-start for-start 1 try finally-start " +
                "for-start 2 try finally-start end ");
    }

    @Test
    public void testTryCatchFinallyReturn() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "try { print('try'); } catch (e) { print('catch ' + e.getClass().getName()); }" +
                "finally { print('finally-start'); return 123; print('finally-end'); }" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start try finally-start 123 ");
    }

    @Test
    public void testTryExceptionCatch() throws Exception {
        execute("print('start');" +
                "try { print('try-start'); throw new RuntimeException('FromScript'); print('try-end'); }" +
                "catch (e) { print('catch(' + e + ')'); }" +
                "print('end');");
        chkOut("start try-start catch(java.lang.RuntimeException: FromScript) end ");
    }

    @Test
    public void testTryExceptionCatchFinally() throws Exception {
        execute("print('start');" +
                "try { print('try-start'); throw new RuntimeException('FromScript'); print('try-end'); }" +
                "catch (e) { print('catch(' + e + ')'); }" +
                "finally { print('finally'); }" +
                "print('end');");
        chkOut("start try-start catch(java.lang.RuntimeException: FromScript) finally end ");
    }

    @Test
    public void testTryExceptionCatchFinallyBreak() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); throw new RuntimeException('FromScript'); print('try-end'); }" +
                "catch (e) { print('catch(' + e + ')'); }" +
                "finally { print('finally-start'); break; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start catch(java.lang.RuntimeException: FromScript) " +
                "finally-start end ");
    }

    @Test
    public void testTryExceptionCatchFinallyContinue() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); throw new RuntimeException('FromScript'); print('try-end'); }" +
                "catch (e) { print('catch(' + e + ')'); }" +
                "finally { print('finally-start'); continue; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start catch(java.lang.RuntimeException: FromScript) finally-start " +
                "for-start 1 try-start catch(java.lang.RuntimeException: FromScript) finally-start " +
                "for-start 2 try-start catch(java.lang.RuntimeException: FromScript) finally-start end ");
    }

    @Test
    public void testTryExceptionCatchFinallyReturn() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "try { print('try-start'); throw new RuntimeException('FromScript'); print('try-end'); }" +
                "catch (e) { print('catch(' + e + ')'); }" +
                "finally { print('finally-start'); return 123; print('finally-end'); }" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start try-start catch(java.lang.RuntimeException: FromScript) finally-start 123 ");
    }

    @Test
    public void testTryFinally() throws Exception {
        execute("print('start');" +
                "try { print('try'); }" +
                "finally { print('finally'); }" +
                "print('end');");
        chkOut("start try finally end ");
    }

    @Test
    public void testTryFinallyBreak() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try'); }" +
                "finally { print('finally-start'); break; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try finally-start end ");
    }

    @Test
    public void testTryFinallyContinue() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try'); }" +
                "finally { print('finally-start'); continue; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try finally-start " +
                "for-start 1 try finally-start " +
                "for-start 2 try finally-start end ");
    }

    @Test
    public void testTryFinallyReturn() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "try { print('try'); }" +
                "finally { print('finally-start'); return 123; print('finally-end'); }" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start try finally-start 123 ");
    }

    @Test
    public void testTryBreakFinally() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); break; print('try-end'); }" +
                "finally { print('finally'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start finally end ");
    }

    @Test
    public void testTryBreakFinallyBreak() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); break; print('try-end'); }" +
                "finally { print('finally-start'); break; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start finally-start end ");
    }

    @Test
    public void testTryBreakFinallyContinue() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); break; print('try-end'); }" +
                "finally { print('finally-start'); continue; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start finally-start " +
                "for-start 1 try-start finally-start " +
                "for-start 2 try-start finally-start end ");
    }

    @Test
    public void testTryBreakFinallyReturn() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); break; print('try-end'); }" +
                "finally { print('finally-start'); return 123; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start for-start 0 try-start finally-start 123 ");
    }

    @Test
    public void testTryContinueFinally() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); continue; print('try-end'); }" +
                "finally { print('finally'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start finally " +
                "for-start 1 try-start finally " +
                "for-start 2 try-start finally end ");
    }

    @Test
    public void testTryContinueFinallyBreak() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); continue; print('try-end'); }" +
                "finally { print('finally-start'); break; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start finally-start end ");
    }

    @Test
    public void testTryContinueFinallyContinue() throws Exception {
        execute("print('start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); continue; print('try-end'); }" +
                "finally { print('finally-start'); continue; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('end');");
        chkOut("start for-start 0 try-start finally-start " +
                "for-start 1 try-start finally-start " +
                "for-start 2 try-start finally-start end ");
    }

    @Test
    public void testTryContinueFinallyReturn() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); continue; print('try-end'); }" +
                "finally { print('finally-start'); return 123; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start for-start 0 try-start finally-start 123 ");
    }

    @Test
    public void testTryReturnFinally() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); return 987; print('try-end'); }" +
                "finally { print('finally'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start for-start 0 try-start finally 987 ");
    }

    @Test
    public void testTryReturnFinallyBreak() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); return 987; print('try-end'); }" +
                "finally { print('finally-start'); break; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('fn-end');" +
                "return 555;" +
                "}" +
                "print(fn());");
        chkOut("fn-start for-start 0 try-start finally-start fn-end 555 ");
    }

    @Test
    public void testTryReturnFinallyContinue() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); return 987; print('try-end'); }" +
                "finally { print('finally-start'); continue; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('fn-end');" +
                "return 555;" +
                "}" +
                "print(fn());");
        chkOut("fn-start for-start 0 try-start finally-start " +
                "for-start 1 try-start finally-start " +
                "for-start 2 try-start finally-start fn-end 555 ");
    }

    @Test
    public void testTryReturnFinallyReturn() throws Exception {
        execute("function fn() {" +
                "print('fn-start');" +
                "for (var i = 0; i < 3; ++i) {" +
                "print('for-start ' + i);" +
                "try { print('try-start'); return 987; print('try-end'); }" +
                "finally { print('finally-start'); return 123; print('finally-end'); }" +
                "print('for-end ' + i);" +
                "}" +
                "print('fn-end');" +
                "}" +
                "print(fn());");
        chkOut("fn-start for-start 0 try-start finally-start 123 ");
    }
}
