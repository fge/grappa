### 1.0.3

* TrieMatcher: do accept visitors. Meh.

### 1.0.2

* build.gradle: enforce Java compiler version.
* Use a binary search to get the line range for an index

### 1.0.1

* Make CharSequenceInputBuffer use daemon threads for its LineCounters (fixes
  issue #20).

### 1.0.0

* Convert (nearly) all rule labels to lowercase.
* Dependency updates.

### 1.0.0-beta.11

* Recompile with JDK 6 :/

### 1.0.0-beta.10

* Completely refactor the event-based parser.
* Depend on, and use, jitescript.
* ParsingResult: introduce .isSuccess().
* Bug fixes in join() rule.
* Deprecated DefaultInputBuffer in favor of CharSequenceInputBuffer; as a consequence, IntArrayStack
  is deprecated as well.
* Use a Guava LoadingCache to load classes from internal names; deprecate old AsmUtils synchronized
  cache.
* Update all dependencies.
* Upgrade gradle (1.12 -> 2.2).
* Make name, description appear in repository pom.

### 1.0.0-beta.9

* BaseParser: make trie() public...
* Javadoc fixes

### 1.0.0-beta.8

* Event-based parser: now possible to register listeners to consume parser productions.
* Use pitest 0.34-SNAPSHOT: cures the bug with disparaging sources when two classes have the same
  name but are in different packages.
* Downgrade to gradle 1.11: 1.12 does not play well with IDEA...
* Add -Xlint:unchecked to compile options.

### 1.0.0-beta.7

* Fix bug with console output reporting in RecoveringParseRunner; clarify .getLog() deprecation (see
  [here](https://github.com/parboiled1/grappa/issues/2)).

### 1.0.0-beta.6

* New trie matcher for multiple string matchings.
* Use Opcodes.ASM5 for ASM API compatibility (was ASM4).
* Include join() and trie() in BaseParser; deprecate JoinParser.

### 1.0.0-beta.5

* Major code cleanup: reformatting, intents with @Documented annotations, a lot of @Deprecated
  classes/methods/constructors. Cleanup for 1.1 will be massive!
* Fix one brown paper bag bug with CharSequenceInputBuffer: it always returned 0 when asked for the
  number of lines... But then tests did not cover this, and still do not...
* Replace custom implementations of linked lists etc with standard JDK classes.
* Tests: get rid of 80+% of tests depending on string outputs; replace them with assertions instead.
* Tests: integrate [mutation testing](https://github.com/hcoles/pitest).
* Deprecate IndentDedentInputBuffer: used characters are of common use in some languages (French,
  among others).
* Replace digest implementation for instruction groups; now means generating a group name does not
  have to be synchronized anymore.
* JoinMatcher: check each time that the matched content is not empty... No choice at this moment.

### 1.0.0-beta.4

* New, experimental `join()` rule.
* Lots of code cleanup; start transform code refactoring.
* Also generate 1.6 bytecode for rule methods... Meh.

### 1.0.0-beta.3

* Add missing match for '3' in hexDigit{,upperCase()}! Oops...

### 1.0.0-beta.2

* Deprecate a lot of classes, replace with Guava instead.
* Code cleanup/update/reformatting (not finished yet).
* Rule methods are now lowercase; deprecate uppercase rules.
* Add all rules defined by
  [RFC 5234, Appendix B, section 1](https://tools.ietf.org/html/rfc5234#appendix-B.1), except for
  `LWSP`.
* Depend on asm-debug-all: asm is critical and we want to reliably debug into it.

### 1.0.0-beta.1

* Make all RuleMethodProcessors public (were package private); document them, change package.
* Document all parser/rule annotations.
* Rewrite ParserStatistics; add custom matcher.
* Depend on mockito, AssertJ for tests.
* Add UnicodeChar and UnicodeRange.
* Add dependency on Guava.
* ASM 5.0.1: make package work with Java 8.
* Implement an InputBuffer over a CharSequence.
* Deprecate a lot of packages which are in Guava (in fact, most of them _were_ copied from Guava in
  the first place).
* Make javadoc compatible with Java 8.

