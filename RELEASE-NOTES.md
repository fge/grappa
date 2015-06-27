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

