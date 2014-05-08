package com.github.parboiled1.grappa.assertions.verify;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.errors.ParseError;

import javax.annotation.Nonnull;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")

@JsonSubTypes(
    @Type(name = "invalidInput", value = InvalidInputErrorVerifier.class))
public abstract class ParseErrorVerifier<E extends ParseError>
    implements Verifier<ParseError>
{
    @JsonIgnore
    protected final Class<E> errorClass;
    protected int startIndex;
    protected int endIndex;

    protected ParseErrorVerifier(final Class<E> errorClass)
    {
        this.errorClass = errorClass;
    }

    void setStartIndex(final int startIndex)
    {
        this.startIndex = startIndex;
    }

    void setEndIndex(final int endIndex)
    {
        this.endIndex = endIndex;
    }

    protected abstract void verifyDetails(final SoftAssertions soft,
        final E toVerify);

    @Override
    @SuppressWarnings("unchecked")
    public final void verify(@Nonnull final SoftAssertions soft,
        @Nonnull final ParseError toVerify)
    {
        assertThat(toVerify).overridingErrorMessage(
            "Wrong error class! Expected %s, got %s", toVerify.getClass(),
            errorClass
        ).isExactlyInstanceOf(errorClass);
        verifyDetails(soft, (E) toVerify);
    }
}

