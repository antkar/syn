package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Function object implemented by an expression (used in short-form functions, lambda expressions).
 */
class ExpressionFunctionObject extends FunctionObject {
    private final Expression expression;

    private ExpressionFunctionObject(String scopeName, List<StringToken> parameters, Expression expression) {
        super(scopeName, parameters);
        this.expression = expression;
    }

    @Override
    Value execute(ScriptScope scope) throws SynsException {
        return expression.evaluate(scope);
    }

    static final class Factory implements FunctionObject.Factory {
        private final Expression expression;

        Factory(Expression expression) {
            super();
            this.expression = expression;
        }

        @Override
        public FunctionObject create(String scopeName, List<StringToken> parameters) {
            return new ExpressionFunctionObject(scopeName, parameters, expression);
        }
    }
}
