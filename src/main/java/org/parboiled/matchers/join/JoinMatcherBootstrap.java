package org.parboiled.matchers.join;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.parboiled.BaseParser;
import org.parboiled.Rule;

import javax.annotation.Nonnull;

public final class JoinMatcherBootstrap<V, P extends BaseParser<V>>
{
    private final P parser;
    private final Rule joined;

    @VisibleForTesting
    static <T, E extends BaseParser<T>> JoinMatcherBootstrap<T, E> create(
        final E parser, final Object joined)
    {
        return new JoinMatcherBootstrap<T, E>(parser, joined);
    }

    public JoinMatcherBootstrap(@Nonnull final P parser,
        @Nonnull final Object joined)
    {
        this.parser = Preconditions.checkNotNull(parser);
        this.joined = parser.toRule(joined);
    }

    public JoinMatcherBuilder using(@Nonnull final Object joining)
    {
        return new JoinMatcherBuilder(joined, parser.toRule(joining));
    }
}
