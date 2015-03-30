## Core changes

### `groupId` change

The (maven) `groupId` is now `com.github.fge`; the name is still `grappa`.

### Java 7 required

Grappa 1.0.x required Java 6.

## API changes

### Namespaces

The whole project has seen a complete overhaul of its namespace; what was under
`org.parboiled` is now under `com.github.fge.grappa`, and subpackages have
changed too.

### Parser creation

Before:

```java
Parboiled.createParser(MyParser.class);
```

Now:

```java
Grappa.createParser(MyParser.class);
```

### Rules

All rules which appeared in grappa 1.0.x are there; however, UpperCaseNamed
rules have _disappeared_.

### Parsers/ parse runners

All other parse runners other than the `ListeningParseRunner` have disappeared.
the latter is a rename of `EventBusParseRunner`.

The `BaseParser` is still there; the `EventBusParser` has been renamed to
`ListeningParser`.

### Tracer

The tracer is included.

### Annotations

The following annotations have **disappeared** from parser classes/rules:

* `@BuildParseTree`;
* `@MemoMismatches`;
* `@SuppressNode`, `@SuppressSubNodes`, `@SkipNode`.

They are obsoleted by the new event based parse runner API.

### No more error recovery

It had only limited value in real life, and complicated the code way too much.

