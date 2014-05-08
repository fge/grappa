package com.github.parboiled1.grappa.assertions.mixins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Closer;
import org.parboiled.parserunners.ProfilingParseRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.parboiled.parserunners.ProfilingParseRunner.RuleReport;

public abstract class ProfilingReportMixin
{
    @JsonProperty("runs")
    private int totalRuns;
    @JsonIgnore
    private int totalInvocations;
    @JsonProperty("matches")
    private int totalMatches;
    @JsonProperty("mismatches")
    private int totalMismatches;
    @JsonIgnore
    private double matchShare;
    @JsonIgnore
    private int reinvocations;
    @JsonProperty("rematches")
    private int rematches;
    @JsonProperty("remismatches")
    private int remismatches;
    @JsonIgnore
    private double reinvocationShare;
    @JsonIgnore
    private long totalNanoTime;
    @JsonProperty("ruleReports")
    private List<RuleReport> ruleReports;

    @JsonCreator
    protected ProfilingReportMixin(
        @JsonProperty("runs") final int totalRuns,
        @JsonProperty("matches") final int totalMatches,
        @JsonProperty("mismatches") final int totalMismatches,
        @JsonProperty("rematches") final int rematches,
        @JsonProperty("remismatches") final int remismatches,
        @JsonProperty("totalNanoTime") final long totalNanoTime,
        @JsonProperty("ruleReports") final List<RuleReport> ruleReports
    )
    {
    }

    public static void main(final String... args)
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        mapper.registerModule(GrappaModule.INSTANCE);
        System.out.println(mapper.getDeserializationConfig().findMixInClassFor(
            ProfilingParseRunner.Report.class));

        final Closer closer = Closer.create();
        final InputStream in;

        try {
            in = closer.register(ProfilingReportMixin.class.
                getResourceAsStream("/profilingReports/test.json"));
            if (in == null)
                throw new IOException("resource not found");
            final ProfilingParseRunner.Report report
                = mapper.readValue(in, ProfilingParseRunner.Report.class);
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(System.out, report);
        } finally {
            closer.close();
        }
    }
}
