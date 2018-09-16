package org.antkar.syn.sample.script.rt.value;

import org.antkar.syn.sample.script.rt.SynsException;

/**
 * A value which can be implicitly adapted to a Java interface implementation (e. g. function
 * value).
 */
interface AdaptableToJavaInterface {
    /** Calls the default function associated with this value. */
    Value call(RValue[] arguments) throws SynsException;

    /** Checks if this value has an associated function with the given name. */
    boolean hasFunction(String name);

    /** Calls an associated function with the given name. */
    Value callFunction(String name, RValue[] arguments) throws SynsException;
}
