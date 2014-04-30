package org.parboiled.matchers.join;

import com.google.common.base.Preconditions;
import org.parboiled.BaseParser;
import org.parboiled.Rule;

import javax.annotation.Nonnull;

public final class JoinedRuleBuilder<V, P extends BaseParser<V>>
{
    private final P parser;
    private final Rule joined;

    public JoinedRuleBuilder(@Nonnull final P parser,
        @Nonnull final Object joined)
    {
        this.parser = Preconditions.checkNotNull(parser);
        this.joined = parser.toRule(joined);
    }

    public JoiningRuleBuilder using(@Nonnull final Object joining)
    {
        return new JoiningRuleBuilder(joined, parser.toRule(joining));
    }
}
