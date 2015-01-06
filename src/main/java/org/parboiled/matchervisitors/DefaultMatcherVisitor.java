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
import com.github.parboiled1.grappa.matchers.AbstractMatcher;
import org.parboiled.matchers.ActionMatcher;
import com.github.parboiled1.grappa.matchers.AnyMatcher;
import com.github.parboiled1.grappa.matchers.AnyOfMatcher;
import com.github.parboiled1.grappa.matchers.CharIgnoreCaseMatcher;
import com.github.parboiled1.grappa.matchers.CharMatcher;
import com.github.parboiled1.grappa.matchers.CharRangeMatcher;
import com.github.parboiled1.grappa.matchers.CustomMatcher;
import com.github.parboiled1.grappa.matchers.EmptyMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import com.github.parboiled1.grappa.matchers.NothingMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.TestMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;

/**
 * A basic {@link MatcherVisitor} implementation that delegates all visiting methods to one default value method.
 *
 * @param <R> the return value of this visitor
 */
public abstract class DefaultMatcherVisitor<R>
    implements MatcherVisitor<R>
{
    @Override
    public R visit(final ActionMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final AnyMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final CharIgnoreCaseMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final CharMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final UnicodeCharMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final CharRangeMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final UnicodeRangeMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final AnyOfMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final CustomMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final TrieMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final EmptyMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final FirstOfMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final NothingMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final JoinMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final OneOrMoreMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final OptionalMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final SequenceMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final TestMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final TestNotMatcher matcher)
    {
        return defaultValue(matcher);
    }

    @Override
    public R visit(final ZeroOrMoreMatcher matcher)
    {
        return defaultValue(matcher);
    }

    /**
     * Returns the default value for all visiting methods that have not been
     * overridden.
     *
     * @param matcher the matcher
     * @return the return value (null by default)
     */
    protected R defaultValue(final AbstractMatcher matcher)
    {
        return null;
    }
}
