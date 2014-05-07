package com.github.parboiled1.grappa.assertions.mixins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.parboiled.matchers.Matcher;

public abstract class RuleReportMixin
{
//    @JsonDeserialize(as = DummyMatcher.class)
//    private Matcher matcher;
    @JsonProperty
    private int matches;
    @JsonProperty
    private int matchSubs;
    @JsonProperty
    private int mismatches;
    @JsonProperty
    private int mismatchSubs;
    @JsonProperty
    private int rematches;
    @JsonProperty
    private int rematchSubs;
    @JsonProperty
    private int remismatches;
    @JsonProperty
    private int remismatchSubs;
    @JsonIgnore
    private long nanoTime;

    @JsonCreator
    protected RuleReportMixin(@JsonProperty("matcher") final Matcher matcher)
    {
    }

    @JsonIgnore
    public Matcher getMatcher()
    {
        return null;
    }


    @JsonIgnore
    public int getInvocations()
    {
        return matches + mismatches;
    }

    @JsonIgnore
    public int getInvocationSubs()
    {
        return matchSubs + mismatchSubs;
    }

    @JsonIgnore
    public double getMatchShare()
    {
        return (double) matches / (double) getInvocations();
    }

    @JsonIgnore
    public double getMatchShare2()
    {
        return (double) matchSubs / (double) getInvocationSubs();
    }

    @JsonIgnore
    public int getReinvocations()
    {
        return rematches + remismatches;
    }

    @JsonIgnore
    public int getReinvocationSubs()
    {
        return rematchSubs + remismatchSubs;
    }

    @JsonIgnore
    public double getReinvocationShare()
    {
        return (double) getReinvocations() / (double) getInvocations();
    }

    @JsonIgnore
    public double getReinvocationShare2()
    {
        return (double) getReinvocationSubs() / (double) getInvocationSubs();
    }
}
