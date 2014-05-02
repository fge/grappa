package org.parboiled.transform.asm;

import javax.annotation.Nonnull;

public final class AsmHelper
{
    private AsmHelper()
    {
    }

    public static ClassHelper classHelper(@Nonnull final Class<?> c)
    {
        return new ClassHelper(c);
    }
}
