/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package org.parboiled;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.parboiled.annotations.Cached;
import org.parboiled.annotations.DontExtend;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.SkipActionsInPredicates;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.buffers.IndentDedentInputBuffer;
import org.parboiled.common.Utils;
import org.parboiled.errors.GrammarException;
import org.parboiled.matchers.ActionMatcher;
import org.parboiled.matchers.AnyMatcher;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.CharIgnoreCaseMatcher;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.EmptyMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.FirstOfStringsMatcher;
import org.parboiled.matchers.NothingMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.StringMatcher;
import org.parboiled.matchers.TestMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;
import org.parboiled.matchers.unicode.UnicodeCharMatcher;
import org.parboiled.matchers.unicode.UnicodeRangeMatcher;
import org.parboiled.support.Characters;
import org.parboiled.support.Chars;
import org.parboiled.support.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

import static org.parboiled.common.Preconditions.checkArgNotNull;
import static org.parboiled.common.Preconditions.checkArgument;

/**
 * Base class of all parboiled parsers. Defines the basic rule creation methods.
 *
 * @param <V> the type of the parser values
 */
@SuppressWarnings("unused")
public abstract class BaseParser<V>
    extends BaseActions<V>
{

    /**
     * Matches the {@link Chars#EOI} (end of input) character.
     */
    public static final Rule EOI = new CharMatcher(Chars.EOI);

    /**
     * Matches the special {@link Chars#INDENT} character produced by an {@link
     * IndentDedentInputBuffer}
     */
    public static final Rule INDENT = new CharMatcher(Chars.INDENT);

    /**
     * Matches the special {@link Chars#DEDENT} character produced by an {@link
     * IndentDedentInputBuffer}
     */
    public static final Rule DEDENT = new CharMatcher(Chars.DEDENT);

    /**
     * Matches any character except {@link Chars#EOI}.
     */
    public static final Rule ANY = new AnyMatcher();

    /**
     * Matches nothing and always succeeds.
     */
    public static final Rule EMPTY = new EmptyMatcher();

    /**
     * Matches nothing and always fails.
     */
    public static final Rule NOTHING = new NothingMatcher();

    /**
     * Creates a new instance of this parsers class using the no-arg constructor. If no no-arg constructor
     * exists this method will fail with a java.lang.NoSuchMethodError.
     * Using this method is faster than using {@link Parboiled#createParser(Class, Object...)} for creating
     * new parser instances since this method does not use reflection.
     *
     * @param <P> the parser class
     * @return a new parser instance
     */
    public <P extends BaseParser<V>> P newInstance()
    {
        throw new UnsupportedOperationException("Illegal parser instance, " +
            "you have to use Parboiled.createParser(...) " +
            "to create your parser instance!");
    }

    /*
     * CORE RULES
     */

    /**
     * Match one given character
     *
     * @param c the character to match
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule ch(final char c)
    {
        return new CharMatcher(c);
    }

    /**
     * Match a given character in a case-insensitive manner
     *
     * @param c the character to match
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule ignoreCase(final char c)
    {
        return Character.isLowerCase(c) == Character.isUpperCase(c)
            ? ch(c) : new CharIgnoreCaseMatcher(c);
    }

    /**
     * Match one Unicode character
     *
     * @param codePoint the code point
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule unicodeChar(final int codePoint)
    {
        Preconditions.checkArgument(Character.isValidCodePoint(codePoint),
            "invalid code point " + codePoint);
        return UnicodeCharMatcher.forCodePoint(codePoint);
    }

    /**
     * Match a Unicode character range
     *
     * <p>Note that this method will delegate to "regular" character matchers if
     * part of, or all of, the specified range is into the basic multilingual
     * plane.</p>
     *
     * @param low the lower code point (inclusive)
     * @param high the upper code point (inclusive)
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule unicodeRange(final int low, final int high)
    {
        Preconditions.checkArgument(Character.isValidCodePoint(low),
            "invalid code point " + low);
        Preconditions.checkArgument(Character.isValidCodePoint(high),
            "invalid code point " + high);
        Preconditions.checkArgument(low <= high,
            "invalid range: " + low + " > " + high);
        return low == high ? UnicodeCharMatcher.forCodePoint(low)
            : UnicodeRangeMatcher.forRange(low, high);
    }

    /**
     * Match an inclusive range of {@code char}s
     *
     * @param cLow the start char of the range (inclusively)
     * @param cHigh the end char of the range (inclusively)
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule charRange(final char cLow, final char cHigh)
    {
        return cLow == cHigh ? ch(cLow) : new CharRangeMatcher(cLow, cHigh);
    }

    /**
     * Match any of the characters in the given string
     *
     * <p>This method delegates to {@link #anyOf(Characters)}.</p>
     *
     * @param characters the characters
     * @return a rule
     *
     * @see #anyOf(Characters)
     */
    @DontLabel
    public Rule anyOf(@Nonnull final String characters)
    {
        Preconditions.checkNotNull(characters, "characters");
        // TODO: see in this Characters class whether it is possible to wrap
        return anyOf(characters.toCharArray());
    }

    /**
     * Match any character in the given {@code char} array
     *
     * <p>This method delegates to {@link #anyOf(Characters)}.</p>
     *
     * @param characters the characters
     * @return a rule
     *
     * @see #anyOf(Characters)
     */
    @DontLabel
    public Rule anyOf(@Nonnull final char[] characters)
    {
        Preconditions.checkNotNull(characters, "characters");
        Preconditions.checkArgument(characters.length > 0);
        return characters.length == 1 ? ch(characters[0])
            : anyOf(Characters.of(characters));
    }

    /**
     * Match any given character among a set of characters
     *
     * <p>Both {@link #anyOf(char[])} and {@link #anyOf(String)} ultimately
     * delegate to this method, which caches its resuls.</p>
     *
     * @param characters the characters
     * @return a new rule
     */
    @Cached
    @DontLabel
    public Rule anyOf(@Nonnull final Characters characters)
    {
        Preconditions.checkNotNull(characters, "characters");
        if (!characters.isSubtractive() && characters.getChars().length == 1)
            return ch(characters.getChars()[0]);
        if (characters.equals(Characters.NONE))
            return NOTHING;
        return new AnyOfMatcher(characters);
    }

    /**
     * Match any characters <em>except</em> the ones contained in the strings
     *
     * @param characters the characters
     * @return a rule
     */
    @DontLabel
    public Rule noneOf(@Nonnull final String characters)
    {
        Preconditions.checkNotNull(characters, "characters");
        return noneOf(characters.toCharArray());
    }

    /**
     * Match all characters <em>except</em> the ones in the {@code char} array
     * given as an argument
     *
     * @param characters the characters
     * @return a new rule
     */
    @DontLabel
    public Rule noneOf(@Nonnull char[] characters)
    {
        Preconditions.checkNotNull(characters, "characters");
        Preconditions.checkArgument(characters.length > 0);

        // make sure to always exclude EOI as well
        boolean containsEOI = false;
        for (final char c: characters)
            if (c == Chars.EOI) {
                containsEOI = true;
                break;
            }
        if (!containsEOI) {
            final char[] withEOI = new char[characters.length + 1];
            System.arraycopy(characters, 0, withEOI, 0, characters.length);
            withEOI[characters.length] = Chars.EOI;
            characters = withEOI;
        }

        return anyOf(Characters.allBut(characters));
    }

    /**
     * Match a string literal
     *
     * @param string the string to match
     * @return a rule
     */
    @DontLabel
    public Rule string(@Nonnull final String string)
    {
        Preconditions.checkNotNull(string, "string");
        return string(string.toCharArray());
    }

    /**
     * Match a given set of characters as a string literal
     *
     * @param characters the characters of the string to match
     * @return a rule
     */
    @Cached
    @SuppressSubnodes
    @DontLabel
    public Rule string(@Nonnull final char... characters)
    {
        if (characters.length == 1)
            return ch(characters[0]); // optimize one-char strings
        final Rule[] matchers = new Rule[characters.length];
        for (int i = 0; i < characters.length; i++)
            matchers[i] = ch(characters[i]);
        return new StringMatcher(matchers, characters);
    }

    /**
     * Match a string literal in a case insensitive manner
     *
     * @param string the string to match
     * @return a rule
     */
    @DontLabel
    public Rule ignoreCase(@Nonnull final String string)
    {
        Preconditions.checkNotNull(string, "string");
        return ignoreCase(string.toCharArray());
    }

    /**
     * Match a sequence of characters as a string literal (case insensitive)
     *
     * @param characters the characters of the string to match
     * @return a rule
     */
    @Cached
    @SuppressSubnodes
    @DontLabel
    public Rule ignoreCase(@Nonnull final char... characters)
    {
        if (characters.length == 1)
            return ignoreCase(characters[0]); // optimize one-char strings
        final Rule[] matchers = new Rule[characters.length];
        for (int i = 0; i < characters.length; i++)
            matchers[i] = ignoreCase(characters[i]);
        return new SequenceMatcher(matchers)
            .label('"' + String.valueOf(characters) + '"');
    }

    /**
     * Match the first rule of a series of rules
     *
     * <p>When one rule matches, all others are ignored.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a rule
     */
    @DontLabel
    public Rule firstOf(@Nonnull final Object rule, @Nonnull final Object rule2,
        @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        final Object[] rules = ImmutableList.builder().add(rule).add(rule2)
            .add(moreRules).build().toArray();
        return firstOf(rules);
    }

    /**
     * Match the first rule of a series of rules
     *
     * <p>When one rule matches, all others are ignored.</p>
     *
     * @param rules the subrules
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule firstOf(@Nonnull final Object[] rules)
    {
        Preconditions.checkNotNull(rules, "rules");
        if (rules.length == 1)
            return toRule(rules[0]);

        final Rule[] convertedRules = toRules(rules);
        final int len = convertedRules.length;
        final char[][] chars = new char[rules.length][];

        Object rule;
        for (int i = 0; i < len; i++) {
            rule = convertedRules[i];
            if (!(rule instanceof StringMatcher))
                return new FirstOfMatcher(convertedRules);
            chars[i] = ((StringMatcher) rule).characters;
        }
        return new FirstOfStringsMatcher(convertedRules, chars);
    }

    /**
     * Try and match a rule repeatedly, at least once
     *
     * @param rule the subrule
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule oneOrMore(@Nonnull final Object rule)
    {
        return new OneOrMoreMatcher(toRule(rule));
    }

    /**
     * Try and repeatedly match a set of rules, at least once
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a rule
     */
    @DontLabel
    public Rule oneOrMore(@Nonnull final Object rule,
        @Nonnull final Object rule2, @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        return oneOrMore(Sequence(rule, rule2, moreRules));
    }

    /**
     * Try and match a rule zero or one time
     *
     * <p>This rule therefore always succeeds.</p>
     *
     * @param rule the subrule
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule optional(@Nonnull final Object rule)
    {
        Preconditions.checkNotNull(rule);
        return new OptionalMatcher(toRule(rule));
    }

    /**
     * Try and match a given set of rules once
     *
     * <p>This rule will therefore never fail.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a rule
     */
    @DontLabel
    public Rule optional(@Nonnull final Object rule,
        @Nonnull final Object rule2, @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        return optional(Sequence(rule, rule2, moreRules));
    }

    /**
     * Match a given set of rules, exactly once
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a rule
     */
    @DontLabel
    public Rule sequence(@Nonnull final Object rule,
        @Nonnull final Object rule2, @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        final Object[] rules = ImmutableList.builder().add(rule).add(rule2)
            .add(moreRules).build().toArray();
        return sequence(rules);
    }

    /**
     * Match a given set of rules, exactly once
     *
     * @param rules the rules
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule sequence(@Nonnull final Object[] rules)
    {
        Preconditions.checkNotNull(rules, "rules");
        return rules.length == 1 ? toRule(rules[0])
            : new SequenceMatcher(toRules(rules));
    }

    /**
     * Test a rule, but do not consume any input (predicate)
     *
     * <p>Its success conditions are the same as the rule. Note that this rule
     * will never consume any input, nor will it create a parse tree node.</p>
     *
     * <p>Note that the embedded rule can be arbitrarily complex, and this
     * includes potential {@link Action}s which can act on the stack for
     * instance; these <em>will</em> be executed here, unless you have chosen to
     * annotate your rule, or parser class, with {@link
     * SkipActionsInPredicates}.</p>
     *
     * @param rule the subrule
     * @return a new rule
     */
    @Cached
    @SuppressNode
    @DontLabel
    public Rule test(@Nonnull final Object rule)
    {
        final Rule subMatcher = toRule(rule);
        return new TestMatcher(subMatcher);
    }

    /**
     * Test a set of rules, but do not consume any input
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @see #test(Object)
     */
    @DontLabel
    public Rule test(@Nonnull final Object rule, @Nonnull final Object rule2,
        @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        return test(sequence(rule, rule2, moreRules));
    }

    /**
     * Test, without consuming an input, that a rule does not match
     *
     * <p>The same warnings given in the description of {@link #test(Object)}
     * apply here.</p>
     *
     * @param rule the subrule
     * @return a rule
     */
    @Cached
    @SuppressNode
    @DontLabel
    public Rule testNot(@Nonnull final Object rule)
    {
        return new TestNotMatcher(toRule(rule));
    }

    /**
     * Test that a set of rules do not apply at this position
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @see #test(Object)
     * @see #testNot(Object)
     */
    @DontLabel
    public Rule testNot(@Nonnull final Object rule, @Nonnull final Object rule2,
        @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        return testNot(sequence(rule, rule2, moreRules));
    }

    /**
     * Try and match a rule zero or more times
     *
     * <p>The rule will therefore always succeed.</p>
     *
     * @param rule the subrule
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule zeroOrMore(@Nonnull final Object rule)
    {
        return new ZeroOrMoreMatcher(toRule(rule));
    }

    /**
     * Try and match a set of rules zero or more times
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a rule
     */
    @DontLabel
    public Rule zeroOrMore(@Nonnull final Object rule,
        @Nonnull final Object rule2, @Nonnull final Object... moreRules)
    {
        Preconditions.checkNotNull(moreRules, "moreRules");
        return zeroOrMore(sequence(rule, rule2, moreRules));
    }

    /**
     * Match a rule a fixed number of times
     *
     * @param repetitions The number of repetitions to match. Must be &gt;= 0.
     * @param rule the sub rule to match repeatedly.
     * @return a rule
     */
    @Cached
    @DontLabel
    public Rule nTimes(final int repetitions, @Nonnull final Object rule)
    {
        return nTimes(repetitions, rule, null);
    }

    /**
     * Match a rule n times with a separator
     *
     * <p>That is, match:</p>
     *
     * <pre>
     *     rule, separator, rule, separator, ...
     * </pre>
     *
     * <p>If {@code separator} is null, this is equivalent to calling {@link
     * #nTimes(int, Object)}.</p>
     *
     * @param repetitions The number of repetitions to match. Must be &gt;= 0.
     * @param rule the sub rule to match repeatedly.
     * @param separator the separator to match, see description
     * @return a new rule
     */
    @Cached
    @DontLabel
    public Rule nTimes(final int repetitions, @Nonnull final Object rule,
        @Nullable final Object separator)
    {
        Preconditions.checkNotNull(rule, "rule");
        Preconditions.checkArgument(repetitions >= 0,
            "repetitions must be non-negative");
        if (repetitions == 0)
            return EMPTY;
        if (repetitions == 1)
            return toRule(rule);
        final ImmutableList.Builder<Object> builder
            = ImmutableList.builder().add(rule);
        final int size = separator == null
            ? repetitions - 1 : (repetitions - 1) * 2;
        final FluentIterable<Object> iterable
            = FluentIterable.from(Arrays.asList(separator, rule))
            .filter(Predicates.notNull()).cycle().limit(size);
        for (final Object o: iterable)
            builder.add(toRule(o));
        return sequence(builder.build().toArray());
    }

    /*
     * UTILITY RULES
     *
     *  All rules defined by RFC 5234, appendix B, section 1
     */

    /**
     * ALPHA as defined by RFC 5234, appendix B, section 1: ASCII letters
     *
     * <p>Therefore a-z, A-Z.</p>
     *
     * @return a rule
     */
    public Rule alpha()
    {
        return firstOf(charRange('a', 'z'), charRange('A', 'Z'));
    }

    /**
     * BIT as defined by RFC 5234, appendix B, section 1: {@code 0} or {@code 1}
     *
     * @return a rule
     */
    public Rule bit()
    {
        return anyOf(Characters.of('0', '1'));
    }

    /**
     * CHAR as defined by RFC 5234, appendix B, section 1: ASCII, except NUL
     *
     * <p>That is, 0x01 to 0x7f.</p>
     *
     * @return a rule
     */
    public Rule asciiChars()
    {
        return charRange((char) 0x01, (char) 0x7f);
    }

    /**
     * CR as defined by RFC 5234, appendix B, section 1 ({@code \r})
     *
     * @return a rule
     */
    public Rule cr()
    {
        return ch('\r');
    }

    /**
     * CRLF as defined by RFC 5234, appendix B, section 1 ({@code \r\n}
     *
     * @return a rule
     */
    public Rule crlf()
    {
        return string("\r\n");
    }

    /**
     * CTL as defined by RFC 5234, appendix B, section 1: control characters
     *
     * <p>0x00-0x1f, plus 0x7f.</p>
     *
     * @return a rule
     */
    public Rule ctl()
    {
        return firstOf(charRange((char) 0x00, (char) 0x1f), ch((char) 0x7f));
    }

    /**
     * DIGIT as defined by RFC 5234, appendix B, section 1 (0 to 9)
     *
     * @return a rule
     */
    public Rule digit()
    {
        return charRange('0', '9');
    }

    /**
     * DQUOTE as defined by RFC 5234, appendix B, section 1 {@code "}
     *
     * @return a rule
     */
    public Rule dquote()
    {
        return ch('"');
    }

    /**
     * Hexadecimal digits, case insensitive
     *
     * <p><b>Note:</b> RFC 5234 only defines {@code HEXDIG} for uppercase
     * letters ({@code A} to {@code F}). Use {@link #hexDigitUpperCase()} for
     * this definition. Use {@link #hexDigitLowerCase()} for lowercase letters
     * only.</p>
     *
     * @return a rule
     */
    public Rule hexDigit()
    {
        return anyOf("ABCDEFabcdef0123456789");
    }

    /**
     * Hexadecimal digits, uppercase
     *
     * @return a rule
     * @see #hexDigit()
     */
    public Rule hexDigitUpperCase()
    {
        return anyOf("ABCDEF0123456789");
    }

    /**
     * Hexadecimal digits, lowercase
     *
     * @return a rule
     * @see #hexDigit()
     */
    public Rule hexDigitLowerCase()
    {
        return anyOf("abcdef0123456789");
    }

    /**
     * HTAB as defined by RFC 5234, appendix B, section 1 ({@code \t})
     *
     * @return a rule
     */
    public Rule hTab()
    {
        return ch('\t');
    }

    /**
     * LF as defined by RFC 5234, appendix B, section 1 ({@code \n})
     *
     * @return a rule
     */
    public Rule lf()
    {
        return ch('\n');
    }

    /**
     * OCTET as defined by RFC 5234, appendix B, section 1 (0x00 to 0xff)
     *
     * @return a rule
     */
    public Rule octet()
    {
        return charRange((char) 0x00, (char) 0xff);
    }

    /**
     * SP as defined by RFC 5234, appendix B, section 1 (one space, 0x20)
     *
     * @return a rule
     */
    public Rule sp()
    {
        return ch(' ');
    }

    /**
     * VCHAR as defined by RFC 5234, appendix B, section 1: ASCII "visible"
     *
     * <p>Letters, {@code @}, etc etc. Note that this <strong>excludes</strong>
     * whitespace characters!</p>
     *
     * @return a rule
     */
    public Rule vchar()
    {
        return charRange((char) 0x21, (char) 0x7e);
    }

    /**
     * WSP as defined by RFC 5234, appendix B, section 1: space or tab
     *
     * @return a rule
     */
    public Rule wsp()
    {
        return anyOf(" \t");
    }

    /*
     * DEPRECATED RULES
     *
     * TODO: remove in 1.1
     */

    /**
     * Explicitly creates a rule matching the given character. Normally you can just specify the character literal
     * directly in you rule description. However, if you don't want to go through {@link #fromCharLiteral(char)},
     * e.g. because you redefined it, you can also use this wrapper.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param c the char to match
     * @return a new rule
     *
     * @deprecated use {@link #ch(char)} instead; will be removed in 1.1
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule Ch(char c)
    {
        return new CharMatcher(c);
    }

    /**
     * Explicitly creates a rule matching the given character case-independently.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param c the char to match independently of its case
     * @return a new rule
     *
     * @deprecated use {@link #ignoreCase(char)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule IgnoreCase(char c)
    {
        if (Character.isLowerCase(c) == Character.isUpperCase(c)) {
            return Ch(c);
        }
        return new CharIgnoreCaseMatcher(c);
    }

    /**
     * Match one Unicode character
     *
     * @param codePoint the code point
     * @return a rule
     *
     * @deprecated use {@link #unicodeChar(int)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule UnicodeChar(final int codePoint)
    {
        checkArgument(Character.isValidCodePoint(codePoint),
            "invalid code point " + codePoint);
        return UnicodeCharMatcher.forCodePoint(codePoint);
    }

    /**
     * Match a Unicode character range
     *
     * <p>Note that this method will delegate to "regular" character matchers if
     * part of, or all of, the specified range is into the basic multilingual
     * plane.</p>
     *
     * @param low the lower code point (inclusive)
     * @param high the upper code point (inclusive)
     * @return a rule
     *
     * @deprecated use {@link #unicodeRange(int, int)} instead; will be removed
     * in 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule UnicodeRange(final int low, final int high)
    {
        checkArgument(Character.isValidCodePoint(low),
            "invalid code point " + low);
        checkArgument(Character.isValidCodePoint(high),
            "invalid code point " + high);
        checkArgument(low <= high, "invalid range: " + low + " > " + high);
        return low == high ? UnicodeCharMatcher.forCodePoint(low)
            : UnicodeRangeMatcher.forRange(low, high);
    }

    /**
     * Creates a rule matching a range of characters from cLow to cHigh (both inclusively).
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param cLow the start char of the range (inclusively)
     * @param cHigh the end char of the range (inclusively)
     * @return a new rule
     *
     * @deprecated use {@link #charRange(char, char)} instead; will be removed
     * in 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule CharRange(char cLow, char cHigh)
    {
        return cLow == cHigh ? Ch(cLow) : new CharRangeMatcher(cLow, cHigh);
    }

    /**
     * Creates a new rule that matches any of the characters in the given string.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters
     * @return a new rule
     *
     * @deprecated use {@link #anyOf(String)} instead; will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule AnyOf(String characters)
    {
        checkArgNotNull(characters, "characters");
        return anyOf(characters.toCharArray());
    }

    /**
     * Creates a new rule that matches any of the characters in the given char array.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters
     * @return a new rule
     *
     * @deprecated use {@link #anyOf(char[])} instead; will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule AnyOf(char[] characters)
    {
        Preconditions.checkNotNull(characters, "characters");
        Preconditions.checkArgument(characters.length > 0);
        return characters.length == 1 ? Ch(characters[0])
            : AnyOf(Characters.of(characters));
    }

    /**
     * Creates a new rule that matches any of the given characters.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters
     * @return a new rule
     *
     * @deprecated use {@link #anyOf(Characters)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule AnyOf(Characters characters)
    {
        checkArgNotNull(characters, "characters");
        if (!characters.isSubtractive() && characters.getChars().length == 1) {
            return Ch(characters.getChars()[0]);
        }
        if (characters.equals(Characters.NONE))
            return NOTHING;
        return new AnyOfMatcher(characters);
    }

    /**
     * Creates a new rule that matches all characters except the ones in the given string and EOI.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters
     * @return a new rule
     *
     * @deprecated use {@link #noneOf(String)} instead; will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule NoneOf(String characters)
    {
        checkArgNotNull(characters, "characters");
        return NoneOf(characters.toCharArray());
    }

    /**
     * Creates a new rule that matches all characters except the ones in the given char array and EOI.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters
     * @return a new rule
     *
     * @deprecated use {@link #noneOf(char[])} instead; will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule NoneOf(char[] characters)
    {
        checkArgNotNull(characters, "characters");
        checkArgument(characters.length > 0);

        // make sure to always exclude EOI as well
        boolean containsEOI = false;
        for (char c : characters)
            if (c == Chars.EOI) {
                containsEOI = true;
                break;
            }
        if (!containsEOI) {
            char[] withEOI = new char[characters.length + 1];
            System.arraycopy(characters, 0, withEOI, 0, characters.length);
            withEOI[characters.length] = Chars.EOI;
            characters = withEOI;
        }

        return AnyOf(Characters.allBut(characters));
    }

    /**
     * Explicitly creates a rule matching the given string. Normally you can just specify the string literal
     * directly in you rule description. However, if you want to not go through {@link #fromStringLiteral(String)},
     * e.g. because you redefined it, you can also use this wrapper.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param string the String to match
     * @return a new rule
     *
     * @deprecated use {@link #string(String)} instead; will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule String(String string)
    {
        checkArgNotNull(string, "string");
        return String(string.toCharArray());
    }

    /**
     * Explicitly creates a rule matching the given string. Normally you can just specify the string literal
     * directly in you rule description. However, if you want to not go through {@link #fromStringLiteral(String)},
     * e.g. because you redefined it, you can also use this wrapper.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters of the string to match
     * @return a new rule
     *
     * @deprecated use {@link #string(char...)} instead; will be removed in 1.1.
     */
    @Deprecated
    @Cached
    @SuppressSubnodes
    @DontLabel
    public Rule String(char... characters)
    {
        if (characters.length == 1)
            return Ch(characters[0]); // optimize one-char strings
        Rule[] matchers = new Rule[characters.length];
        for (int i = 0; i < characters.length; i++) {
            matchers[i] = Ch(characters[i]);
        }
        return new StringMatcher(matchers, characters);
    }

    /**
     * Explicitly creates a rule matching the given string in a case-independent fashion.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param characters the characters of the string to match
     * @return a new rule
     *
     * @deprecated use {@link #ignoreCase(char...)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @SuppressSubnodes
    @DontLabel
    public Rule IgnoreCase(char... characters)
    {
        if (characters.length == 1)
            return IgnoreCase(characters[0]); // optimize one-char strings
        Rule[] matchers = new Rule[characters.length];
        for (int i = 0; i < characters.length; i++) {
            matchers[i] = IgnoreCase(characters[i]);
        }
        return ((SequenceMatcher) Sequence(matchers))
            .label('"' + String.valueOf(characters) + '"');
    }

    /**
     * Explicitly creates a rule matching the given string in a case-independent fashion.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param string the string to match
     * @return a new rule
     *
     * @deprecated use {@link #ignoreCase(String)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule IgnoreCase(String string)
    {
        checkArgNotNull(string, "string");
        return IgnoreCase(string.toCharArray());
    }

    /**
     * Creates a new rule that successively tries all of the given subrules and succeeds when the first one of
     * its subrules matches. If all subrules fail this rule fails as well.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #firstOf(Object, Object, Object...)} instead; will
     * be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule FirstOf(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return FirstOf(Utils.arrayOf(rule, rule2, moreRules));
    }

    /**
     * Creates a new rule that successively tries all of the given subrules and succeeds when the first one of
     * its subrules matches. If all subrules fail this rule fails as well.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param rules the subrules
     * @return a new rule
     *
     * @deprecated use {@link #firstOf(Object[])} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule FirstOf(Object[] rules)
    {
        checkArgNotNull(rules, "rules");
        if (rules.length == 1) {
            return toRule(rules[0]);
        }
        Rule[] convertedRules = toRules(rules);
        char[][] chars = new char[rules.length][];
        for (int i = 0, convertedRulesLength = convertedRules.length;
             i < convertedRulesLength; i++) {
            Object rule = convertedRules[i];
            if (rule instanceof StringMatcher) {
                chars[i] = ((StringMatcher) rule).characters;
            } else {
                return new FirstOfMatcher(convertedRules);
            }
        }
        return new FirstOfStringsMatcher(convertedRules, chars);
    }

    /**
     * Creates a new rule that tries repeated matches of its subrule and succeeds if the subrule matches at least once.
     * If the subrule does not match at least once this rule fails.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param rule the subrule
     * @return a new rule
     *
     * @deprecated use {@link #oneOrMore(Object)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule OneOrMore(Object rule)
    {
        return new OneOrMoreMatcher(toRule(rule));
    }

    /**
     * Creates a new rule that tries repeated matches of a sequence of the given subrules and succeeds if the sequence
     * matches at least once. If the sequence does not match at least once this rule fails.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #oneOrMore(Object, Object, Object...)} instead;
     * will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule OneOrMore(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return OneOrMore(Sequence(rule, rule2, moreRules));
    }

    /**
     * Creates a new rule that tries a match on its subrule and always succeeds, independently of the matching
     * success of its sub rule.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param rule the subrule
     * @return a new rule
     *
     * @deprecated use {@link #oneOrMore(Object)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule Optional(Object rule)
    {
        return new OptionalMatcher(toRule(rule));
    }

    /**
     * Creates a new rule that tries a match on the sequence of the given subrules and always succeeds, independently
     * of the matching success of its sub sequence.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #optional(Object, Object, Object...)} instead;
     * will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule Optional(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return Optional(Sequence(rule, rule2, moreRules));
    }

    /**
     * Creates a new rule that only succeeds if all of its subrule succeed, one after the other.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #sequence(Object, Object, Object...)} instead;
     * will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule Sequence(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return Sequence(Utils.arrayOf(rule, rule2, moreRules));
    }

    /**
     * Creates a new rule that only succeeds if all of its subrule succeed, one after the other.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rules the sub rules
     * @return a new rule
     *
     * @deprecated use {@link #sequence(Object[])} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule Sequence(Object[] rules)
    {
        checkArgNotNull(rules, "rules");
        return rules.length == 1 ? toRule(rules[0])
            : new SequenceMatcher(toRules(rules));
    }

    /**
     * <p>Creates a new rule that acts as a syntactic predicate, i.e. tests the given sub rule against the current
     * input position without actually matching any characters. Succeeds if the sub rule succeeds and fails if the
     * sub rule rails. Since this rule does not actually consume any input it will never create a parse tree node.</p>
     * <p>Also it carries a {@link SuppressNode} annotation, which means all sub nodes will also never create a parse
     * tree node. This can be important for actions contained in sub rules of this rule that otherwise expect the
     * presence of certain parse tree structures in their context.
     * Also see {@link SkipActionsInPredicates}</p>
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param rule the subrule
     * @return a new rule
     *
     * @deprecated use {@link #test(Object)} instead; will be removed in 1.1.
     */
    @Deprecated
    @Cached
    @SuppressNode
    @DontLabel
    public Rule Test(Object rule)
    {
        Rule subMatcher = toRule(rule);
        return new TestMatcher(subMatcher);
    }

    /**
     * <p>Creates a new rule that acts as a syntactic predicate, i.e. tests the sequence of the given sub rule against
     * the current input position without actually matching any characters. Succeeds if the sub sequence succeeds and
     * fails if the sub sequence rails. Since this rule does not actually consume any input it will never create a
     * parse tree node.</p>
     * <p>Also it carries a {@link SuppressNode} annotation, which means all sub nodes will also never create a parse
     * tree node. This can be important for actions contained in sub rules of this rule that otherwise expect the
     * presence of certain parse tree structures in their context.
     * Also see {@link SkipActionsInPredicates}</p>
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #test(Object, Object, Object...)} instead; will be
     * removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule Test(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return Test(Sequence(rule, rule2, moreRules));
    }

    /**
     * <p>Creates a new rule that acts as an inverse syntactic predicate, i.e. tests the given sub rule against the
     * current input position without actually matching any characters. Succeeds if the sub rule fails and fails if the
     * sub rule succeeds. Since this rule does not actually consume any input it will never create a parse tree node.</p>
     * <p>Also it carries a {@link SuppressNode} annotation, which means all sub nodes will also never create a parse
     * tree node. This can be important for actions contained in sub rules of this rule that otherwise expect the
     * presence of certain parse tree structures in their context.
     * Also see {@link SkipActionsInPredicates}</p>
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param rule the subrule
     * @return a new rule
     *
     * @deprecated use {@link #testNot(Object)} instead; will be removed in 1.1.
     */
    @Deprecated
    @Cached
    @SuppressNode
    @DontLabel
    public Rule TestNot(Object rule)
    {
        Rule subMatcher = toRule(rule);
        return new TestNotMatcher(subMatcher);
    }

    /**
     * <p>Creates a new rule that acts as an inverse syntactic predicate, i.e. tests the sequence of the given sub rules
     * against the current input position without actually matching any characters. Succeeds if the sub sequence fails
     * and fails if the sub sequence succeeds. Since this rule does not actually consume any input it will never create
     * a parse tree node.</p>
     * <p>Also it carries a {@link SuppressNode} annotation, which means all sub nodes will also never create a parse
     * tree node. This can be important for actions contained in sub rules of this rule that otherwise expect the
     * presence of certain parse tree structures in their context.
     * Also see {@link SkipActionsInPredicates}</p>
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #testNot(Object, Object, Object...)} instead; will
     * be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule TestNot(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return TestNot(Sequence(rule, rule2, moreRules));
    }

    /**
     * Creates a new rule that tries repeated matches of its subrule.
     * Succeeds always, even if the subrule doesn't match even once.
     * <p>Note: This methods carries a {@link Cached} annotation, which means that multiple invocations with the same
     * argument will yield the same rule instance.</p>
     *
     * @param rule the subrule
     * @return a new rule
     *
     * @deprecated use {@link #zeroOrMore(Object)} instead; will be removed in
     * 1.1.
     */

    @Deprecated
    @Cached
    @DontLabel
    public Rule ZeroOrMore(Object rule)
    {
        return new ZeroOrMoreMatcher(toRule(rule));
    }

    /**
     * Creates a new rule that tries repeated matches of the sequence of the given sub rules.
     * Succeeds always, even if the sub sequence doesn't match even once.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param rule the first subrule
     * @param rule2 the second subrule
     * @param moreRules the other subrules
     * @return a new rule
     *
     * @deprecated use {@link #zeroOrMore(Object, Object, Object...)} instead;
     * will be removed in 1.1.
     */
    @Deprecated
    @DontLabel
    public Rule ZeroOrMore(Object rule, Object rule2, Object... moreRules)
    {
        checkArgNotNull(moreRules, "moreRules");
        return ZeroOrMore(Sequence(rule, rule2, moreRules));
    }

    /**
     * Creates a new rule that repeatedly matches a given sub rule a certain fixed number of times.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param repetitions The number of repetitions to match. Must be &gt;= 0.
     * @param rule the sub rule to match repeatedly.
     * @return a new rule
     *
     * @deprecated use {@link #nTimes(int, Object)} instead; will be removed in
     * 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule NTimes(int repetitions, Object rule)
    {
        return NTimes(repetitions, rule, null);
    }

    /**
     * Creates a new rule that repeatedly matches a given sub rule a certain fixed number of times, optionally
     * separated by a given separator rule.
     * <p>Note: This methods provides caching, which means that multiple invocations with the same
     * arguments will yield the same rule instance.</p>
     *
     * @param repetitions The number of repetitions to match. Must be &gt;= 0.
     * @param rule the sub rule to match repeatedly.
     * @param separator the separator to match, if null the individual sub rules will be matched without separator.
     * @return a new rule
     *
     * @deprecated use {@link #nTimes(int, Object, Object)} instead; will be
     * removed in 1.1.
     */
    @Deprecated
    @Cached
    @DontLabel
    public Rule NTimes(int repetitions, Object rule, Object separator)
    {
        checkArgNotNull(rule, "rule");
        checkArgument(repetitions >= 0, "repetitions must be non-negative");
        switch (repetitions) {
            case 0:
                return EMPTY;
            case 1:
                return toRule(rule);
            default:
                Object[] rules = new Object[separator == null ? repetitions
                    : repetitions * 2 - 1];
                if (separator != null) {
                    for (int i = 0; i < rules.length; i++)
                        rules[i] = i % 2 == 0 ? rule : separator;
                } else
                    Arrays.fill(rules, rule);
                return Sequence(rules);
        }
    }

    ///************************* "MAGIC" METHODS ***************************///

    /**
     * Explicitly marks the wrapped expression as an action expression.
     * parboiled transforms the wrapped expression into an {@link Action} instance during parser construction.
     *
     * @param expression the expression to turn into an Action
     * @return the Action wrapping the given expression
     */
    public static <T> Action<T> ACTION(final boolean expression)
    {
        throw new UnsupportedOperationException(
            "ACTION(...) calls can only be used in Rule creating parser methods");
    }

    ///************************* HELPER METHODS ***************************///

    /**
     * Used internally to convert the given character literal to a parser rule.
     * You can override this method, e.g. for specifying a Sequence that automatically matches all trailing
     * whitespace after the character.
     *
     * @param c the character
     * @return the rule
     */
    @DontExtend
    protected Rule fromCharLiteral(char c)
    {
        return ch(c);
    }

    /**
     * Used internally to convert the given string literal to a parser rule.
     * You can override this method, e.g. for specifying a Sequence that automatically matches all trailing
     * whitespace after the string.
     *
     * @param string the string
     * @return the rule
     */
    @DontExtend
    protected Rule fromStringLiteral(@Nonnull final String string)
    {
        Preconditions.checkNotNull(string, "string");
        return fromCharArray(string.toCharArray());
    }

    /**
     * Used internally to convert the given char array to a parser rule.
     * You can override this method, e.g. for specifying a Sequence that automatically matches all trailing
     * whitespace after the characters.
     *
     * @param array the char array
     * @return the rule
     */
    @DontExtend
    protected Rule fromCharArray(@Nonnull final char[] array)
    {
        Preconditions.checkNotNull(array, "array");
        return string(array);
    }

    /**
     * Converts the given object array to an array of rules.
     *
     * @param objects the objects to convert
     * @return the rules corresponding to the given objects
     */
    @DontExtend
    public Rule[] toRules(@Nonnull final Object... objects)
    {
        Preconditions.checkNotNull(objects, "objects");
        final Rule[] rules = new Rule[objects.length];
        for (int i = 0; i < objects.length; i++)
            rules[i] = toRule(objects[i]);
        return rules;
    }

    /**
     * Converts the given object to a rule.
     * This method can be overriden to enable the use of custom objects directly
     * in rule specifications.
     *
     * @param obj the object to convert
     * @return the rule corresponding to the given object
     */
    @DontExtend
    public Rule toRule(@Nonnull final Object obj)
    {
        if (obj instanceof Rule)
            return (Rule) obj;
        if (obj instanceof Character)
            return fromCharLiteral((Character) obj);
        if (obj instanceof String)
            return fromStringLiteral((String) obj);
        if (obj instanceof char[])
            return fromCharArray((char[]) obj);
        if (obj instanceof Action) {
            final Action<?> action = (Action<?>) obj;
            return new ActionMatcher(action);
        }
        Checks.ensure(!(obj instanceof Boolean), "Rule specification contains "
            + "an unwrapped Boolean value, if you were trying to specify a "
            + "parser action wrap the expression with ACTION(...)");

        throw new GrammarException("'" + obj + "' cannot be automatically "
            + "converted to a parser Rule");
    }
}
