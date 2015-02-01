package com.github.fge.grappa.trace;

public final class PreMatchRecord
{
    private final long startNanos;
    private final int startIndex;

    public PreMatchRecord(final long startNanos, final int startIndex)
    {
        this.startNanos = startNanos;
        this.startIndex = startIndex;
    }
}
