### 2.1.0-beta.2

* Fix stupid bug with Characters

### 2.1.0-beta.1

* Replace the value stack with a better performing one, based on arrays.
* Make InputBuffer implement CharSequence.
* New EventBusParser class.
* Name change: ParseRunnerListener -> ParseEventListener.
* Gradle 2.12.
* Experimental code to generate parsers from classes in custom classloaders.

### 2.0.4

* Add repeat() rule; remove {Zero,One}OrMoreMatcher since they are special cases
  of the new RepeatMatcher.
* Add popAs(), peekAs() to retrieve stack values casted to a subclass.
* join() (and using()) now allow several arguments: join(rule1,
  rule2).using(rule2, rule3) etc.
* Add regex() to match a Java regular expression (using .lookingAt()).
* Add longestString{,IgnoreCase}() as aliases to trie{,IgnoreCase}()

### 2.0.3

* Fix bugs with trieIgnoreCase()

### 2.0.2

* Significant performance improvements in trieIgnoreCase()

### 2.0.1

* trieIgnoreCase()

### 2.0.0

* trie() now accepts single character strings.
* When generating a label in the tracing CSV, escape all ASCII controls (this
  includes \r and \n).

### 2.0.0-beta.4

* Simplify firstOf() with strings: replace with a trie()
* Simplify string() and ignoreCase()
* Get rid of MatcherPath

### 2.0.0-beta.3

* ListeningParseRunner: throw exceptions thrown by parse runner listeners

### 2.0.0-beta.2

* Regression: clear the value stack when reusing the same ParseRunner between to
  runs

### 2.0.0-beta.1

* First beta version

