package com.github.fge.grappa.stack;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class DummyValueStack
    extends ValueStackBase<Object>
{
    @Override
    protected void doPush(final int down, final Object value)
    {
        // TODO

    }

    @Override
    protected Object doPop(final int down)
    {
        // TODO
        return null;
    }

    @Override
    protected Object doPeek(final int down)
    {
        // TODO
        return null;
    }

    @Override
    protected void doPoke(final int down, final Object value)
    {
        // TODO

    }

    @Override
    protected void doDup()
    {
        // TODO

    }

    @Override
    protected void doSwap(final int n)
    {
        // TODO

    }

    @Override
    public int size()
    {
        // TODO
        return 0;
    }

    @Override
    public void clear()
    {
        // TODO

    }

    @Nonnull
    @Override
    public Object takeSnapshot()
    {
        // TODO
        return null;
    }

    @Override
    public void restoreSnapshot(final Object snapshot)
    {
        // TODO

    }

    @Override
    public Iterator<Object> iterator()
    {
        // TODO
        return null;
    }
}
