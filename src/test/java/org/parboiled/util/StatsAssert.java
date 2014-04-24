package org.parboiled.util;

import org.assertj.core.api.AbstractAssert;
import org.parboiled.Action;
import org.parboiled.ParserStatistics;
import org.parboiled.Rule;
import org.parboiled.common.StringUtils;
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
public final class StatsAssert
    extends AbstractAssert<StatsAssert, ParserStatistics>
{
    private final Map<Class<?>, MatcherStats<?>> regularMatcherStats;
    private final Map<Class<?>, MatcherStats<?>> specialMatcherStats;
    private final Set<Action<?>> actions;
    private final Set<Class<?>> actionClasses;
    private final int totalRules;


    Class<? extends Matcher> currentMatcherClass = null;
    MatcherStats<? extends Matcher> currentStats = null;

    public static StatsAssert assertStatsForRule(final Rule rule)
    {
        return new StatsAssert(ParserStatistics.generateFor(rule));
    }

    private StatsAssert(final ParserStatistics actual)
    {
        super(actual, StatsAssert.class);
        regularMatcherStats  = new HashMap<Class<?>, MatcherStats<?>>(
            actual.getRegularMatcherStats());
        specialMatcherStats = new HashMap<Class<?>, MatcherStats<?>>(
            actual.getSpecialMatcherStats());
        actions = new HashSet<Action<?>>(actual.getActions());
        actionClasses = new HashSet<Class<?>>(actual.getActionClasses());
        totalRules = actual.getTotalRules();
    }

    public StatsAssert hasRecordedTotalOf(final int expected)
    {
        assertThat(totalRules).overridingErrorMessage(
            "number of recorded rules is incorrect: expected %d but got %d",
            expected, totalRules
        ).isEqualTo(expected);
        return this;
    }

    public StatsAssert hasRecorded(
        final Class<? extends Matcher> matcherClass)
    {
        Map<Class<?>, MatcherStats<?>> map = regularMatcherStats;
        currentStats = map.get(matcherClass);
        if (currentStats == null) {
            map = specialMatcherStats;
            currentStats = map.get(matcherClass);
        }

        assertThat(currentStats)
            .overridingErrorMessage(matcherClass + " not found in stats??")
            .isNotNull();

        currentMatcherClass = matcherClass;
        currentStats = map.remove(matcherClass);
        return this;
    }

    public StatsAssert withCount(final int expected)
    {
        assertThat(currentStats)
            .overridingErrorMessage("No current stats; call .hasMatcher()")
            .isNotNull();

        final int count = currentStats.getInstanceCount();
        assertThat(count).overridingErrorMessage(
            "recorded invocation count for class %s differ from expectations! "
            + "Wanted %d, got %d", currentMatcherClass.getSimpleName(), count,
            expected
        ).isEqualTo(expected);
        return this;
    }

    public StatsAssert hasRecordedNoOtherMatchers()
    {
        final List<String> mishaps = new ArrayList<String>();
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
            StringUtils.join(mishaps, "\n")
        ).isEmpty();

        return this;
    }

    public StatsAssert hasRecordedActionsCountOf(final int expected)
    {
        final int size = actions.size();
        assertThat(size).overridingErrorMessage(
            "recored number of actions is incorrect! Expected %d but got %d",
            expected, size
        ).isEqualTo(expected);
        return this;
    }

    public StatsAssert hasNotRecordedAnyActions()
    {
        return hasRecordedActionsCountOf(0);
    }

    public StatsAssert hasCountedActionClasses(final int expected)
    {
        final int size = actionClasses.size();
        assertThat(size).overridingErrorMessage(
            "recorded count of action classes is incorrect! Is %d, expected %d",
            size, expected
        ).isEqualTo(expected);
        return this;
    }

    public StatsAssert hasNotCountedAnyActionClasses()
    {
        return hasCountedActionClasses(0);
    }
}
