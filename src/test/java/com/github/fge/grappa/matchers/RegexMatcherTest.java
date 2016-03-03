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

package com.github.fge.grappa.matchers;

import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.run.context.MatcherContext;
import com.github.fge.grappa.util.MatcherContextBuilder;
import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

public final class RegexMatcherTest
{
    private final Matcher matcher;

    public RegexMatcherTest()
    {
        matcher = new RegexMatcher("[A-Za-z]+");
    }

    @DataProvider
    public Iterator<Object[]> getMatchData()
    {
        final List<Object[]> list = Lists.newArrayList();

        final MatcherContextBuilder builder = new MatcherContextBuilder()
            .withMatcher(matcher);

        String input;
        boolean matched;
        int index;
        MatcherContext<Object> context;

        // Full match...
        input = "abstract";
        matched = true;
        index = 8;
        context = builder.withInput(input).build();
        list.add(new Object[] { context, matched, index });

        // Match within string...
        input = "assert^";
        matched = true;
        index = 6;
        context = builder.withInput(input).build();
        list.add(new Object[] { context, matched, index });

        // No match -- empty
        input = "";
        matched = false;
        index = 0;
        context = builder.withInput(input).build();
        list.add(new Object[] { context, matched, index });

        // Match but not at current index: no dice...
        input = " hello";
        matched = false;
        index = 0;
        context = builder.withInput(input).build();
        list.add(new Object[] { context, matched, index });

        // Match at a given index
        input = " hello";
        matched = true;
        index = 6;
        context = builder.withInput(input).withIndex(1).build();
        list.add(new Object[] { context, matched, index });

        return list.iterator();
    }

    @Test(dataProvider = "getMatchData")
    public void trieMatchingWorksCorrectly(final MatcherContext<Object> ctx,
        final boolean matched, final int index)
    {
        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(matcher.match(ctx)).as("match/no match")
            .isEqualTo(matched);

        soft.assertThat(ctx.getCurrentIndex()).as("post match run index")
            .isEqualTo(index);

        soft.assertAll();
    }
}
