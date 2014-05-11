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

package com.github.parboiled1.grappa.profiling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.assertions.soft.ProfilingReportSoftAssert;
import com.github.parboiled1.grappa.assertions.mixins.GrappaModule;
import com.github.parboiled1.grappa.testparsers.TestParser;
import com.google.common.base.Preconditions;
import com.google.common.io.Closer;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Parboiled;
import org.parboiled.buffers.CharSequenceInputBuffer;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.parserunners.ProfilingParseRunner;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

import static org.parboiled.parserunners.ProfilingParseRunner.Report;

@Test
public abstract class ProfilingReportTest<P extends TestParser<V>, V>
{
    private static final String RESOURCE_PREFIX = "/profilingReports/";
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(GrappaModule.INSTANCE);

    private final Report actualReport;
    private final Report expectedReport;

    protected ProfilingReportTest(@Nonnull final Class<P> c,
        @Nonnull final String resourcePath, @Nonnull final String input)
        throws IOException
    {
        Preconditions.checkNotNull(c);
        Preconditions.checkNotNull(resourcePath);
        Preconditions.checkNotNull(input);
        final InputBuffer buffer = new CharSequenceInputBuffer(input);
        final P parser = Parboiled.createParser(c);
        final ProfilingParseRunner<V> runner
            = new ProfilingParseRunner<V>(parser.mainRule());
        runner.run(buffer);
        actualReport = runner.getReport();

        final Closer closer = Closer.create();
        final InputStream in;

        try {
            final String resource = RESOURCE_PREFIX + resourcePath;
            in = closer.register(ProfilingReportTest.class
                .getResourceAsStream(resource));
            if (in == null)
                throw new IOException("resource " + resource + " not found");
            expectedReport = MAPPER.readValue(in, Report.class);
        } finally {
            closer.close();
        }
    }

    @Test
    public final void profilingReportMatchesExpectations()
    {
        final SoftAssertions soft = new SoftAssertions();
        final ProfilingReportSoftAssert reportAssert
            = new ProfilingReportSoftAssert(soft, expectedReport);
        reportAssert.matchesReport(actualReport);
        soft.assertAll();
    }
}
