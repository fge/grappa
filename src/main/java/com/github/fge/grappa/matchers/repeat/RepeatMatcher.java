package com.github.fge.grappa.matchers.repeat;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.matchers.base.AbstractMatcher;
import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.context.MatcherContext;

/**
 * A matcher which repeats matching a given number of times
 *
 * <p>This matcher (and all its subclasses) is used, among others, in {@link
 * BaseParser#repeat(Object) repeat()} but also {@code zeroOrMore()} and {@code
 * oneOrMore()}, since the latter two are just specialized versions of this
 * matcher.</p>
 *
 * <p>Note that it is forbidden for the subrule as an argument to match an
 * empty input. Unfortunately, due to current limitations, this can only be
 * detected at runtime.</p>
 *
 * <p>Example:</p>
 *
 * <pre>
 *     public Rule threeTimesHello()
 *     {
 *         return repeat("hello").times(3);
 *     }
 * </pre>
 */
public abstract class RepeatMatcher
    extends AbstractMatcher
{
    private final Matcher matcher;

    protected RepeatMatcher(final Rule subRule)
    {
        super(subRule, "repeat");
        matcher = getChildren().get(0);
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.COMPOSITE;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        int cycles = 0;

        int beforeMatch = context.getCurrentIndex();
        int afterMatch;

        while (runAgain(cycles)) {
            if (!context.getSubContext(matcher).runMatcher())
                break;
            afterMatch = context.getCurrentIndex();
            if (beforeMatch == afterMatch)
                throw new GrappaException("Inner rule of a RepeatMatcher"
                    + " cannot match an empty character sequence");
            beforeMatch = afterMatch;
            cycles++;
        }

        return enoughCycles(cycles);
    }

    protected abstract boolean enoughCycles(final int cycles);

    protected abstract boolean runAgain(final int cycles);
}
