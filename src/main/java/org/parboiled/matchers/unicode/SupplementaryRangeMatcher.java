package org.parboiled.matchers.unicode;

public abstract class SupplementaryRangeMatcher
    extends UnicodeRangeMatcher
{
    protected SupplementaryRangeMatcher(final String label)
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
