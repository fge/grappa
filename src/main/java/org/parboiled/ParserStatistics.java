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

package org.parboiled;

import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.parboiled.matchers.ActionMatcher;
import org.parboiled.matchers.AnyMatcher;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.CharIgnoreCaseMatcher;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.CustomMatcher;
import org.parboiled.matchers.EmptyMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.FirstOfStringsMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.MemoMismatchesMatcher;
import org.parboiled.matchers.NothingMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.ProxyMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.StringMatcher;
import org.parboiled.matchers.TestMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.matchers.VarFramingMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;
import com.github.parboiled1.grappa.matchers.join.JoinMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeCharMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeRangeMatcher;
import org.parboiled.matchervisitors.MatcherVisitor;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WillBeFinal(version = "1.1")
public class ParserStatistics
    implements MatcherVisitor<ParserStatistics>
{
    private static final Joiner NEWLINE = Joiner.on('\n');
    private static final Joiner COMMA = Joiner.on(", ");

    /**
     * Set of "regular" matcher classes
     *
     * <p>Do not use directly! However, if you intend to develop matchers and
     * want them to show up in statistics, then you should add your matcher
     * class into this set, or the set below.</p>
     *
     * <p>Note: this is a Guava's {@link ImmutableSet}, so it can be shared
     * safely (and {@code Class} objects are final)</p>
     */
    @VisibleForTesting
    public static final Set<Class<? extends Matcher>> REGULAR_MATCHER_CLASSES;

    /**
     * See {@link #REGULAR_MATCHER_CLASSES}
     *
     * <p>However you should not have to use this one.</p>
     *
     * <p>Note: this is a Guava's {@link ImmutableSet}, so it can be shared
     * safely (and {@code Class} objects are final)</p>
     */
    @VisibleForTesting
    public static final Set<Class<? extends Matcher>> SPECIAL_MATCHER_CLASSES;

    static {
        ImmutableSet.Builder<Class<? extends Matcher>> builder;

        builder = ImmutableSet.builder();

        builder.add(AnyMatcher.class);
        builder.add(CharIgnoreCaseMatcher.class);
        builder.add(CharMatcher.class);
        builder.add(UnicodeCharMatcher.class);
        builder.add(CustomMatcher.class);
        builder.add(CharRangeMatcher.class);
        builder.add(UnicodeRangeMatcher.class);
        builder.add(AnyOfMatcher.class);
        builder.add(EmptyMatcher.class);
        builder.add(FirstOfMatcher.class);
        builder.add(FirstOfStringsMatcher.class);
        builder.add(NothingMatcher.class);
        builder.add(JoinMatcher.class);
        builder.add(OneOrMoreMatcher.class);
        builder.add(OptionalMatcher.class);
        builder.add(SequenceMatcher.class);
        builder.add(StringMatcher.class);
        builder.add(TestMatcher.class);
        builder.add(TestNotMatcher.class);
        builder.add(ZeroOrMoreMatcher.class);

        REGULAR_MATCHER_CLASSES = builder.build();

        builder = ImmutableSet.builder();

        builder.add(ProxyMatcher.class);
        builder.add(VarFramingMatcher.class);
        builder.add(MemoMismatchesMatcher.class);

        SPECIAL_MATCHER_CLASSES = builder.build();
    }

    private final Matcher root;
    private int totalRules;

    private final Map<Class<?>, MatcherStats<?>> regularMatcherStats
        = new LinkedHashMap<Class<?>, MatcherStats<?>>();

    private final Map<Class<?>, MatcherStats<?>> specialMatcherStats
        = new LinkedHashMap<Class<?>, MatcherStats<?>>();

    private final Set<Action<?>> actions = new HashSet<Action<?>>();
    private final Set<Class<?>> actionClasses = new HashSet<Class<?>>();

    public static ParserStatistics generateFor(final Rule rule)
    {
        Preconditions.checkNotNull(rule, "rule");
        final Matcher matcher = (Matcher) rule;
        return matcher.accept(new ParserStatistics(matcher));
    }

    private ParserStatistics(final Matcher root)
    {
        this.root = root;
        for (final Class<? extends Matcher> c: REGULAR_MATCHER_CLASSES)
            regularMatcherStats.put(c, MatcherStats.forClass(c, false));
        for (final Class<? extends Matcher> c: SPECIAL_MATCHER_CLASSES)
            specialMatcherStats.put(c, MatcherStats.forClass(c, true));
        countSpecials(root);
    }

    public int getTotalRules()
    {
        return totalRules;
    }

    public Map<Class<?>, MatcherStats<?>> getRegularMatcherStats()
    {
        return Collections.unmodifiableMap(regularMatcherStats);
    }

    public Map<Class<?>, MatcherStats<?>> getSpecialMatcherStats()
    {
        return Collections.unmodifiableMap(specialMatcherStats);
    }

    public Set<Action<?>> getActions()
    {
        return Collections.unmodifiableSet(actions);
    }

    public Set<Class<?>> getActionClasses()
    {
        return Collections.unmodifiableSet(actionClasses);
    }

    // MatcherVisitor interface

    @Override
    public ParserStatistics visit(final ActionMatcher matcher)
    {
        if (actions.add(matcher.action)) {
            totalRules++;
            actionClasses.add(matcher.action.getClass());
        }
        return this;
    }

    @Override
    public ParserStatistics visit(final AnyMatcher matcher)
    {
        return doVisit(matcher, AnyMatcher.class);
    }

    @Override
    public ParserStatistics visit(final CharIgnoreCaseMatcher matcher)
    {
        return doVisit(matcher, CharIgnoreCaseMatcher.class);
    }

    @Override
    public ParserStatistics visit(final CharMatcher matcher)
    {
        return doVisit(matcher, CharMatcher.class);
    }

    @Override
    public ParserStatistics visit(final UnicodeCharMatcher matcher)
    {
        return doVisit(matcher, UnicodeCharMatcher.class);
    }

    @Override
    public ParserStatistics visit(final CustomMatcher matcher)
    {
        return doVisit(matcher, CustomMatcher.class);
    }

    @Override
    public ParserStatistics visit(final CharRangeMatcher matcher)
    {
        return doVisit(matcher, CharRangeMatcher.class);
    }

    @Override
    public ParserStatistics visit(final UnicodeRangeMatcher matcher)
    {
        return doVisit(matcher, UnicodeRangeMatcher.class);
    }

    @Override
    public ParserStatistics visit(final AnyOfMatcher matcher)
    {
        return doVisit(matcher, AnyOfMatcher.class);
    }

    @Override
    public ParserStatistics visit(final EmptyMatcher matcher)
    {
        return doVisit(matcher, EmptyMatcher.class);
    }

    @Override
    public ParserStatistics visit(final FirstOfMatcher matcher)
    {
        return doVisit(matcher, FirstOfMatcher.class);
    }

    @Override
    public ParserStatistics visit(final NothingMatcher matcher)
    {
        return doVisit(matcher, NothingMatcher.class);
    }

    @Override
    public ParserStatistics visit(final JoinMatcher matcher)
    {
        return doVisit(matcher, JoinMatcher.class);
    }

    @Override
    public ParserStatistics visit(final OneOrMoreMatcher matcher)
    {
        return doVisit(matcher, OneOrMoreMatcher.class);
    }

    @Override
    public ParserStatistics visit(final OptionalMatcher matcher)
    {
        return doVisit(matcher, OptionalMatcher.class);
    }

    @Override
    public ParserStatistics visit(final SequenceMatcher matcher)
    {
        return doVisit(matcher, SequenceMatcher.class);
    }

    @Override
    public ParserStatistics visit(final TestMatcher matcher)
    {
        return doVisit(matcher, TestMatcher.class);
    }

    @Override
    public ParserStatistics visit(final TestNotMatcher matcher)
    {
        return doVisit(matcher, TestNotMatcher.class);
    }

    @Override
    public ParserStatistics visit(final ZeroOrMoreMatcher matcher)
    {
        return doVisit(matcher, ZeroOrMoreMatcher.class);
    }

    private <M extends Matcher> ParserStatistics doVisit(final M matcher)
    {
        return doVisit(matcher, matcher.getClass());
    }

    private <M extends Matcher> ParserStatistics doVisit(final M matcher,
        final Class<? extends M> c)
    {
        final MatcherStats<?> stats = getMatcherStats(c, matcher.getClass());
        Preconditions.checkNotNull(stats,
            c.getCanonicalName() + " not recorded??");
        if (stats.recordInstance(matcher)) {
            totalRules++;
            for (final Matcher child: matcher.getChildren()) {
                countSpecials(child);
                child.accept(this);
            }
        }
        return this;
    }

    private MatcherStats<?> getMatcherStats(final Class<?> base,
    final Class<?> real)
    {
        MatcherStats<?> ret;

        ret = regularMatcherStats.get(real);
        if (ret != null)
            return ret;

        ret = regularMatcherStats.get(base);
        if (ret != null)
            return ret;

        ret = specialMatcherStats.get(real);
        if (ret != null)
            return ret;

        return Preconditions.checkNotNull(specialMatcherStats.get(base),
            "class " + real.getCanonicalName() + " not recorded in stats");
    }

    private void countSpecials(final Matcher matcher)
    {
        final Class<? extends Matcher> matcherClass = matcher.getClass();
        if (SPECIAL_MATCHER_CLASSES.contains(matcherClass))
            specialMatcherStats.get(matcherClass).recordInstance(matcher);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("Parser statistics for rule '").append(root).append("':\n");
        sb.append("    Total rules       : ").append(totalRules).append('\n');
        sb.append("        Actions       : ").append(actions.size())
            .append('\n');
        NEWLINE.appendTo(sb, regularMatcherStats.values());
        sb.append("\n\n");
        sb.append("    Action Classes    : ").append(actionClasses.size())
            .append('\n');
        NEWLINE.appendTo(sb, specialMatcherStats.values());
        return sb.append('\n').toString();
    }

    public String printActionClassInstances()
    {
        final StringBuilder sb = new StringBuilder(
            "Action classes and their instances for rule '").append(root)
            .append("':\n");

        for (final String line: printActionClassLines())
            sb.append("    ").append(line).append('\n');
        return sb.toString();
    }

    private List<String> printActionClassLines()
    {
        final List<String> lines = new ArrayList<String>();
        int anonymous = 0;
        for (final Class<?> c : actionClasses) {
            final String name = c.getSimpleName();
            if (name.isEmpty()) {
                anonymous++;
            } else {
                lines.add(name + " : "
                    + COMMA.join(printActionClassInstances(c)));
            }
        }
        Collections.sort(lines);
        if (anonymous > 0)
            lines.add("and " + anonymous + " anonymous instance(s)");
        return lines;
    }

    private List<String> printActionClassInstances(final Class<?> actionClass)
    {
        final List<String> actionNames = new ArrayList<String>();
        for (final Action<?> action: actions)
            if (action.getClass().equals(actionClass))
                actionNames.add(action.toString());
        Collections.sort(actionNames);
        return actionNames;
    }

    @VisibleForTesting
    public static final class MatcherStats<T extends Matcher>
    {
        private final String name;
        private final Set<Matcher> instances = new HashSet<Matcher>();

        private static <M extends Matcher> MatcherStats<M> forClass(
            final Class<M> matcherClass, final boolean special)
        {
            return new MatcherStats<M>(matcherClass, special);
        }

        private MatcherStats(final Class<T> matcherClass, final boolean special)
        {
            name = generateName(matcherClass.getSimpleName(), special);
        }

        private boolean recordInstance(final Matcher matcher)
        {
            return instances.add(matcher);
        }

        public int getInstanceCount()
        {
            return instances.size();
        }

        private static String generateName(final String name,
            final boolean special)
        {
            // FIXME: 22 is hardcoded!!
            // It is the length of "MemoMismatchesMatchers"
            final char[] array = new char[22];
            Arrays.fill(array, ' ');
            final CharBuffer buf = CharBuffer.wrap(array);
            final String realName = special ? name + 's'
                : name.replaceFirst("Matcher$", "");
            int position = special ? 4 : 8;
            while (realName.length() + position > 22)
                position -= 4;
            buf.position(position);
            buf.put(realName).rewind();
            return buf.toString();
        }

        @Override
        public String toString()
        {
            return name + ": " + instances.size();
        }
    }
}
