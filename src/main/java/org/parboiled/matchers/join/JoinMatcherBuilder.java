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
        //return new JoinMatcher(joined, joining, Range.atLeast(nrMatches));
    }

    public Rule max(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified (" + nrMatches
                + "), must be 0 or greater");
        return range(Range.atMost(nrMatches));
//        if (nrMatches == 0)
//            return new EmptyMatcher();
//        if (nrMatches == 1)
//            return new OptionalMatcher(joined);
//        return new JoinMatcher(joined, joining, Range.atMost(nrMatches));
    }

    public Rule times(final int nrMatches)
    {
        Preconditions.checkArgument(nrMatches >= 0,
            "illegal repetition number specified (" + nrMatches
                + "), must be 0 or greater");
        return range(Range.singleton(nrMatches));
//        if (nrMatches == 0)
//            return new EmptyMatcher();
//        if (nrMatches == 1)
//            return joined;
//        return new JoinMatcher(joined, joining, Range.singleton(nrMatches));
    }

    public Rule times(final int min, final int max)
    {
        Preconditions.checkArgument(min >= 0,
            "illegal repetition number specified (" + min
                + "), must be 0 or greater");
        Preconditions.checkArgument(max >= min, "illegal range specified ("
            + min + ", " + max + "): maximum must be greater than minimum");
        return range(Range.closed(min, max));
//        return max == min ? times(min)
//            : new JoinMatcher(joined, joining, Range.closed(min, max));
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
         * Deal with special cases
         */
        if (closedRange.hasUpperBound()) {
            final int upperEndpoint = closedRange.upperEndpoint();
            if (upperEndpoint == 0)
                return new EmptyMatcher();
            if (upperEndpoint == 1)
                return closedRange.lowerEndpoint() == 0
                    ? new OptionalMatcher(joined)
                    : joined;
        }
        return new JoinMatcher(joined, joining, closedRange);
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
