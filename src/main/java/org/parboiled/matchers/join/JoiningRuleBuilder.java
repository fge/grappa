package org.parboiled.matchers.join;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import org.parboiled.Rule;
import org.parboiled.matchers.EmptyMatcher;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.google.common.collect.BoundType.CLOSED;

@ParametersAreNonnullByDefault
public final class JoiningRuleBuilder
{
    private static final Range<Integer> AT_LEAST_ZERO = Range.atLeast(0);

    private final Rule joined;
    private final Rule joining;

    JoiningRuleBuilder(final Rule joined, final Rule joining)
    {
        this.joined = joined;
        this.joining = joining;
    }

    public Rule min(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified: " + nrMatches
            + ", must be 0 minimum");
        return new JoinMatcher(joined, joining, Range.atLeast(nrMatches));
    }

    public Rule max(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified: " + nrMatches
            + ", must be 0 minimum");
        if (nrMatches == 0)
            return new EmptyMatcher();
        if (nrMatches == 1)
            return joined;
        return new JoinMatcher(joined, joining, Range.atMost(nrMatches));
    }

    public Rule times(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified: " + nrMatches
                + ", must be 0 minimum");
        if (nrMatches == 0)
            return new EmptyMatcher();
        if (nrMatches == 1)
            return joined;
        return new JoinMatcher(joined, joining, Range.singleton(nrMatches));
    }

    public Rule times(final int min, final int max)
    {
        Preconditions.checkArgument(min >= 0,
            "illegal repetition number specified: " + min
            + ", must be 0 minimum");
        Preconditions.checkArgument(max >= min,
            "illegal range specified: max must be greater than min");
        return max == min ? times(min)
            : new JoinMatcher(joined, joining, Range.closed(min, max));
    }

    public Rule range(@Nonnull final Range<Integer> range)
    {
        Preconditions.checkNotNull(range, "range must not be null");
        /*
         * We always intersect with that range...
         */
        final Range<Integer> realRange = AT_LEAST_ZERO.intersection(range);

        /*
         * Empty ranges not allowed (what are we supposed to do with that
         * anyway?)
         */
        Preconditions.checkArgument(!realRange.isEmpty(),
            "illegal range " + range + " : must not be empty");

        /*
         * Given that we intersect with AT_LEAST_ZERO, which has a lower bound,
         * the range will always have a lower bound, which must be closed...
         */
        Preconditions.checkArgument(realRange.lowerBoundType() == CLOSED,
            "illegal range " + range + ": not closed on the lower bound");
        /*
         * But maybe not an _upper_ bound; if it has we check that it is closed.
         */
        if (realRange.hasUpperBound())
            Preconditions.checkArgument(realRange.upperBoundType() == CLOSED,
                "illegal range " + range + ": not closed on the upper bound");
        return new JoinMatcher(joined, joining, realRange);
    }
}
