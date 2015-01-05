/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.parboiled1.grappa.assertions;

import com.google.common.base.Joiner;
import org.assertj.core.api.AbstractAssert;
import org.parboiled.Action;
import org.parboiled.ParserStatistics;
import org.parboiled.Rule;
import org.parboiled.matchers.Matcher;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.parboiled.ParserStatistics.MatcherStats;

@ParametersAreNonnullByDefault
public final class OldStatsAssert
    extends AbstractAssert<OldStatsAssert, ParserStatistics>
{
    private static final Joiner NEWLINE = Joiner.on('\n');

    private final Map<Class<?>, MatcherStats<?>> regularMatcherStats;
    private final Map<Class<?>, MatcherStats<?>> specialMatcherStats;
    private final Set<Action<?>> actions;
    private final Set<Class<?>> actionClasses;
    private final int totalRules;

    private boolean actionsCounted = false;
    private boolean actionClassesCounted = false;

    public static OldStatsAssert assertStatsForRule(final Rule rule)
    {
        return new OldStatsAssert(ParserStatistics.generateFor(rule));
    }

    private OldStatsAssert(final ParserStatistics actual)
    {
        super(actual, OldStatsAssert.class);
        regularMatcherStats  = new HashMap<>(
            actual.getRegularMatcherStats());
        specialMatcherStats = new HashMap<>(
            actual.getSpecialMatcherStats());
        actions = new HashSet<>(actual.getActions());
        actionClasses = new HashSet<>(actual.getActionClasses());
        totalRules = actual.getTotalRules();
    }

    public OldStatsAssert hasCountedTotal(final int expected)
    {
        assertThat(totalRules).overridingErrorMessage(
            "number of recorded rules is incorrect: expected %d but got %d",
            expected, totalRules
        ).isEqualTo(expected);
        return this;
    }

    public OldStatsAssert hasCounted(final int expected,
        final Class<? extends Matcher> matcherClass)
    {
        MatcherStats<?> stats = regularMatcherStats.remove(matcherClass);
        if (stats == null)
            stats = specialMatcherStats.remove(matcherClass);

        assertThat(stats)
            .overridingErrorMessage(matcherClass + " not found in stats??")
            .isNotNull();

        final int count = stats.getInstanceCount();
        assertThat(count).overridingErrorMessage(
            "recorded invocation count for class %s differ from expectations! "
            + "Wanted %d, got %d", matcherClass.getSimpleName(), count, expected
        ).isEqualTo(expected);
        return this;
    }

    public OldStatsAssert hasCountedActions(final int expected)
    {
        final int size = actions.size();
        assertThat(size).overridingErrorMessage(
            "recored number of actions is incorrect! Expected %d but got %d",
            expected, size
        ).isEqualTo(expected);
        actionsCounted = true;
        return this;
    }

    public OldStatsAssert hasCountedActionClasses(final int expected)
    {
        final int size = actionClasses.size();
        assertThat(size).overridingErrorMessage(
            "recorded count of action classes is incorrect! Is %d, expected %d",
            size, expected
        ).isEqualTo(expected);
        actionClassesCounted = true;
        return this;
    }

    public OldStatsAssert hasCountedNothingElse()
    {
        return noMatchersLeft().noActionsLeft().noActionClassesLeft();
    }

    private OldStatsAssert noMatchersLeft()
    {
        final List<String> mishaps = new ArrayList<>();
        final String fmt = "matcher class %s: recorded %d instances";

        int count;

        for (final Map.Entry<Class<?>, MatcherStats<?>> entry:
            regularMatcherStats.entrySet()) {
            count = entry.getValue().getInstanceCount();
            if (count != 0)
                mishaps.add(String.format(fmt, entry.getKey().getSimpleName(),
                    count));
        }

        for (final Map.Entry<Class<?>, MatcherStats<?>> entry:
            specialMatcherStats.entrySet()) {
            count = entry.getValue().getInstanceCount();
            if (count != 0)
                mishaps.add(String.format(fmt, entry.getKey().getSimpleName(),
                    count));
        }

        assertThat(mishaps).overridingErrorMessage(
            "Unwanted matcher counts! List follows\n\n%s\n",
            NEWLINE.join(mishaps)
        ).isEmpty();

        return this;
    }

    private OldStatsAssert noActionsLeft()
    {
        return actionsCounted ? this : hasCountedActions(0);
    }

    private OldStatsAssert noActionClassesLeft()
    {
        return actionClassesCounted ? this : hasCountedActionClasses(0);
    }
}
