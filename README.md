## Read me first

The license of this project is Apache 2.0.

Requires Java 7 or later.

The latest versions are:

* development: **2.1.0-beta.1**;
* stable: **2.0.4**.

## What this is

This package allows you to write grammars entirely in Java. Unlike, for instance, ANTLR and JavaCC,
parsers written with this package do **not** require a pre-processing phase. Yes, this means you
generate your parsers at _runtime_. And there is **no DSL**. Pure Java!

See also the [debugger package](https://github.com/fge/grappa-debugger).

### And, uh, the name?

Well, you write <i>gra</i>mmars and can then generate <i>pa</i>rsers for them. (I also happen to
like the [drink of the same name](http://www.istitutograppa.org/))

## Versions

There are three major versions.

### 2.1.x

This version is currently in development. See
[here](https://github.com/fge/grappa/wiki/Changes:-2.0.x----2.1.x) for a list of
changes from 2.0.x.

### 2.0.x

The latest 2.0.x version is **2.0.4**. Using gradle:

```groovy
dependencies {
    compile(group: "com.github.fge", name: "grappa", version: "2.0.4");
}
```

## Projects using grappa

### Parse tree generator

Grappa in itself does not generate a parse tree. While you can do it yourself, a module exists which
allows you to do exactly that with little effort:

https://github.com/ChrisBrenton/grappa-parsetree

A visualizer (using Graphviz) also exists for such parse trees:
https://github.com/fge/grappa-parsetree-visual

### More advanced usage...

A project, currently in development, will allow you to generate a grappa parser
from a formal grammar (BNF, EBNF, WSN, others) at runtime. See here:

https://github.com/ChrisBrenton/grappa-formal

### Other usages

The following projects, all on GitHub, use grappa:

* https://github.com/Offene-Bibel/converter (1.0.x);
* https://github.com/uscexp/grappa.extension (1.0.x);
* https://github.com/haasted/grappa-xml-parser (1.0.x);
* https://github.com/opennars/opennars (2.0.x);
* https://github.com/litesolutions/sonar-sslr-grappa (2.0.x);
* https://github.com/hatstand0/Staskken (2.0.x).

## Examples

Unfortunately, I still haven't documented the project properly.

Here is a link to a tutorial by [Joseph Ottinger](https://github.com/jottinger)
explaining a possible use of grappa:

http://enigmastation.com/2016/03/07/simple-grappa-tutorial/

The code for this tutorial is [available on
GitHub](https://github.com/jottinger/grappaexample).

I have also put up a project with some sample grammars; in particular, a grammar
which is able to fully parse any [JSON](http://tools.ietf.org/html/rfc7159):

https://github.com/fge/grappa-examples

## Getting help

This project has an IRC channel (`#grappa` on Freenode; server: `irc.freenode.net`) and two
dedicated Google groups: [grappa-users](http://groups.google.com/d/forum/grappa-users) and
[grappa-devel](http://groups.google.com/d/forum/grappa-devel). They are also available to post by
email.

