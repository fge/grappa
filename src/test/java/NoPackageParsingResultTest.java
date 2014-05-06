import com.github.parboiled1.grappa.testparsers.TestParser;
import com.github.parboiled1.grappa.parsingresult.ParsingResultTest;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import java.io.IOException;

public final class NoPackageParsingResultTest
    extends ParsingResultTest<NoPackageParsingResultTest.Parser, Integer>
{
    @BuildParseTree
    public static class Parser
        extends TestParser<Integer>
    {
        @Override
        public Rule mainRule()
        {
            return sequence('a', push(42));
        }
    }

    public NoPackageParsingResultTest()
        throws IOException
    {
        super(Parser.class, "noPackage.json");
    }
}
