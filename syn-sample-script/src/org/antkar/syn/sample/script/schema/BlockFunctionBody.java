package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;

/**
 * A function body defined via a block.
 */
public class BlockFunctionBody extends FunctionBody {
    @SynField
    private Block synBlock;
    
    public BlockFunctionBody(){}

    @Override
    FunctionObject createFunction(String scopeName, List<StringToken> parameters) {
        return new BlockFunctionObject.Factory(synBlock).create(scopeName, parameters);
    }
}
