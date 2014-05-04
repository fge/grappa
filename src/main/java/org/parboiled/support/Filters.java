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

package org.parboiled.support;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.parboiled.Context;
import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.common.Tuple2;
import org.parboiled.matchers.Matcher;
import org.parboiled.parserunners.TracingParseRunner;

import java.util.HashSet;
import java.util.Set;

import static org.parboiled.matchers.MatcherUtils.unwrap;
import static org.parboiled.trees.GraphUtils.hasChildren;

public final class Filters
{
    private Filters()
    {
    }

    /**
     * A predicate for Node tree printing, suppresses printing of parse tree
     * nodes for Optional rules that do not have sub nodes.
     */
    public static final Predicate<Node<Object>> SKIP_EMPTY_OPTS
        = new Predicate<Node<Object>>()
    {
        @Override
        public boolean apply(final Node<Object> input)
        {
            return hasChildren(input)
                || input.getEndIndex() != input.getStartIndex()
                || !"Optional".equals(input.getLabel());
        }
    };

    /**
     * A predicate for Node tree printing, suppresses printing of parse tree
     * nodes for ZeroOrMore rules that do not have sub nodes.
     */
    public static final Predicate<Node<Object>> SKIP_EMPTY_ZOMS
        = new Predicate<Node<Object>>()
    {
        @Override
        public boolean apply(final Node<Object> input)
        {
            return hasChildren(input)
                || input.getEndIndex() != input.getStartIndex()
                || !"ZeroOrMore".equals(input.getLabel());
        }
    };

    /**
     * A predicate for Node tree printing, suppresses printing of parse tree
     * nodes for Optional and ZeroOrMore rules that do not have sub nodes.
     */
    public static final Predicate<Node<Object>> SKIP_EMPTY_OPTS_AND_ZOMS
        = Predicates.and(SKIP_EMPTY_OPTS, SKIP_EMPTY_ZOMS);

    /**
     * A predicate for rule tree printing. Prevents SOEs by detecting and
     * suppressing loops in the rule tree.
     *
     * @return a predicate
     */
    public static Predicate<Matcher> preventLoops()
    {
        return new Predicate<Matcher>()
        {
            private final Set<Matcher> visited = new HashSet<Matcher>();

            @Override
            public boolean apply(final Matcher input)
            {
                return visited.add(unwrap(input));
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all input in the given
     * range of input lines.
     *
     * @param firstLine the number of the first input line to generate tracing
     * message for
     * @param lastLine the number of the last input line to generate tracing
     * message for
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> lines(
        final int firstLine, final int lastLine)
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                final int line = input.a.getInputBuffer()
                    .getPosition(input.a.getCurrentIndex()).line;
                return firstLine <= line && line <= lastLine;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all input in the given
     * range of input lines.
     *
     * @param firstLine the number of the first input line to generate tracing
     * message for
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> fromLine(
        final int firstLine)
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                return input.a.getInputBuffer()
                    .getPosition(input.a.getCurrentIndex()).line >= firstLine;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all input in the given
     * range of input lines.
     *
     * @param lastLine the number of the last input line to generate tracing
     * message for
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> untilLine(
        final int lastLine)
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> tuple)
            {
                return tuple.a.getInputBuffer()
                    .getPosition(tuple.a.getCurrentIndex()).line <= lastLine;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all given rules and
     * their sub rules.
     *
     * @param rules the rules to generate tracing message for
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> rules(
        final Rule... rules)
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                final MatcherPath path = input.a.getPath();
                for (final Rule rule: rules)
                    if (path.contains((Matcher) rule))
                        return true;
                return false;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all given rules
     * (without their sub rules).
     *
     * @param rules the rules to generate tracing message for
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> onlyRules(
        final Rule... rules)
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                for (final Rule rule: rules)
                    if (input.a.getMatcher() == rule)
                        return true;
                return false;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all sub rules of the
     * given rules.
     *
     * @param rules the rules whose sub rules to generate tracing message for
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> rulesBelow(
        final Rule... rules)
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                final MatcherPath path = input.a.getPath();
                Matcher matcher;
                for (final Rule rule: rules) {
                    matcher = (Matcher) rule;
                    if (input.a.getMatcher() != matcher
                        && path.contains(matcher))
                        return true;
                }
                return false;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all matched rules.
     *
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> onlyMatches()
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                return input.b;
            }
        };
    }

    /**
     * A predicate usable as a filter (element) of a {@link TracingParseRunner}.
     * Enables printing of rule tracing log messages for all mismatched rules.
     *
     * @return a predicate
     */
    public static Predicate<Tuple2<Context<?>, Boolean>> onlyMismatches()
    {
        return new Predicate<Tuple2<Context<?>, Boolean>>()
        {
            @Override
            public boolean apply(final Tuple2<Context<?>, Boolean> input)
            {
                return !input.b;
            }
        };
    }
}
