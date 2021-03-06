package org.antkar.syn.sample.script.schema;

import java.util.Arrays;
import java.util.List;

import org.antkar.syn.TextPos;
import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;

/**
 * Lambda expression formal parameters in parentheses, e. g. (a, b, c) -> ? or () -> ?.
 */
public final class MultipleLambdaParameters extends LambdaParameters {
    @SynField
    private TextPos synPos;

    @SynField
    private StringToken[] synNames;

    public MultipleLambdaParameters(){}

    @Override
    TextPos getStartPos() {
        return synPos;
    }

    @Override
    List<StringToken> getParameters() {
        return Arrays.asList(synNames);
    }

    @Override
    public String toString() {
        return Arrays.toString(synNames);
    }
}
