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

