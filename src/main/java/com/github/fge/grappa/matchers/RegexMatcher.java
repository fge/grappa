package com.github.fge.grappa.matchers;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.matchers.base.AbstractMatcher;
import com.github.fge.grappa.run.context.MatcherContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A matcher for a Java regular expression
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
        final int startIndex = context.getCurrentIndex();
        final InputBuffer buffer = context.getInputBuffer();
        final CharSequence cs = new RegexInputBuffer(startIndex, buffer);

        // That is a java.util.regex.Matcher!!
        final Matcher matcher = pattern.matcher(cs);

        final boolean ret = matcher.lookingAt();

        if (ret)
            context.advanceIndex(matcher.end());

        return ret;
    }

    private static final class RegexInputBuffer
        implements CharSequence
    {
        private final int startIndex;
        private final InputBuffer buffer;

        /*
         * We need two lengths: the one viewed by the InputBuffer and the one
         * viewed by the regex!
         */
        private final int bufferLength;
        private final int csLength;

        private RegexInputBuffer(final int index, final InputBuffer buffer)
        {
            startIndex = index;
            this.buffer = buffer;
            bufferLength = buffer.length();
            csLength = bufferLength - startIndex;
        }

        @Override
        public int length()
        {
            return csLength;
        }

        @Override
        public char charAt(final int index)
        {
            final int realIndex = index + startIndex;
            final int codePoint = buffer.codePointAt(realIndex);

            /*
             * Ooops... Shouldn't happen...
             */
            if (codePoint == -1)
                throw new IndexOutOfBoundsException();

            return buffer.charAt(realIndex);
        }

        @Override
        public CharSequence subSequence(final int start, final int end)
        {
            if (start < 0 || end < 0)
                throw new IndexOutOfBoundsException();

            final int realStart = start + startIndex;
            final int realEnd = end + startIndex;

            if (realEnd < realStart)
                throw new IllegalArgumentException();

            if (buffer.codePointAt(realStart) == -1
                || buffer.codePointAt(realEnd) == -1)
                throw new IndexOutOfBoundsException();

            return buffer.extract(realStart, realEnd);
        }
    }
}
