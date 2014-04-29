package org.parboiled.matchers.join;

import org.parboiled.Rule;

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
}
