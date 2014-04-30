package org.parboiled.matchers.join;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import org.parboiled.Rule;
import org.parboiled.matchers.EmptyMatcher;
import org.parboiled.matchers.Matcher;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class JoiningRuleBuilder
{
    private final Rule joined;
    private final Rule joining;

    JoiningRuleBuilder(final Rule joined, final Rule joining)
    {
        this.joined = joined;
        this.joining = joining;
    }

    public Matcher min(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified: " + nrMatches
            + ", must be 0 minimum");
        return new JoinMatcher(joined, joining, Range.atLeast(nrMatches));
    }

    public Matcher max(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified: " + nrMatches
            + ", must be 0 minimum");
        if (nrMatches == 0)
            return new EmptyMatcher();
        if (nrMatches == 1)
            return (Matcher) joined;
        return new JoinMatcher(joined, joining, Range.atMost(nrMatches));
    }

    public Matcher times(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified: " + nrMatches
                + ", must be 0 minimum");
        if (nrMatches == 0)
            return new EmptyMatcher();
        if (nrMatches == 1)
            return (Matcher) joined;
        return new JoinMatcher(joined, joining, Range.singleton(nrMatches));
    }

    public Matcher times(final int min, final int max)
    {
        Preconditions.checkArgument(min >= 0,
            "illegal repetition number specified: " + min
            + ", must be 0 minimum");
        Preconditions.checkArgument(max >= min,
            "illegal range specified: max must be greater than min");
        return max == min ? times(min)
            : new JoinMatcher(joined, joining, Range.closed(min, max));
    }
}
