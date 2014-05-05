package com.github.parboiled1.grappa.assertions;

import org.assertj.core.api.AbstractAssert;
import org.parboiled.Rule;
import org.parboiled.matchers.Matcher;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public final class MatcherAssert
    extends AbstractAssert<MatcherAssert, Matcher>
{
    private MatcherAssert(final Matcher actual)
    {
        super(actual, MatcherAssert.class);
    }

    public static MatcherAssert assertMatcher(final Rule rule)
    {
        final Class<?> c = rule.getClass();
        assertThat(rule).overridingErrorMessage(
            "rule is not even a matcher to start with?? (class %s)",
            c.getCanonicalName()
        ).isInstanceOf(Matcher.class);
        return new MatcherAssert((Matcher) rule);
    }

    public MatcherAssert hasChild(final Rule rule)
    {
        final Class<?> c = rule.getClass();
        assertThat(rule).overridingErrorMessage(
            "rule is not even a matcher to start with?? (class %s)",
            c.getCanonicalName()
        ).isInstanceOf(Matcher.class);
        assertThat(actual.getChildren()).overridingErrorMessage(
            "No such matcher in this matcher's children: %s", rule
        ).contains((Matcher) rule);
        return this;
    }
}
