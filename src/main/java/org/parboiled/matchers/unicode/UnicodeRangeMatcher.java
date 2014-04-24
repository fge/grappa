package org.parboiled.matchers.unicode;

public abstract class UnicodeRangeMatcher
    extends UnicodeMatcher
{
    public static UnicodeRangeMatcher forRange(final int low, final int high)
    {

        return null;
    }

    protected UnicodeRangeMatcher(final String label)
    {
        super(label);
    }

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
