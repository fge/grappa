package com.github.parboiled1.grappa.assertions.verify;

import com.beust.jcommander.internal.Maps;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.parboiled1.grappa.assertions.ParserStatisticsAssert;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.ParserStatistics;
import org.parboiled.matchers.Matcher;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class ParserStatisticsVerifier
    implements Verifier<ParserStatistics>
{
    private static final Map<String, Class <? extends Matcher>>
        NAMES_TO_CLASS;

    static {
        final ImmutableMap.Builder<String, Class<? extends Matcher>> builder
            = ImmutableMap.builder();
        Set<Class<? extends Matcher>> classes;
        classes = ParserStatistics.REGULAR_MATCHER_CLASSES;

        for (final Class<? extends Matcher> c: classes)
            builder.put(c.getSimpleName(), c);

        classes = ParserStatistics.SPECIAL_MATCHER_CLASSES;
        for (final Class<? extends Matcher> c: classes)
            builder.put(c.getSimpleName(), c);

        NAMES_TO_CLASS = builder.build();
    }

    private int totalRules;
    @JsonIgnore
    private final Map<Class<? extends Matcher>, Integer> regularStats
        = Maps.newHashMap();
    @JsonIgnore
    private final Map<Class<? extends Matcher>, Integer> specialStats
        = Maps.newHashMap();
    private int actionClassesCount;
    private int actionCount;

    ParserStatisticsVerifier()
    {
        Set<Class<? extends Matcher>> classes;
        classes = ParserStatistics.REGULAR_MATCHER_CLASSES;

        for (final Class<? extends Matcher> c: classes)
            regularStats.put(c, 0);

        classes = ParserStatistics.SPECIAL_MATCHER_CLASSES;
        for (final Class<? extends Matcher> c: classes)
            specialStats.put(c, 0);
    }

    void setTotalRules(final int totalRules)
    {
        this.totalRules = totalRules;
    }

    public void setActionCount(final int actionCount)
    {
        this.actionCount = actionCount;
    }

    public void setActionClassesCount(final int actionClassesCount)
    {
        this.actionClassesCount = actionClassesCount;
    }

    @JsonAnySetter
    void setCount(final String name, final int count)
    {
        final Class<? extends Matcher> c = NAMES_TO_CLASS.get(name);
        if (c == null)
            throw new IllegalArgumentException("no entry for name " + name);

        final Map<Class<? extends Matcher>, Integer> map
            = regularStats.containsKey(c) ? regularStats : specialStats;

        map.put(c, count);
    }

    @Override
    public void verify(@Nonnull final SoftAssertions soft,
        @Nonnull final ParserStatistics toVerify)
    {
        Preconditions.checkNotNull(soft);
        Preconditions.checkNotNull(toVerify);
        final ParserStatisticsAssert statisticsAssert
            = new ParserStatisticsAssert(toVerify);
        statisticsAssert.hasTotalRules(soft, totalRules);
        // TODO: rewrite with a map as argument since we are using softasserts
        for (final Map.Entry<Class<? extends Matcher>, Integer> entry:
            regularStats.entrySet())
            statisticsAssert.hasCounted(soft, entry.getKey(), entry.getValue());
        for (final Map.Entry<Class<? extends Matcher>, Integer> entry:
            specialStats.entrySet())
            statisticsAssert.hasCounted(soft, entry.getKey(), entry.getValue());
        statisticsAssert.hasCountedActionClasses(soft, actionClassesCount);
        statisticsAssert.hasCountedActions(soft, actionCount);
        statisticsAssert.hasCountedNothingElse(soft);
    }
}
