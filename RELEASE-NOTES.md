### 2.0.0-alpha.3 (IN PROGRESS)

* Create GrappaException, InvalidGrammarException.
* Add .canMatchEmpty() to Rule; detect anomalous grammars at build time.
* Sequences now refuse to have an action as the first argument.
* EOI is given the boot as ch(0xffff), now uses an EndOfInputMatcher().
* Unify exceptions thrown by grammar build errors (InvalidGrammarException).
* Dump trace events in CSV format instead; use a parser to parse this CSV.
* Remove @BuildParseTree, @*Node{s} annotations and associated code.
* Remove @MemoizeMismatches and associated code.

### 2.0.0-alpha.2

* InputBuffer: new method .codePointAt(); rewrite unicode matchers.
* InputBuffer: .charAt(), .codePointAt() throw IAE if index is negative.
* Code pruning: nearly 10k lines of code removed.
* Add a .getType() method to Matcher with 4 types: TERMINAL, COMPOSITE, ACTION,
  PREDICATE.
* Change package to com.github.fge.
* Fix bugs with CharSequenceInputBuffer (backport from 1.0.x).
* New EndOfInputMatcher, and eof() in BaseParser to match.

### 2.0.0-alpha.1

* ValueStacks: remove .swap[3456]() methods, .put(Iterable).
* ValueStacks: null values are no more allowed.
* Use a modified version of jitescript CodeBlock in bytecode rewriters.
* A lot of classes/methods/variables are final.
* A lot of variables have been made private.
* Remove error recovery and associated methods/visitors.
* Remove all parse runners except the basic one.
* Remove MatcherVisitor; it was pretty much useless anyway.
* Add an EventBasedParseRunner; provide a tracer listener.
* Depend on jackson-databind 2.5.x.
* Update mockito and assertj dependencies.

