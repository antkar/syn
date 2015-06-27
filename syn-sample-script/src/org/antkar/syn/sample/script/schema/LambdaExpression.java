package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;
import org.antkar.syn.SynInit;
import org.antkar.syn.TextPos;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Lambda expression.
 */
public class LambdaExpression extends Expression {
    @SynField
    private LambdaParameters synParameters;
    
    @SynField
    private Expression synExpression;
    
    private FunctionObject function;
    
    public LambdaExpression(){}
    
    @SynInit
    private void synInit() {
        String scopeName = "lambda " + synParameters.getStartPos();
        List<StringToken> parameters = synParameters.getParameters();
        function = synExpression.getFunctionObjectFactory().create(scopeName, parameters);
    }

    @Override
    TextPos getStartTextPos() {
        return synParameters.getStartPos();
    }

    @Override
    Value evaluate0(ScriptScope scope) {
        return Value.forFunction(scope, function);
    }
    
    @Override
    public String toString() {
        return synParameters + " -> " + synExpression;
    }
}
