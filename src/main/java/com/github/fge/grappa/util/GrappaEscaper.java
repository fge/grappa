package com.github.fge.grappa.util;

import com.google.common.escape.ArrayBasedCharEscaper;
import com.google.common.escape.Escaper;
import org.parboiled.support.Chars;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class GrappaEscaper
    extends Escaper
{
    private static final ArrayBasedCharEscaper DELEGATE
        = new ArrayBasedCharEscaper(Chars.escapeMap(), Character.MIN_VALUE,
            Character.MAX_VALUE)
    {
        @Override
        protected char[] escapeUnsafe(final char c)
        {
            return new char[] { c };
        }
    };

    public static final Escaper INSTANCE = new GrappaEscaper();

    private GrappaEscaper()
    {
    }

    @Override
    public String escape(final String string)
    {
        return DELEGATE.escape(string.replace("\r\n", "\n"));
    }
}
