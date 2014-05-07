package com.github.parboiled1.grappa.assertions.mixins;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CustomMatcher;

final class DummyMatcher
    extends CustomMatcher
{
    DummyMatcher()
    {
        super("dummy");
    }

    /**
     * Determines whether this matcher instance always matches exactly one
     * character.
     *
     * @return true if this matcher always matches exactly one character
     */
    @Override
    public boolean isSingleCharMatcher()
    {
        return false;
    }

    /**
     * Determines whether this matcher instance allows empty matches.
     *
     * @return true if this matcher instance allows empty matches
     */
    @Override
    public boolean canMatchEmpty()
    {
        return false;
    }

    /**
     * Determines whether this matcher instance can start a match with the
     * given char.
     *
     * @param c the char
     * @return true if this matcher instance can start a match with the given
     * char.
     */
    @Override
    public boolean isStarterChar(final char c)
    {
        return false;
    }

    /**
     * Returns one of possibly several chars that a match can start with.
     *
     * @return a starter char
     */
    @Override
    public char getStarterChar()
    {
        return 0;
    }

    /**
     * Tries a match on the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if the match was successful
     */
    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return false;
    }
}
