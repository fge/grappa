package com.github.parboiled1.grappa.assertions;

import com.google.common.base.Joiner;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Action;
import org.parboiled.ParserStatistics;
import org.parboiled.matchers.Matcher;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.parboiled.ParserStatistics.MatcherStats;

@ParametersAreNonnullByDefault
public final class ParserStatisticsAssert
    extends AbstractAssert<ParserStatisticsAssert, ParserStatistics>
{
    private static final Joiner NEWLINE = Joiner.on('\n');

    private final Map<Class<?>, MatcherStats<?>> regularMatcherStats;
    private final Map<Class<?>, MatcherStats<?>> specialMatcherStats;
    private final Set<Action<?>> actions;
    private final Set<Class<?>> actionClasses;
    private final int totalRules;

    private boolean actionsCounted = false;
    private boolean actionClassesCounted = false;

    public ParserStatisticsAssert(final ParserStatistics actual)
    {
        super(actual, ParserStatisticsAssert.class);
        regularMatcherStats  = new HashMap<Class<?>, MatcherStats<?>>(
            actual.getRegularMatcherStats());
        specialMatcherStats = new HashMap<Class<?>, MatcherStats<?>>(
            actual.getSpecialMatcherStats());
        actions = new HashSet<Action<?>>(actual.getActions());
        actionClasses = new HashSet<Class<?>>(actual.getActionClasses());
        totalRules = actual.getTotalRules();
    }

    public void hasTotalRules(final SoftAssertions soft,
        final int expectedCount)
    {
        soft.assertThat(expectedCount).as("total rule count")
            .isEqualTo(totalRules);
    }

    public void hasCounted(final SoftAssertions soft,
        final Class<? extends Matcher> c, final int expectedCount)
    {
        MatcherStats<?> stats = regularMatcherStats.remove(c);
        if (stats == null)
            stats = specialMatcherStats.remove(c);

        final int actualCount = stats.getInstanceCount();
        soft.assertThat(expectedCount).as(
            "number of recorded matchers for class " + c.getSimpleName()
        ).isEqualTo(actualCount);
    }

    public void hasCountedActions(final SoftAssertions soft, final int expectedCount)
    {
        final int actualCount = actions.size();
        soft.assertThat(actualCount).as("number of recorded actions")
            .isEqualTo(expectedCount);
        actionsCounted = true;
    }

    public void hasCountedActionClasses(final SoftAssertions soft,
        final int expectedCount)
    {
        final int actualCount = actionClasses.size();
        soft.assertThat(actualCount).as("number of recorded actions")
            .isEqualTo(expectedCount);
        actionClassesCounted = true;
    }

    public void hasCountedNothingElse(final SoftAssertions soft)
    {
        noMatchersLeft(soft);
        noActionsLeft(soft);
        noActionClassesLeft(soft);
    }

    // TODO: filter if count == 0?
    private void noMatchersLeft(final SoftAssertions soft)
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

        soft.assertThat(mishaps).as("no matchers lefft in stats")
            .overridingErrorMessage(
                "Unwanted matcher counts! List follows\n\n%s\n",
                NEWLINE.join(mishaps)
            ).isEmpty();
    }

    private void noActionsLeft(final SoftAssertions soft)
    {
        if (!actionsCounted)
            hasCountedActions(soft, 0);
    }

    private void noActionClassesLeft(final SoftAssertions soft)
    {
        if (!actionClassesCounted)
            hasCountedActionClasses(soft, 0);
    }
}
