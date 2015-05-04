package com.github.fge.grappa.issues.issue26;

import com.github.fge.grappa.Grappa;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.ListeningParseRunner;
import com.github.fge.grappa.run.ParseRunner;
import com.github.fge.grappa.run.ParsingResult;
import com.github.fge.grappa.support.Var;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public final class Issue26Test
{
    private final Issue26Parser parser
        = Grappa.createParser(Issue26Parser.class);

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> getRules()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { parser.rule2(new Var<>(0))});
        list.add(new Object[] {
            parser.rule3(new Var<>(new AtomicInteger(0)))
        });

        return list.iterator();
    }

    @Test(dataProvider = "getRules")
    public void cachedAnnotationWorks(final Rule rule)
    {
        final String input = "bbbcbbb";

        final ParseRunner<Object> runner = new ListeningParseRunner<>(rule);

        final ParsingResult<Object> result = runner.run(input);


        assertThat(result.isSuccess()).isTrue();
    }
}
