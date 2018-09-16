package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Function object: can be called with parameters and return a value.
 */
public abstract class FunctionObject {
    private final String scopeName;

    private final List<StringToken> parameters;

    FunctionObject(String scopeName, List<StringToken> parameters) {
        this.scopeName = scopeName;
        this.parameters = parameters;
    }

    public final String getScopeName() {
        return scopeName;
    }

    public final Value call(ScriptScope scope, RValue[] arguments) throws SynsException {
        ScriptScope argumentsScope = scope.nestedFunctionScope(scopeName);

        for (int i = 0; i < parameters.size(); ++i) {
            StringToken param = parameters.get(i);
            Value value = i < arguments.length ? arguments[i] : Value.forNull();
            Value variable = Value.newVariable(value);
            argumentsScope.addValue(param, variable);
        }

        Value result = execute(argumentsScope);
        return result;
    }

    abstract Value execute(ScriptScope scope) throws SynsException;

    interface Factory {
        FunctionObject create(String scopeName, List<StringToken> parameters);
    }
}
