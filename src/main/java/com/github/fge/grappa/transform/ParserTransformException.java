package com.github.fge.grappa.transform;

public final class ParserTransformException
    extends IllegalStateException
{
    public ParserTransformException(final String s)
    {
        super(s);
    }

    public ParserTransformException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
