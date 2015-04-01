## Read me first

The license of this project is Apache 2.0.

The latest version is **2.0.0-beta.2**.

## What this is

This package allows you to write grammars entirely in Java. Unlike, for instance, ANTLR and JavaCC,
parsers written with this package do **not** require a pre-processing phase. Yes, this means you
generate your parsers at _runtime_. And there is **no DSL**. Pure Java!

See also the [debugger package](https://github.com/fge/grappa-debugger).

### And, uh, the name?

Well, you write <i>gra</i>mmars and can then generate <i>pa</i>rsers for them. (I also happen to
like the [drink of the same name](http://www.istitutograppa.org/))

## Versions

There are two major versions.

### 2.0.x

The latest 2.0.x version is **2.0.0-beta.2**.

Note that even though it is beta at the moment, it is **highly recommended**
that new projects starting using Grappa use this one and not 1.0.x; see below.

```groovy
dependencies {
    compile(group: "com.github.fge", name: "grappa", version: "2.0.0-beta.2");
}
```

### 1.0.x

Grappa originally started its life as a fork of
[parboiled](https://github.com/sirthias/parboiled). It has since then evolved to
become its own beast, but if you still have projects using Parboiled, then you
can use this version, which is entirely compatible (although it [does bring more
features](https://github.com/fge/grappa/wiki/Overview:-grappa-1.0.x-vs-parboiled-java)).

```groovy
dependencies {
    compile(group: "com.github.parboiled1", name: "grappa", version: "1.0.4");
}
```

**No new development will occur on that version**.

See [here](https://github.com/fge/grappa/wiki/Changes-from-grappa-1.0.x) for a
list of changes between 1.0.x and 2.0.x.

## Projects using grappa

OK, so, the package is tested as much as possible etc, but this does not replace real life usage,
so...

The following projects, all on GitHub, use grappa:

* https://github.com/Offene-Bibel/converter (1.0.x);
* https://github.com/uscexp/grappa.extension (1.0.x);
* https://github.com/haasted/grappa-xml-parser (1.0.x);
* https://github.com/opennars/opennars (1.0.x);
* https://github.com/litesolutions/sonar-sslr-grappa (2.0.x);
* https://github.com/hatstand0/Staskken (2.0.x).

## Examples

I have put up a project with some sample grammars; in particular, a grammar
which is able to fully parse any [JSON](http://tools.ietf.org/html/rfc7159):

https://github.com/fge/grappa-examples

## Getting help

This project has an IRC channel (`#grappa` on Freenode; server: `irc.freenode.net`) and two
dedicated Google groups: [grappa-users](http://groups.google.com/d/forum/grappa-users) and
[grappa-devel](http://groups.google.com/d/forum/grappa-devel). They are also available to post by
email.

