package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;

/**
 * Function body defined via an expression.
 */
public final class ExpressionFunctionBody extends FunctionBody {
    @SynField
    private Expression synExpression;

    public ExpressionFunctionBody(){}

    @Override
    FunctionObject createFunction(String scopeName, List<StringToken> parameters) {
        return synExpression.getFunctionObjectFactory().create(scopeName, parameters);
    }
}
