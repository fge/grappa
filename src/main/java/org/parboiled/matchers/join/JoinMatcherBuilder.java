package org.parboiled.matchers.join;

import com.google.common.base.Preconditions;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import org.parboiled.Rule;
import org.parboiled.matchers.EmptyMatcher;
import org.parboiled.matchers.OptionalMatcher;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class JoinMatcherBuilder
{
    private static final Range<Integer> AT_LEAST_ZERO = Range.atLeast(0);

    private final Rule joined;
    private final Rule joining;

    JoinMatcherBuilder(final Rule joined, final Rule joining)
    {
        this.joined = joined;
        this.joining = joining;
    }

    public Rule min(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified (" + nrMatches
            + "), must be 0 or greater");
        return range(Range.atLeast(nrMatches));
    }

    public Rule max(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified (" + nrMatches
                + "), must be 0 or greater");
        return range(Range.atMost(nrMatches));
    }

    public Rule times(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified (" + nrMatches
                + "), must be 0 or greater");
        return range(Range.singleton(nrMatches));
    }

    public Rule times(final int min, final int max)
    {
        Preconditions.checkArgument(min >= 0,
            "illegal repetition number specified (" + min
                + "), must be 0 or greater");
        Preconditions.checkArgument(max >= min, "illegal range specified ("
            + min + ", " + max + "): maximum must be greater than minimum");
        return range(Range.closed(min, max));
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
        Preconditions.checkArgument(!realRange.isEmpty(), "illegal range "
            + range + ": should not be empty after intersection with "
            + AT_LEAST_ZERO);

        /*
         * Given that we intersect with AT_LEAST_ZERO, which has a lower bound,
         * the range will always have a lower bound. We want a closed range
         * internally, therefore change it if it is open.
         */
        final Range<Integer> closedRange = toClosedRange(realRange);

        /*
         * We always have a lower bound
         */
        final int lowerBound = closedRange.lowerEndpoint();

        /*
         * Handle the case where there is no upper bound
         */
        if (!closedRange.hasUpperBound())
            return new BoundedDownJoinMatcher(joined, joining, lowerBound);

        /*
         * There is an upper bound. Handle the case where it is 0 or 1. Since
         * the range is legal, we know that if it is 0, so is the lowerbound;
         * and if it is one, the lower bound is either 0 or 1.
         */
        final int upperBound = closedRange.upperEndpoint();
        if (upperBound == 0)
            return new EmptyMatcher();
        if (upperBound == 1)
            return lowerBound == 0 ? new OptionalMatcher(joined) : joined;

        /*
         * So, upper bound is 2 or greater; return the appropriate matcher
         * according to what the lower bound is.
         *
         * Also, if the lower and upper bounds are equal, return a matcher doing
         * a fixed number of matches.
         */
        if (lowerBound == 0)
            return new BoundedUpJoinMatcher(joined, joining, upperBound);

        return lowerBound == upperBound
            ? new ExactMatchesJoinMatcher(joined, joining, lowerBound)
            : new BoundedBothJoinMatcher(joined, joining, lowerBound,
                upperBound);
    }

    private static Range<Integer> toClosedRange(final Range<Integer> range)
    {
        /*
         * The canonical form will always be the same: closed on the lower bound
         * (if any; but here we are guaranteed that), open on the upper bound
         * (if any).
         *
         * All we have to do is therefore to pick the canonical representation,
         * pick the lower bound, and if it has an upper bound, pick it and
         * substract 1.
         */
        final Range<Integer> canonical
            = range.canonical(DiscreteDomain.integers());
        final int lowerBound = canonical.lowerEndpoint();
        return canonical.hasUpperBound()
            ? Range.closed(lowerBound, canonical.upperEndpoint() - 1)
            : Range.atLeast(lowerBound);
    }
}
