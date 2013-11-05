Syn
===

## Overview

Syn - a configurable parser for context-free grammars, written in Java.

Features:
* Grammar is set at run time, not at compile time.
* An extended BNF notation is used for the grammar.
* Generation of an abstract syntax tree (AST) with attributes:
* Mapping AST nodes to objects of arbitrary Java classes (see below).
* Uses GLR parsing algorithm.

## Getting Started

Consider a fragment of a grammar:
```Java
Statement : IfStatement | OtherStatement ;
IfStatement : "if" "(" expr=Expression ")" tStmt=Statement ("else" fStmt=Statement)? ;
```

And Java classes:
```Java
public abstract class Statement {
	public abstract void execute(); 
}

public class IfStatement extends Statement {
	@SynField
	private Expression expr;
	
	@SynField
	private Statement tStmt;
	
	@SynField
	private Statement fStmt;
	
	@Override
	public void execute() {
		if (expr.evaluate()) {
			tStmt.execute();
		} else if (fStmt != null) {
			fStmt.execute();
		}
	}	
}
```

Syn parses a text and creates corresponding Java objects:
```Java
SynBinder<Statement> binder = new SynBinder<>(Statement.class, grammar);
Statement statement = binder.parse(text);
statement.execute();
```

See SynBinder and SynParser classes documentation for more details.
