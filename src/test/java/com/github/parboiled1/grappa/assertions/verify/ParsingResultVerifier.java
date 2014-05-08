package com.github.parboiled1.grappa.assertions.verify;

import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.buffers.CharSequenceInputBuffer;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.support.ParsingResult;

import javax.annotation.Nonnull;
import java.util.List;

public final class ParsingResultVerifier<V>
    implements Verifier<ParsingResult<V>>
{
    private CharSequenceInputBuffer buffer;
    private boolean hasMatch;
    private NodeVerifier<V> parseTree;
    private final List<ParseErrorVerifier<?>> errors = Lists.newArrayList();

    void setBuffer(final String buffer)
    {
        this.buffer = new CharSequenceInputBuffer(buffer);
    }

    public InputBuffer getBuffer()
    {
        return buffer;
    }

    void setHasMatch(final boolean hasMatch)
    {
        this.hasMatch = hasMatch;
    }

    void setParseTree(final NodeVerifier<V> parseTree)
    {
        this.parseTree = parseTree;
    }

    void setErrors(final List<ParseErrorVerifier<?>> errors)
    {
        this.errors.addAll(errors);
    }

    @Override
    public void verify(@Nonnull final SoftAssertions soft,
        @Nonnull final ParsingResult<V> toVerify)
    {
        soft.assertThat(toVerify.matched).as("rule matches/does not match")
            .isEqualTo(hasMatch);
        parseTree.setBuffer(buffer).verify(soft, toVerify.parseTreeRoot);
        soft.assertThat(toVerify.parseErrors.size())
            .as("number of recorded errors").isEqualTo(errors.size());
        final int size = Math.min(toVerify.parseErrors.size(),
            errors.size());
        for (int index = 0; index < size; index++)
            errors.get(index).verify(soft, toVerify.parseErrors.get(index));
    }
}
