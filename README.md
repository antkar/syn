Syn
===

## Overview

Syn - a configurable parser for context-free grammars, written in Java.

Features:
* Grammar is set at runtime, not at compile time.
* An extended BNF notation is used for the grammar.
* Generation of an abstract syntax tree (AST) with attributes.
* Mapping AST nodes to objects of arbitrary Java classes (see below).
* Uses GLR parsing algorithm.

## Getting Started

Consider a fragment of a grammar:
```Java
@Statement : IfStatement | OtherStatement ;
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

See [SynBinder](http://antkar.github.io/syn/javadoc/org/antkar/syn/binder/SynBinder.html)
and [SynParser](http://antkar.github.io/syn/javadoc/org/antkar/syn/SynParser.html) classes documentation
for more details.

## Samle: a Scripting Language

A simple Javascript-like language interpreter, which uses Syn to parse source code and build a convenient syntax tree.

Features:

* Dynamic type-checking and binding.
* Lambda expressions.
* Access to Java library classes.
* Possibility to implement Java interfaces.

See the [Syn grammar of the language](https://github.com/antkar/syn/blob/master/syn-sample-script/src/main/java/org/antkar/syn/sample/script/schema/Script_grammar.txt).

### Code Examples

Using Java library classes:

```JavaScript
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

var frame = new JFrame("Demo");
frame.setSize(400, 300);
frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
frame.setLocationRelativeTo(null);
frame.setVisible(true);
```

Try-catch-finally block:

```JavaScript
var file = new java.io.RandomAccessFile("Demo.syns", "r");
try {
    System.out.println("File size: " + file.length());
} catch (e) {
    System.err.println("Unable to get file size.");
    e.printStackTrace();
} finally {
    file.close();
}
```

For-each loop statement:

```JavaScript
var dir = new java.io.File(".");
for (var subFile : dir.listFiles()) {
    System.out.println(subFile.getName());
}
```

Lambda expressions:

```JavaScript
function count(collection, predicate) {
    var result = 0;
    for (var element : collection) if (predicate(element)) ++result;
    return result;
}

var numbers = [1, 2, 3, 4, 5];
var oddCount = count(numbers, x -> (x % 2) != 0); // Lambda expression.
System.out.println(oddCount);
```

Implementing Java interfaces:

```JavaScript
SwingUtilities.invokeLater({
    //Implements java.lang.Runnable interface, method run().
    System.out.println("In AWT event thread.");
});

frame.addMouseListener({
    //Implements java.awt.event.MouseListener interface.

    function mousePressed(e) {
        System.out.println("Mouse pressed.");
    }

    function mouseReleased(e) {
        System.out.println("Mouse released.");
    }

    //Other methods will have default empty implementations.
});
```

Classes:

```JavaScript
class Foo {
    var x;
    var y;

    public function Foo(x, y) {// Constructor.
        this.x = x;
        this.y = y;
    }

    public function print() {
        System.out.println("Foo(" + mx + ", " + my + ")");
    }

    public function getX() = x;  // Short function notation.
    public function getY() = y;
}

var foo = Foo(5, 10);
foo.print();
```

Explicit primitive type casting:

```JavaScript
var x = (int) 123.456;
System.out.println(x);
var y = (double) 789;
System.out.println(y);
```

## Sample Snake Game

Written in the sample scripting language. Features:

* UI is implemented via Java AWT.
* Generation of random symmetric levels.
* Smooth snake movement.
* Autosave, autoresume.

See the source code: [Snake.syns](https://github.com/antkar/syn/blob/master/syn-sample-script/sample/Snake.syns), [Level.syns](https://github.com/antkar/syn/blob/master/syn-sample-script/sample/Level.syns), [View.syns](https://github.com/antkar/syn/blob/master/syn-sample-script/sample/View.syns), [World.syns](https://github.com/antkar/syn/blob/master/syn-sample-script/sample/World.syns), [Utils.syns](https://github.com/antkar/syn/blob/master/syn-sample-script/sample/Utils.syns).

![Screenshot](https://antkar.github.io/syn/img/snake/snakec.png)

### How to Build and Run?

To build, clone the repository and execute in the root directory (needs Maven 3):

```
mvn package
```

To run, use `.sh` / `.cmd` scripts in the `syn-sample-script` directory:

* `script.sh` / `script.cmd` - command-line interpreter
* `console.sh` / `console.cmd` - GUI interpreter
* `snake.sh` / `snake.cmd` - sample game

