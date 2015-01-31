package com.github.fge.grappa.trace;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fge.grappa.buffers.InputBuffer;

public final class ParseRunInfo
{
    private final long startDate;
    private final int nrLines;
    private final int nrChars;
    private final int nrCodePoints;
    private final int nrRuleInvocations;

    @JsonIgnore
    public ParseRunInfo(final long startDate, final InputBuffer buffer,
        final int nrRuleInvocations)
    {
        this.startDate = startDate;
        this.nrRuleInvocations = nrRuleInvocations;
        nrLines = buffer.getLineCount();

        final int len = buffer.length();
        nrChars = len;

        int codePoint;
        int codePointCount = nrChars;

        for (int i = 0; i < len; i++) {
            codePoint = buffer.codePointAt(i);
            if (codePoint >= Character.MIN_SUPPLEMENTARY_CODE_POINT) {
                i++;
                codePointCount--;
            }
        }

        nrCodePoints = codePointCount;
    }

    @JsonCreator
    public ParseRunInfo(@JsonProperty("startDate") final long startDate,
        @JsonProperty("nrLines") final int nrLines,
        @JsonProperty("nrChars") final int nrChars,
        @JsonProperty("nrCodePoints") final int nrCodePoints,
        @JsonProperty("nrRuleInvocations") final int nrRuleInvocations)
    {
        this.startDate = startDate;
        this.nrLines = nrLines;
        this.nrChars = nrChars;
        this.nrCodePoints = nrCodePoints;
        this.nrRuleInvocations = nrRuleInvocations;
    }

    public long getStartDate()
    {
        return startDate;
    }

    public int getNrLines()
    {
        return nrLines;
    }

    public int getNrChars()
    {
        return nrChars;
    }

    public int getNrCodePoints()
    {
        return nrCodePoints;
    }

    public int getNrRuleInvocations()
    {
        return nrRuleInvocations;
    }
}
