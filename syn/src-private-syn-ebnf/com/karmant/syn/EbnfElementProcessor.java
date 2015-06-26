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

/**
 * An interface used to process each particular EBNF element type in a specific way.
 * Similar to the Visitor pattern.
 *
 * @param <T> the type of the value returned by processing methods.
 */
interface EbnfElementProcessor<T> {
    
    T processValueElement(EbnfValueElement element) throws SynException;

    T processNonterminalElement(EbnfNonterminalElement element) throws SynException;

    T processTerminalElement(EbnfTerminalElement element) throws SynException;

    T processOptionalElement(EbnfOptionalElement element) throws SynException;

    T processNestedElement(EbnfNestedElement element) throws SynException;

    T processRepetitionElement(EbnfRepetitionElement element) throws SynException;
}
