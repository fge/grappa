package com.github.fge.grappa.matchers;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.matchers.base.AbstractMatcher;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.run.context.MatcherContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A matcher for a Java regular expression
 *
 * <p>This is the matcher used by {@link BaseParser#regex(String) regex()}.</p>
 *
 * <p>Note that the operation used to perform the match is {@link
 * Matcher#lookingAt()}, not {@code matches()} or {@code find()}.</p>
 */
public final class RegexMatcher
    extends AbstractMatcher
{
    private final Pattern pattern;

    public RegexMatcher(final String regex)
    {
        super("regex(" + regex + ')');
        pattern = Pattern.compile(regex);
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.TERMINAL;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final InputBuffer buffer = context.getInputBuffer();
        final int startIndex = context.getCurrentIndex();
        final int length = buffer.length();

        final CharSequence cs = buffer.subSequence(startIndex, length);

        // That is a java.util.regex.Matcher!!
        final Matcher matcher = pattern.matcher(cs);

        final boolean ret = matcher.lookingAt();

        if (ret)
            context.advanceIndex(matcher.end());

        return ret;
    }
}
