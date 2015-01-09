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

package com.github.parboiled1.grappa.matchers.unicode;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class UnicodeCharMatcherTest
{
    private static final int PILE_OF_POO = 0x1f4a9;

    static class TestGrammar
        extends BaseParser<Void>
    {
        public Rule rule(final int codePoint)
        {
            return unicodeChar(codePoint);
        }
    }

    @DataProvider
    public Iterator<Object[]> getClassInfo()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { (int) 'b', BmpCharMatcher.class });
        list.add(new Object[] { 0x1f4e3, SupplementaryCharMatcher.class });

        return list.iterator();
    }

    @Test(dataProvider = "getClassInfo")
    public void generatedMatcherClassIsWhatIsExpected(final int codePoint,
        final Class<? extends UnicodeCharMatcher> expected)
    {
        final Class<? extends UnicodeCharMatcher> actual
            = UnicodeCharMatcher.forCodePoint(codePoint).getClass();

        assertThat(actual).overridingErrorMessage(
            "Classes differ! Expected %s, got %s", expected, actual
        ).isSameAs(expected);
    }
}