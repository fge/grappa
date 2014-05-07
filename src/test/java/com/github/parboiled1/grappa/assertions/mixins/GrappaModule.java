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
