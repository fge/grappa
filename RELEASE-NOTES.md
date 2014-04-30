### 1.0.0-beta.2

* Deprecate a lot of classes, replace with Guava instead.
* Code cleanup/update/reformatting (not finished yet).
* Rule methods are now lowercase; deprecate uppercase rules.
* Add all rules defined by
  [RFC 5234, Appendix B, section 1](https://tools.ietf.org/html/rfc5234#appendix-B.1), except for `LWSP`.

### 1.0.0-beta.1

* Make all RuleMethodProcessors public (were package private); document them,
  change package.
* Document all parser/rule annotations.
* Rewrite ParserStatistics; add custom matcher.
* Depend on mockito, AssertJ for tests.
* Add UnicodeChar and UnicodeRange.
* Add dependency on Guava.
* ASM 5.0.1: make package work with Java 8.
* Implement an InputBuffer over a CharSequence.
* Deprecate a lot of packages which are in Guava (in fact, most of them _were_
  copied from Guava in the first place).
* Make javadoc compatible with Java 8.

