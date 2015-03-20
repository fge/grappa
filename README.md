![logo](misc/grappa-logo.png)

\[Original image courtesy of [clipartbest.com](http://www.clipartbest.com/clipart-Kin5EMyiq)\]

## Read me first

The license of this project is Apache 2.0.

## What this is

This package allows you to write grammars entirely in Java. Unlike, for instance, ANTLR and JavaCC,
parsers written with this package do **not** require a pre-processing phase. Yes, this means you
generate your parsers at _runtime_.  And there is **no DSL**.  Pure Java!

This is a fork of the original [parboiled](https://github.com/sirthias/parboiled). See also the
[debugger package](https://github.com/fge/grappa-debugger).

### And, uh, the name?

Well, you write <i>gra</i>mmars and can then generate <i>pa</i>rsers for them. (I also happen to
like the [drink of the same name](http://www.istitutograppa.org/))

## Examples

OK, so, the package is tested as much as possible etc, but this does not replace real life usage,
so...

### Projects using grappa

The following projects, all on GitHub, use grappa (**1.0.x**, see below):

* https://github.com/Offene-Bibel
* https://github.com/uscexp/grappa.extension
* https://github.com/haasted/grappa-xml-parser
* https://github.com/opennars/opennars

### More simple examples

I have put up a project, also using version 1.0.x, with some sample grammars; in particular, a
grammar which is able to fully parse any [JSON](http://tools.ietf.org/html/rfc7159):

https://github.com/fge/grappa-examples

## Versions

There are two major versions: 1.0.x and 2.0.x.

### 1.0.x

The 1.0.x branch is entirely compatible with parboiled, but with some added rules and other (mostly)
technical differences; see [this
page](https://github.com/fge/grappa/wiki/Overview:-grappa-1.0.x-vs-parboiled-java) for more details.

Requires Java 6.

The current version of this branch is **1.0.4** (released January 24, 2015):

```groovy
dependencies {
    compile(group: "com.github.parboiled1", name: "grappa", version: "1.0.4");
}
```

### 2.0.x

This version is in development; it will require Java 7. No "official" version is out for now, but if
you are curious, you can have a look at the [release
notes](https://github.com/fge/grappa/blob/master/RELEASE-NOTES.md).

Its first major feature compared to 1.0.x is an event-based parse runner, which is what is used to
collect data for the debugger; this parse runner has been [backported to
1.0.x](https://github.com/fge/grappa-tracer-backport); which means you can also use it to analyze
your parboiled grammars.

Other features are planned; see [here](https://github.com/fge/grappa/wiki/planned-features).
(warning: moving target!)

Preliminary versions are published on Maven central if you're interested, however beware that while
it is still `alpha`, the API may change without notice! Also, the group is not the same:

```groovy
dependencies {
    compile(group: "com.github.fge", name: "grappa", version: "2.0.0-alpha.3");
}
```

## Getting help

This project has an IRC channel (`#grappa` on Freenode; server: `irc.freenode.net`) and two
dedicated Google groups: [grappa-users](http://groups.google.com/d/forum/grappa-users) and
[grappa-devel](http://groups.google.com/d/forum/grappa-devel). They are also available to post by
email.

