package com.github.fge.grappa.misc;

import com.github.fge.grappa.annotations.Cached;
import com.github.fge.grappa.annotations.DontLabel;
import com.github.fge.grappa.matchers.EmptyMatcher;
import com.github.fge.grappa.matchers.delegate.OptionalMatcher;
import com.github.fge.grappa.matchers.join.JoinMatcherBuilder;
import com.github.fge.grappa.matchers.repeat.RepeatMatcherBuilder;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.google.common.base.Preconditions;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import java.util.Objects;

/**
 * A matcher builder for matchers repeating a given number of times
 *
 * <p>This class allows to specify a number of times which a given rule should
 * be repeated:</p>
 *
 * <ul>
 *     <li>at least n times;</li>
 *     <li>at most n times;</li>
 *     <li>exactly n times;</li>
 *     <li>between n1 and n2 times.</li>
 * </ul>
 *
 * <p>When appropriate, the returned rule is a simplified version; for instance,
 * a rule repeated "exactly once" is the rule itself. See {@link #range(Range)}
 * for more details.</p>
 *
 * @param <V> the type parameter of the parser
 *
 * @see JoinMatcherBuilder
 * @see RepeatMatcherBuilder
 */
public abstract class RangeMatcherBuilder<V>
{
    private static final Range<Integer> AT_LEAST_ZERO = Range.atLeast(0);

    protected final BaseParser<V> parser;
    protected final Rule rule;

    protected RangeMatcherBuilder(final BaseParser<V> parser, final Rule rule)
    {
        this.parser = Objects.requireNonNull(parser);
        this.rule = Objects.requireNonNull(rule);
    }

    /**
     * Return a rule with a minimum number of cycles to run
     *
     * @param nrCycles the number of cycles
     * @return a rule
     * @throws IllegalArgumentException {@code nrCycles} is less than 0
     *
     * @see Range#atLeast(Comparable)
     */
    public Rule min(final int nrCycles)
    {
        Preconditions.checkArgument(nrCycles >= 0,
            "illegal repetition number specified (" + nrCycles
                + "), must be 0 or greater");
        return range(Range.atLeast(nrCycles));
    }

    /**
     * Return a rule with a maximum number of cycles to run
     *
     * @param nrCycles the number of cycles
     * @return a rule
     * @throws IllegalArgumentException {@code nrCycles} is less than 0
     *
     * @see Range#atMost(Comparable)
     */
    public Rule max(final int nrCycles)
    {
        Preconditions.checkArgument(nrCycles >= 0,
            "illegal repetition number specified (" + nrCycles
                + "), must be 0 or greater");
        return range(Range.atMost(nrCycles));
    }

    /**
     * Return a rule with an exact number of cycles to run
     *
     * @param nrCycles the number of cycles
     * @return a rule
     * @throws IllegalArgumentException {@code nrCycles} is less than 0
     *
     * @see Range#singleton(Comparable)
     */
    public Rule times(final int nrCycles)
    {
        Preconditions.checkArgument(nrCycles >= 0,
            "illegal repetition number specified (" + nrCycles
                + "), must be 0 or greater");
        return range(Range.singleton(nrCycles));
    }

    /**
     * Return a rule with both lower and upper bounds on the number of cycles
     *
     * <p>Note that the range of cycles to run is closed on both ends (that is,
     * the minimum and maximum number of cycles) are <strong>inclusive</strong>.
     * </p>
     *
     * @param minCycles the minimum number of cycles
     * @param maxCycles the maximum number of cycles
     * @return a rule
     * @throws IllegalArgumentException minimum number of cycles is negative; or
     * maximum number of cycles is less than the minimum
     *
     * @see Range#closed(Comparable, Comparable)
     */
    public Rule times(final int minCycles, final int maxCycles)
    {
        Preconditions.checkArgument(minCycles >= 0,
            "illegal repetition number specified (" + minCycles
                + "), must be 0 or greater");
        Preconditions.checkArgument(maxCycles >= minCycles,
            "illegal range specified (" + minCycles + ", " + maxCycles
                + "): maximum must be greater than minimum");
        return range(Range.closed(minCycles, maxCycles));
    }

