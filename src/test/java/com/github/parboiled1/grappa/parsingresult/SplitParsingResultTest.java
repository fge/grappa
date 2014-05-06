package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.TestParser;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;

import java.io.IOException;

public final class SplitParsingResultTest
    extends ParsingResultTest<SplitParsingResultTest.SplitParser, Object>
{
    static class SplitParser
        extends TestParser<Object>
    {
        final Primitives primitives = Parboiled.createParser(Primitives.class);

        public Rule clause() {
            return sequence(
                digit(),
                primitives.operator(),
                primitives.digit(),
                EOI
            );
        }

        @Override
        @DontLabel
        public Rule mainRule()
        {
            return clause();
        }
    }

    @BuildParseTree
    static class Primitives
        extends BaseParser<Object>
    {
        public Rule operator()
        {
            return firstOf('+', '-');
        }
    }

    public SplitParsingResultTest()
        throws IOException
    {
        super(SplitParser.class, "split.json", "1+5");
    }
}
