package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;

/**
 * A function body defined via a block.
 */
public final class BlockFunctionBody extends FunctionBody {
    @SynField
    private Block synBlock;

    public BlockFunctionBody(){}

    @Override
    FunctionObject createFunction(String scopeName, List<StringToken> parameters) {
        return new BlockFunctionObject.Factory(synBlock).create(scopeName, parameters);
    }
}
