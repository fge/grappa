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

package com.github.parboiled1.grappa.issues;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.Var;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Test for issue #17, https://github.com/parboiled1/grappa/issues/17
 *
 * <p>This is a strange one. With "optional" as a rule, we get an NPE:
 *
java.lang.NullPointerException
 at com.google.common.base.Preconditions.checkNotNull(Preconditions.java:213)
 at com.google.common.collect.ImmutableCollection$ArrayBasedBuilder.add(ImmutableCollection.java:339)
 at com.google.common.collect.ImmutableList$Builder.add(ImmutableList.java:652)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.sequence(Unknown Source)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.optional(Unknown Source)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.someRule(Unknown Source)
 *
 * With parboiled's "Optional", the error is different:
 *
org.parboiled.errors.GrammarException: 'null' cannot be automatically converted to a parser Rule
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.toRule(Unknown Source)
 at org.parboiled.BaseParser.toRules(BaseParser.java:1835)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.Sequence(Unknown Source)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.Sequence(Unknown Source)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.Optional(Unknown Source)
 at com.github.parboiled1.grappa.issues.Issue17$TestParser$$parboiled.someRule(Unknown Source)
 *
 * It is just as if the default value of Var was not accounted for at all...
 *
 * The bug with "optional" is triggered here:
 *
    @DontLabel
    public Rule sequence(@Nonnull final Object rule,
        @Nonnull final Object rule2, @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        final Object[] rules = ImmutableList.builder().add(rule).add(rule2)
            .add(moreRules).build().toArray();
        return sequence(rules);
    }
 *
 * ".add(rule)" fails since the rule (the result of Var's .get()) is null;
 * however, it should not be since there is a default value... Right?
 */
public final class Issue17
{
    static class TestParser
        extends BaseParser<Object>
    {
        public Rule someRule()
        {
            final Var<Boolean> guard = new Var<Boolean>(false);
            return sequence(optional('@', guard.set(true)), "some text",
                optional(guard.get(), // fail if this does not start with @
                    "some other text"));
        }
    }

    @Test
    public void varOfBooleanIsCountedAsAction()
    {
        final TestParser parser = Parboiled.createParser(TestParser.class);
        final BasicParseRunner<Object> runner
            = new BasicParseRunner<Object>(parser.someRule());

        assertThat(runner.run("some text").isSuccess()).isTrue();
    }
}
