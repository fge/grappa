package com.github.parboiled1.grappa.misc;

import com.google.common.io.CharSink;
import org.parboiled.common.Sink;

import java.io.IOException;

public abstract class CharSourceWithSink
    extends CharSink
    implements Sink<String>
{
    @Override
    public final void receive(final String value)
    {
        try {
            write(value);
        } catch (IOException e) {
            throw new RuntimeException("cannot write to CharSource", e);
        }
    }
}
