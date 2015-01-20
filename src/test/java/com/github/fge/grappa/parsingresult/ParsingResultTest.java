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

package com.github.fge.grappa.parsingresult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.assertions.verify.ParsingResultVerifier;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.run.ParseRunner;
import com.github.fge.grappa.testparsers.TestParser;
import com.google.common.io.Closer;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Parboiled;
import org.parboiled.support.ParsingResult;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

@Test
public abstract class ParsingResultTest<P extends TestParser<V>, V>
{
    private static final String RESOURCE_PREFIX = "/parseResults/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TypeReference<ParsingResultVerifier<V>> typeRef
        = new TypeReference<ParsingResultVerifier<V>>() {};

    private final ParsingResult<V> result;
    private final ParsingResultVerifier<V> data;

    protected ParsingResultTest(final Class<P> c,
        final String resourceName)
        throws IOException
    {
        final String path = RESOURCE_PREFIX + resourceName;
        final Closer closer = Closer.create();
        final InputStream in;

        try {
            in = closer.register(ParsingResultTest.class
                .getResourceAsStream(path));
            if (in == null)
                throw new IOException("resource " + path + " not found");
            data = MAPPER.readValue(in, typeRef);
        } finally {
            closer.close();
        }

        final TestParser<V> parser = Parboiled.createParser(c);
        final ParseRunner<V> runner
            = new EventBasedParseRunner<>(parser.mainRule());
        result = runner.run(data.getBuffer());
    }

    @Test
    public final void treeIsWhatIsExpected()
    {
        final SoftAssertions soft = new SoftAssertions();
        data.verify(soft, result);
        soft.assertAll();
    }
}
