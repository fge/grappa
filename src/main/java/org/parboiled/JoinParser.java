package org.parboiled;

import com.google.common.annotations.Beta;
import com.github.parboiled1.grappa.matchers.join.JoinMatcher;
import com.github.parboiled1.grappa.matchers.join.JoinMatcherBootstrap;

/**
 * Base parser with a {@code join()} method to create a {@link JoinMatcher}
 *
 * <p>Given two rules {@code rule} and {@code separator}, here are sample
 * usages:</p>
 *
 * <pre>
 *     Rule myRule()
 *     {
 *         // Minimum twice
 *         return join(rule).using(separator).min(2);
 *         // Maximum eight times
 *         return join(rule).using(separator).max(8);
 *         // Exactly twice
 *         return join(rule).using(separator).times(2);
 *         // At least 3, at most 5
 *         return join(rule).using(separator).times(3, 5);
 *         // Advanced: using a Guava Range
 *         return join(rule).using(separator).range(Range.atLeast(8));
 *     }
 * </pre>
 *
 * @param <V> production value of this parser
 */
@Beta
public abstract class JoinParser<V>
    extends BaseParser<V>
{
    public final JoinMatcherBootstrap<V, BaseParser<V>> join(
        final Object joined)
    {
        return new JoinMatcherBootstrap<V, BaseParser<V>>(this, joined);
    }
}
