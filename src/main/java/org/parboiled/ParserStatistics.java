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

import org.parboiled.common.StringUtils;
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
import org.parboiled.matchervisitors.MatcherVisitor;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.parboiled.common.Preconditions.checkArgNotNull;

public class ParserStatistics
    implements MatcherVisitor<ParserStatistics>
{
    /*
     * Modify these maps if you add matcher classes!
     */
    private static final Set<Class<? extends Matcher>> REGULAR_MATCHER_CLASSES;
    private static final Set<Class<? extends Matcher>> SPECIAL_MATCHER_CLASSES;

    static {
        REGULAR_MATCHER_CLASSES = new LinkedHashSet<Class<? extends Matcher>>();

        REGULAR_MATCHER_CLASSES.add(AnyMatcher.class);
        REGULAR_MATCHER_CLASSES.add(CharIgnoreCaseMatcher.class);
        REGULAR_MATCHER_CLASSES.add(CharMatcher.class);
        REGULAR_MATCHER_CLASSES.add(CustomMatcher.class);
        REGULAR_MATCHER_CLASSES.add(CharRangeMatcher.class);
        REGULAR_MATCHER_CLASSES.add(AnyOfMatcher.class);
        REGULAR_MATCHER_CLASSES.add(EmptyMatcher.class);
        REGULAR_MATCHER_CLASSES.add(FirstOfMatcher.class);
        REGULAR_MATCHER_CLASSES.add(FirstOfStringsMatcher.class);
        REGULAR_MATCHER_CLASSES.add(NothingMatcher.class);
        REGULAR_MATCHER_CLASSES.add(OneOrMoreMatcher.class);
        REGULAR_MATCHER_CLASSES.add(OptionalMatcher.class);
        REGULAR_MATCHER_CLASSES.add(SequenceMatcher.class);
        REGULAR_MATCHER_CLASSES.add(StringMatcher.class);
        REGULAR_MATCHER_CLASSES.add(TestMatcher.class);
        REGULAR_MATCHER_CLASSES.add(TestNotMatcher.class);
        REGULAR_MATCHER_CLASSES.add(ZeroOrMoreMatcher.class);

        SPECIAL_MATCHER_CLASSES = new LinkedHashSet<Class<? extends Matcher>>();

        SPECIAL_MATCHER_CLASSES.add(ProxyMatcher.class);
        SPECIAL_MATCHER_CLASSES.add(VarFramingMatcher.class);
        SPECIAL_MATCHER_CLASSES.add(MemoMismatchesMatcher.class);
    }

    private final Matcher root;
    private int totalRules;

    private final Map<Class<?>, MatcherStats<?>> regularMatcherStats
        = new LinkedHashMap<Class<?>, MatcherStats<?>>();

    private final Map<Class<?>, MatcherStats<?>> specialMatcherStats
        = new LinkedHashMap<Class<?>, MatcherStats<?>>();

    private final Set<AnyMatcher> anyMatchers = new HashSet<AnyMatcher>();
    private final Set<CharIgnoreCaseMatcher> charIgnoreCaseMatchers = new HashSet<CharIgnoreCaseMatcher>();
    private final Set<CharMatcher> charMatchers = new HashSet<CharMatcher>();
    private final Set<CustomMatcher> customMatchers = new HashSet<CustomMatcher>();
    private final Set<CharRangeMatcher> charRangeMatchers = new HashSet<CharRangeMatcher>();
    private final Set<AnyOfMatcher> anyOfMatchers = new HashSet<AnyOfMatcher>();
    private final Set<EmptyMatcher> emptyMatchers = new HashSet<EmptyMatcher>();
    private final Set<FirstOfMatcher> firstOfMatchers = new HashSet<FirstOfMatcher>();
    private final Set<FirstOfStringsMatcher> firstOfStringMatchers = new HashSet<FirstOfStringsMatcher>();
    private final Set<NothingMatcher> nothingMatchers = new HashSet<NothingMatcher>();
    private final Set<OneOrMoreMatcher> oneOrMoreMatchers = new HashSet<OneOrMoreMatcher>();
    private final Set<OptionalMatcher> optionalMatchers = new HashSet<OptionalMatcher>();
    private final Set<SequenceMatcher> sequenceMatchers = new HashSet<SequenceMatcher>();
    private final Set<StringMatcher> stringMatchers = new HashSet<StringMatcher>();
    private final Set<TestMatcher> testMatchers = new HashSet<TestMatcher>();
    private final Set<TestNotMatcher> testNotMatchers = new HashSet<TestNotMatcher>();
    private final Set<ZeroOrMoreMatcher> zeroOrMoreMatchers = new HashSet<ZeroOrMoreMatcher>();

    private final Set<Action> actions = new HashSet<Action>();
    private final Set<Class<?>> actionClasses = new HashSet<Class<?>>();
    private final Set<ProxyMatcher> proxyMatchers = new HashSet<ProxyMatcher>();
    private final Set<VarFramingMatcher> varFramingMatchers = new HashSet<VarFramingMatcher>();
    private final Set<MemoMismatchesMatcher> memoMismatchesMatchers = new HashSet<MemoMismatchesMatcher>();

    @SuppressWarnings({"unchecked"})
    public static ParserStatistics generateFor(Rule rule) {
        checkArgNotNull(rule, "rule");
        Matcher matcher = (Matcher) rule;
        return matcher.accept(new ParserStatistics(matcher));
    }

    private ParserStatistics(Matcher root) {
        this.root = root;
        for (final Class<? extends Matcher> c: REGULAR_MATCHER_CLASSES)
            regularMatcherStats.put(c, MatcherStats.forClass(c, false));
        for (final Class<? extends Matcher> c: SPECIAL_MATCHER_CLASSES)
            specialMatcherStats.put(c, MatcherStats.forClass(c, true));
        countSpecials(root);
    }

    // MatcherVisitor interface

    public ParserStatistics visit(ActionMatcher matcher) {
        if (!actions.contains(matcher.action)) {
            totalRules++;
            actions.add(matcher.action);
            actionClasses.add(matcher.action.getClass());
        }
        return this;
    }

    public ParserStatistics visit(AnyMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, anyMatchers);
    }

    public ParserStatistics visit(CharIgnoreCaseMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, charIgnoreCaseMatchers);
    }

    public ParserStatistics visit(CharMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, charMatchers);
    }

    public ParserStatistics visit(CustomMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, customMatchers);
    }

    public ParserStatistics visit(CharRangeMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, charRangeMatchers);
    }

    public ParserStatistics visit(AnyOfMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, anyOfMatchers);
    }

    public ParserStatistics visit(EmptyMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, emptyMatchers);
    }

    public ParserStatistics visit(FirstOfMatcher matcher) {
        return doVisit(matcher);
//        return matcher instanceof FirstOfStringsMatcher ?
//                visit((FirstOfStringsMatcher)matcher, firstOfStringMatchers) :
//                visit(matcher, firstOfMatchers);
    }

    public ParserStatistics visit(NothingMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, nothingMatchers);
    }

    public ParserStatistics visit(OneOrMoreMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, oneOrMoreMatchers);
    }

    public ParserStatistics visit(OptionalMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, optionalMatchers);
    }

    public ParserStatistics visit(SequenceMatcher matcher) {
        return doVisit(matcher);
//        return matcher instanceof StringMatcher ?
//                visit((StringMatcher)matcher, stringMatchers) :
//                visit(matcher, sequenceMatchers);
    }

    public ParserStatistics visit(TestMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, testMatchers);
    }

    public ParserStatistics visit(TestNotMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, testNotMatchers);
    }

    public ParserStatistics visit(ZeroOrMoreMatcher matcher) {
        return doVisit(matcher);
        //return visit(matcher, zeroOrMoreMatchers);
    }

    private <M extends Matcher> ParserStatistics visit(M matcher, Set<M> set) {
        if (!set.contains(matcher)) {
            totalRules++;
            set.add(matcher);
            for (Matcher child : matcher.getChildren()) {
                countSpecials(child);
                child.accept(this);
            }
        }
        return this;
    }

    private <M extends Matcher> ParserStatistics doVisit(final M matcher)
    {
        final Class<? extends Matcher> c = matcher.getClass();
        final MatcherStats<?> stats = REGULAR_MATCHER_CLASSES.contains(c)
            ? regularMatcherStats.get(c)
            : specialMatcherStats.get(c);
        checkArgNotNull(stats, c.getCanonicalName() + " not recorded??");
        if (stats.recordInstance(matcher)) {
            totalRules++;
            for (final Matcher child : matcher.getChildren()) {
                countSpecials(child);
                child.accept(this);
            }
        }
        return this;
    }

    private void countSpecials(Matcher matcher) {
//        if (matcher instanceof ProxyMatcher) {
//            proxyMatchers.add((ProxyMatcher) matcher);
//        } else if (matcher instanceof VarFramingMatcher) {
//            varFramingMatchers.add((VarFramingMatcher) matcher);
//        } else if (matcher instanceof MemoMismatchesMatcher) {
//            memoMismatchesMatchers.add((MemoMismatchesMatcher) matcher);
//        }
        final Class<? extends Matcher> matcherClass = matcher.getClass();
        if (SPECIAL_MATCHER_CLASSES.contains(matcherClass))
            specialMatcherStats.get(matcherClass).recordInstance(matcher);
    }

    @Override
    public String toString() {
        return toString2();
//        return new StringBuilder("Parser statistics for rule '").append(root).append("':\n")
//                .append("    Total rules       : ").append(totalRules / 2).append(
//                '\n')
//                .append("        Actions       : ").append(actions.size()).append('\n')
//                .append("        Any           : ").append(anyMatchers.size()).append('\n')
//                .append("        CharIgnoreCase: ").append(charIgnoreCaseMatchers.size()).append('\n')
//                .append("        Char          : ").append(charMatchers.size()).append('\n')
//                .append("        Custom        : ").append(customMatchers.size()).append('\n')
//                .append("        CharRange     : ").append(charRangeMatchers.size()).append('\n')
//                .append("        AnyOf         : ").append(anyOfMatchers.size()).append('\n')
//                .append("        Empty         : ").append(emptyMatchers.size()).append('\n')
//                .append("        FirstOf       : ").append(firstOfMatchers.size()).append('\n')
//                .append("        FirstOfStrings: ").append(firstOfStringMatchers.size()).append('\n')
//                .append("        Nothing       : ").append(nothingMatchers.size()).append('\n')
//                .append("        OneOrMore     : ").append(oneOrMoreMatchers.size()).append('\n')
//                .append("        Optional      : ").append(optionalMatchers.size()).append('\n')
//                .append("        Sequence      : ").append(sequenceMatchers.size()).append('\n')
//                .append("        String        : ").append(stringMatchers.size()).append('\n')
//                .append("        Test          : ").append(testMatchers.size()).append('\n')
//                .append("        TestNot       : ").append(testNotMatchers.size()).append('\n')
//                .append("        ZeroOrMore    : ").append(zeroOrMoreMatchers.size()).append('\n')
//                .append('\n')
//                .append("    Action Classes    : ").append(actionClasses.size()).append('\n')
//                .append("    ProxyMatchers     : ").append(proxyMatchers.size()).append('\n')
//                .append("    VarFramingMatchers: ").append(varFramingMatchers.size()).append('\n')
//                .append("MemoMismatchesMatchers: ").append(memoMismatchesMatchers.size()).append('\n')
//                .toString();
    }

    private String toString2()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("Parser statistics for rule '").append(root).append("':\n");
        sb.append("    Total rules       : ").append(totalRules).append('\n');
        sb.append("        Actions       : ").append(actions.size())
            .append('\n');
        sb.append(StringUtils.join(regularMatcherStats.values(), "\n"));
        sb.append("\n\n");
        sb.append("    Action Classes    : ").append(actionClasses.size())
            .append('\n');
        sb.append(StringUtils.join(specialMatcherStats.values(), "\n"));
        return sb.append('\n').toString();
    }

    public String printActionClassInstances() {
        StringBuilder sb = new StringBuilder("Action classes and their instances for rule '")
                .append(root).append("':\n");

        for (String line : printActionClassLines()) {
            sb.append("    ").append(line).append('\n');
        }
        return sb.toString();
    }

    private List<String> printActionClassLines() {
        List<String> lines = new ArrayList<String>();
        int anonymous = 0;
        for (Class<?> actionClass : actionClasses) {
            String name = actionClass.getSimpleName();
            if (StringUtils.isEmpty(name)) {
                anonymous++;
            } else {
                lines.add(name + " : " + StringUtils.join(printActionClassInstances(actionClass), ", "));
            }
        }
        Collections.sort(lines);
        if (anonymous > 0) lines.add("and " + anonymous + " anonymous instance(s)");
        return lines;
    }

    private List<String> printActionClassInstances(Class<?> actionClass) {
        List<String> actionNames = new ArrayList<String>();
        for (Action action : actions) {
            if (action.getClass().equals(actionClass)) {
                actionNames.add(action.toString());
            }
        }
        Collections.sort(actionNames);
        return actionNames;
    }

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