    /**
     * Core method for building a repeating matcher
     *
     * <p>This is the method which all other methods (min, max, times) delegate
     * to; among other things it is responsible for the logic of simplifying
     * matchers where possible.</p>
     *
     * <p>The simplifications are as follows:</p>
     *
     * <ul>
     *     <li>[0..0]: returns an {@link EmptyMatcher};</li>
     *     <li>[0..1]: returns an {@link OptionalMatcher} with the rule as a
     *     submatcher;</li>
     *     <li>[1..1]: returns the rule itself.</li>
     * </ul>
     *
     * <p>If none of these apply, this method delegates as follows:</p>
     *
     * <ul>
     *     <li>[n..+∞) for whatever n: delegates to {@link #boundedDown(int)};
     *     </li>
     *     <li>[0..n] for n &gt;= 2: delegates to {@link #boundedUp(int)};</li>
     *     <li>[n..n] for n &gt;= 2: delegates to {@link #exactly(int)};</li>
     *     <li>[n..m] with 0 &lt; n &lt; m: delegates to {@link
     *     #boundedBoth(int, int)}.</li>
     * </ul>
     *
     * @param range the range
     * @return the final resulting rule
     */
    @Cached
    @DontLabel
    public Rule range(final Range<Integer> range)
    {
        Objects.requireNonNull(range, "range must not be null");

        final Range<Integer> realRange = normalizeRange(range);

        /*
         * We always have a lower bound
         */
        final int lowerBound = realRange.lowerEndpoint();

        /*
         * Handle the case where there is no upper bound
         */
        if (!realRange.hasUpperBound())
            return boundedDown(lowerBound);

        /*
         * There is an upper bound. Handle the case where it is 0 or 1. Since
         * the range is legal, we know that if it is 0, so is the lowerbound;
         * and if it is one, the lower bound is either 0 or 1.
         */
        final int upperBound = realRange.upperEndpoint();
        if (upperBound == 0)
            return parser.empty();
        if (upperBound == 1)
            return lowerBound == 0 ? parser.optional(rule) : rule;

        /*
         * So, upper bound is 2 or greater; return the appropriate matcher
         * according to what the lower bound is.
         *
         * Also, if the lower and upper bounds are equal, return a matcher doing
         * a fixed number of matches.
         */
        if (lowerBound == 0)
            return boundedUp(upperBound);

        return lowerBound == upperBound
            ? exactly(lowerBound)
            : boundedBoth(lowerBound, upperBound);
    }

    /**
     * Build a rule which is expected to match a minimum number of times
     *
     * <p>The returned matcher will attempt to match indefinitely its input
     * until it fails. Success should be declared if and only if the number of
     * times the matcher has succeeded is greater than, or equal to, the number
     * of cycles given as an argument (including 0).</p>
     *
     * @param minCycles the minimum number of cycles (inclusive)
     * @return a rule
     */
    protected abstract Rule boundedDown(int minCycles);

    /**
     * Build a rule which is expected to match a maximum number of times
     *
     * <p>The returned matcher will attempt to match repeatedly up to, and
     * including, the number of cycles returned as an argument. Note that the
     * argument will always be greater than or equal to 2.</p>
     *
     * <p>One consequence is that this matcher always succeeds.</p>
     *
     * @param maxCycles the maximum number of cycles (inclusive)
     * @return a rule
     */
    protected abstract Rule boundedUp(int maxCycles);

    /**
     * Build a rule which is expected to match a fixed number of times
     *
     * <p>The returned matcher will attempt to match repeatedly up to, and
     * including, the number of cycles given as an argument. Success should be
     * declared if and only if the number of cycles matched is exactly this
     * number.</p>
     *
     * @param nrCycles the number of cycles (inclusive)
     * @return a rule
     */
    protected abstract Rule exactly(int nrCycles);

    /**
     * Build a rule which is expected to match a number of times between two
     * end points
     *
     * <p>The returned matcher will attempt to match repeatedly up to, and
     * including, the maximum number of cycles specified as the second argument.
     * Success should be declared if and only if the number of cycles performed
     * is at least equal to the number of cycles specified as the first
     * argument.</p>
     *
     * <p>Note that the first argument will always be strictly greater than 0,
     * and that the second argument will always be strictly greater than the
     * first.</p>
     *
     * @param minCycles the minimum number of cycles (inclusive)
     * @param maxCycles the maximum number of cycles (exclusive)
     * @return a rule
     */
    protected abstract Rule boundedBoth(int minCycles, int maxCycles);

    private static Range<Integer> normalizeRange(final Range<Integer> range)
    {
        Range<Integer> newRange = AT_LEAST_ZERO.intersection(range);

        if (newRange.isEmpty())
            throw new IllegalArgumentException("illegal range " + range
                + ": intersection with " + AT_LEAST_ZERO + " is empty");

        newRange = newRange.canonical(DiscreteDomain.integers());

        final int lowerBound = newRange.lowerEndpoint();

        return newRange.hasUpperBound()
            ? Range.closed(lowerBound, newRange.upperEndpoint() - 1)
            : Range.atLeast(lowerBound);
    }
}
