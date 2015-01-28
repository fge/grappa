package com.github.fge.grappa.tracingtest;

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import org.parboiled.annotations.Label;

public class JsonParser
    extends BaseParser<Void>
{
    @Label("e")
    Rule e()
    {
        return sequence(anyOf("eE"), optional(anyOf("+-")));
    }

    @Label("digits")
    Rule digits()
    {
        return oneOrMore(digit());
    }

    @Label("exp")
    Rule exp()
    {
        return sequence(e(), digits());
    }

    @Label("frac")
    Rule frac()
    {
        return sequence('.', digits());
    }

    @Label("int")
    Rule integer()
    {
        return sequence(
            optional('-'),
            firstOf(
                sequence(charRange('1', '9'), zeroOrMore(digit())),
                digit()
            )
        );
    }

    @Label("number")
    Rule number()
    {
        return sequence(integer(), optional(frac()), optional(exp()));
    }

    @Label("charUnescaped")
    Rule charUnescaped()
    {
        return sequence(
            testNot(
                firstOf(anyOf("\\\""), ctl())
            ),
            unicodeRange(0, 0x10ffff)
        );
    }

    @Label("charEscaped")
    Rule charEscaped()
    {
        return sequence(
            '\\',
            firstOf(
                anyOf("\"\\/bfnrt"),
                sequence('u', nTimes(4, hexDigit()))
            )
        );
    }

    @Label("char")
    Rule character()
    {
        return firstOf(charUnescaped(), charEscaped());
    }

    @Label("string")
    Rule string()
    {
        return sequence('"', zeroOrMore(character()), '"');
    }

    @Label("booleanOrNull")
    Rule booleanOrNull()
    {
        return trie("true", "false", "null");
    }

    @Label("primitiveValue")
    Rule primitiveValue()
    {
        return firstOf(string(), number(), booleanOrNull());
    }

    @Label("whiteSpaces")
    Rule whiteSpaces()
    {
        return join(zeroOrMore(wsp())).using(sequence(optional(cr()), lf()))
            .min(0);

    }

    @Label("array")
    Rule array()
    {
        return sequence(
            '[',
            whiteSpaces(),
            join(value())
                .using(sequence(whiteSpaces(), ',', whiteSpaces()))
                .min(0),
            whiteSpaces(),
            ']'
        );
    }

    @Label("objectMember")
    Rule objectMember()
    {
        return sequence(string(), whiteSpaces(), ':', whiteSpaces(), value());
    }

    @Label("object")
    Rule object()
    {
        return sequence(
            '{',
            whiteSpaces(),
            join(objectMember())
                .using(sequence(whiteSpaces(), ',', whiteSpaces()))
                .min(0),
            whiteSpaces(),
            '}'
        );
    }

    @Label("value")
    Rule value()
    {
        return firstOf(primitiveValue(), object(), array());
    }

    @Label("jsonText")
    Rule jsonText()
    {
        return sequence(testNot(';'), whiteSpaces(), value(), whiteSpaces(), EOI);
    }
}
