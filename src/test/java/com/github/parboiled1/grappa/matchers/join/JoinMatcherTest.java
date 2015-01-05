/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.parboiled1.grappa.matchers.join;

import org.assertj.core.api.SoftAssertions;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.errors.GrammarException;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.fail;

public final class JoinMatcherTest
{
    static class MyParser
        extends BaseParser<Object>
    {
        @SuppressNode
        Rule rule()
        {
            return join(zeroOrMore('a'))
                .using(optional('b').label("foo"))
                .min(0);
        }
    }

    @Test
    public void joinMatcherYellsIfJoiningRuleMatchesEmpty()
    {
        final CharSequence input = "aaaabaaaaxaaa";
        final MyParser parser = Parboiled.createParser(MyParser.class);
        final ParseRunner<Object> runner
            = new BasicParseRunner<>(parser.rule());
        final String expectedMessage = "joining rule (foo) of a JoinMatcher" +
            " cannot match an empty character sequence!";

        try {
            runner.run(input);
            fail("No exception thrown!!");
        } catch (ParserRuntimeException e) {
            final SoftAssertions soft = new SoftAssertions();
            final Throwable cause = e.getCause();
            soft.assertThat(cause).as("cause has correct class")
                .isExactlyInstanceOf(GrammarException.class);
            soft.assertThat(cause).as("exception has the correct message")
                .hasMessage(expectedMessage);
            soft.assertAll();
        }
    }

}
