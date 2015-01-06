/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.matchervisitors;

import com.github.parboiled1.grappa.matchers.join.JoinMatcher;
import com.github.parboiled1.grappa.matchers.trie.TrieMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeCharMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeRangeMatcher;
import org.parboiled.matchers.ActionMatcher;
import com.github.parboiled1.grappa.matchers.AnyMatcher;
import com.github.parboiled1.grappa.matchers.AnyOfMatcher;
import com.github.parboiled1.grappa.matchers.CharIgnoreCaseMatcher;
import com.github.parboiled1.grappa.matchers.CharMatcher;
import com.github.parboiled1.grappa.matchers.CharRangeMatcher;
import com.github.parboiled1.grappa.matchers.CustomMatcher;
import com.github.parboiled1.grappa.matchers.EmptyMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import com.github.parboiled1.grappa.matchers.Matcher;
import com.github.parboiled1.grappa.matchers.NothingMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.SequenceMatcher;
import com.github.parboiled1.grappa.matchers.predicates.TestMatcher;
import com.github.parboiled1.grappa.matchers.predicates.TestNotMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;

/**
 * The interface to be implemented by all visitors of {@link Matcher}s.
 *
 * @param <R> the return value of this visitor
 * @see <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern on Wikipedia</a>
 */
public interface MatcherVisitor<R> {

    /*
     * Actions
     */
    R visit(ActionMatcher matcher);

    /*
     * Simple matchers
     */
    R visit(CharMatcher matcher);

    R visit(CharIgnoreCaseMatcher matcher);

    R visit(UnicodeCharMatcher matcher);

    R visit(AnyOfMatcher matcher);

    R visit(CharRangeMatcher matcher);

    R visit(UnicodeRangeMatcher matcher);

    R visit(AnyMatcher matcher);

    R visit(TrieMatcher matcher);

    R visit(EmptyMatcher matcher);

    R visit(NothingMatcher matcher);

    /*
     * "Composite" matchers
     */

    R visit(JoinMatcher matcher);

    R visit(SequenceMatcher matcher);

    R visit(ZeroOrMoreMatcher matcher);

    R visit(OneOrMoreMatcher matcher);

    R visit(OptionalMatcher matcher);

    R visit(FirstOfMatcher matcher);

    /*
     * Predicates
     */
    R visit(TestMatcher matcher);

    R visit(TestNotMatcher matcher);

    /*
     * Custom
     */
    R visit(CustomMatcher matcher);
}
