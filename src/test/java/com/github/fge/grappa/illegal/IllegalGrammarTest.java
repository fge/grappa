package com.github.fge.grappa.illegal;

import com.github.fge.grappa.exceptions.InvalidGrammarException;
import org.parboiled.Parboiled;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Objects;

import static com.github.fge.grappa.util.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
@Test
public abstract class IllegalGrammarTest
{
    private final Class<? extends IllegalGrammarParser> parserClass;
    private final String errorMessage;

    protected IllegalGrammarTest(
        final Class<? extends IllegalGrammarParser> parserClass,
        final String errorMessage)
    {
        this.parserClass = Objects.requireNonNull(parserClass);
        this.errorMessage = Objects.requireNonNull(errorMessage);
    }

    @Test
    public final void illegalGrammarIsDetected()
    {
        final IllegalGrammarParser parser = Parboiled.createParser(parserClass);

        try {
            parser.theRule();
            shouldHaveThrown(InvalidGrammarException.class);
        } catch (InvalidGrammarException e) {
            assertThat(e).isExactlyInstanceOf(InvalidGrammarException.class)
                .hasMessage(errorMessage);
        }
    }
}
