![logo](misc/grappa-logo.png)

\[Original image courtesy of [clipartbest.com](http://www.clipartbest.com/clipart-Kin5EMyiq)\]

## What this is

This package allows you to write grammars (more precisely, parsing expression grammars, or
[PEG](http://en.wikipedia.org/wiki/Parsing_expression_grammar) for short; another name is "packrat"
grammars) entirely in Java. Unlike, for instance, ANTLR and JavaCC, parsers written with this
package do **not** require a pre-processing phase. Yes, this means you generate your parsers at
_runtime_. And there is no DSL.  Pure Java!

License is **Apache 2.0**.

This project has an IRC channel (`#grappa` on Freenode; server: `irc.freenode.net`) and two
dedicated Google groups: [grappa-users](http://groups.google.com/d/forum/grappa-users) and
[grappa-devel](http://groups.google.com/d/forum/grappa-devel). They are also available to post by
email.

The current version is **1.0.0** (released December 31, 2014).

Work has started on 1.1.x. See [here](https://github.com/parboiled1/grappa/wiki/Work-on-1.1.x) for
the current items being worked on, the plans etc.

## Motivation

This is a fork of the original [parboiled](https://github.com/sirthias/parboiled). Its author,
[Mathias Doenitz](https://github.com/sirthias), has since left the Java world for the Scala world
(with [parboiled2](https://github.com/sirthias/parboiled2)).

But parboiled does not deserve to be left stranded, nor its users. It is just too good. No other
package allows you to write grammars entirely in Java... And what is more, it performs really well.
This package is therefore dedicated to continuing its development.

Grappa requires Java 6 or later. **Starting with version 2.0, Grappa will require Java 7**.

### And, uh, the name?

Well, you write <i>gra</i>mmars and can then generate <i>pa</i>rsers for them. (I also happen to
like the [drink of the same name](http://www.istitutograppa.org/))

## Comparison with parboiled

Backwards compatibility with parboiled 1 is ensured for the 1.0.x timeline. **1.1.x and greater
will introduce incompatible changes**, however these changes will be documented.

See [this
page](https://github.com/parboiled1/grappa/wiki/Overview:-grappa-1.0.x-vs-parboiled-java) for an
overview of the changes; if you want the nitty gritty details, you can read the [release
notes](https://github.com/parboiled1/grappa/blob/master/RELEASE-NOTES.md) as well.

## Gradle/maven artifacts

Grappa is available on Maven Central. With Gradle:

```groovy
repositories {
    mavenCentral();
}

dependencies {
    compile(group: "com.github.parboiled1", name: "grappa", version: "1.0.0-beta.11");
}
```

## Example grammars

You can have a look at the [examples
package](https://github.com/parboiled1/grappa-examples). Note however that these
examples do not make use of the latest developments available in this project.

## How to build

You will need a JDK 6 or greater use this command to build and install into your local repository:

```
# Unix systems: Linux, MacOSX, *BSD
./gradlew clean test install
# Windows systems
gradlew.bat clean test install
```

