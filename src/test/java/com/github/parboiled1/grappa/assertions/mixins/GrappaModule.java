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

package com.github.parboiled1.grappa.assertions.mixins;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.parboiled.parserunners.ProfilingParseRunner;

public final class GrappaModule
    extends SimpleModule
{
    private static final Version VERSION = new Version(1, 0, 0,
        "beta.5-SNAPSHOT", "com.github.parboiled1", "grappa");

    public static final Module INSTANCE = new GrappaModule();

    private GrappaModule()
    {
        super("grappa", VERSION);
    }

    @Override
    public void setupModule(final SetupContext context)
    {
        context.setMixInAnnotations(ProfilingParseRunner.RuleReport.class,
            RuleReportMixin.class);
        context.setMixInAnnotations(ProfilingParseRunner.Report.class,
            ProfilingReportMixin.class);
        //context.setMixInAnnotations(Matcher.class, DummyMatcher.class);
    }
}
