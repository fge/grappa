package org.parboiled.matchers.join;

import com.google.common.base.Preconditions;
import org.parboiled.Rule;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class JoinedRuleBuilder
{
    private final Rule joined;

    private JoinedRuleBuilder(final Rule joined)
    {
        this.joined = joined;
    }

    public static JoinedRuleBuilder join(@Nonnull final Rule joined)
    {
        return new JoinedRuleBuilder(Preconditions.checkNotNull(joined));
    }
}
