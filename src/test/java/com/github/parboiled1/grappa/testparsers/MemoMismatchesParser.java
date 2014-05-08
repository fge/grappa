package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.MemoMismatches;

public class MemoMismatchesParser
    extends NoMismatchesParser
{
    @Override
    @MemoMismatches
    public Rule sevenOrNine()
    {
        return super.sevenOrNine();
    }
}
