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

