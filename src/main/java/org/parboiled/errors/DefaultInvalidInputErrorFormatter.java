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

package org.parboiled.errors;

import com.google.common.base.Joiner;
import com.google.common.escape.Escaper;
import org.parboiled.common.Formatter;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.Characters;
import org.parboiled.support.Chars;
import org.parboiled.support.CharsEscaper;
import org.parboiled.support.MatcherPath;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Formatter} for {@link InvalidInputError}s that automatically creates the correct "expected" text
 * for the error.
 */
public final class DefaultInvalidInputErrorFormatter
    implements Formatter<InvalidInputError>
{
    private static final Escaper ESCAPER = CharsEscaper.INSTANCE;
    private static final Joiner JOINER = Joiner.on(", ");

    @Override
    public String format(final InvalidInputError object)
    {
        if (object == null)
            return "";

        final int len = object.getEndIndex() - object.getStartIndex();
        final StringBuilder sb = new StringBuilder();
        if (len > 0) {
            final char c = object.getInputBuffer().charAt(object.getStartIndex());
            if (c == Chars.EOI) {
                sb.append("Unexpected end of input");
            } else {
                sb.append("Invalid input '")
                    .append(ESCAPER.escape(String.valueOf(c)));
                if (len > 1)
                    sb.append("...");
                sb.append('\'');
            }
        } else {
            sb.append("Invalid input");
        }
        final String expectedString = getExpectedString(object);
        if (!expectedString.isEmpty()) {
            sb.append(", expected ").append(expectedString);
        }
        return sb.toString();
    }

    public String getExpectedString(final InvalidInputError error)
    {
        // In non recovery-mode there is no complexity in the error and start
        // indices since they are all stable. However, in recovery-mode the
        // RecoveringParseRunner inserts characters into the InputBuffer, which
        // requires for all indices taken before to be shifted. The
        // RecoveringParseRunner does this by changing the indexDelta of the
        // parse runner. All users of the ParseError will then automatically see
        // shifted start and end indices matching the state of the underlying
        // InputBuffer. However, since the failed MatcherPaths still carry the
        // "original" indices we need to unapply the IndexDelta in order to be
        // able to compare with them.
        final int pathStartIndex = error.getStartIndex() - error
            .getIndexDelta();

        final List<String> labelList = new ArrayList<>();
        for (final MatcherPath path : error.getFailedMatchers()) {
            final Matcher labelMatcher = ErrorUtils
                .findProperLabelMatcher(path, pathStartIndex);
            if (labelMatcher == null)
                continue;
            final String[] labels = getLabels(labelMatcher);
            for (final String label : labels) {
                if (label != null && !labelList.contains(label)) {
                    labelList.add(label);
                }
            }
        }
        return join(labelList);
    }

    /**
     * Gets the labels corresponding to the given matcher, AnyOfMatchers are
     * treated specially in that their label is constructed as a list of their
     * contents
     *
     * @param matcher the matcher
     * @return the labels
     */
    //TODO: make it so that this special treatment does not exist
    public String[] getLabels(final Matcher matcher)
    {
        final String label = matcher.getLabel();
        final String[] byDefault = { label };

        if (!(matcher instanceof AnyOfMatcher))
            return byDefault;

        final AnyOfMatcher anyOfMatcher = (AnyOfMatcher) matcher;
        final Characters characters = anyOfMatcher.getCharacters();

        if (!characters.toString().equals(label))
            return byDefault;

        if (characters.isSubtractive())
            return byDefault;

        final String[] labels = new String[characters.getChars().length];
        for (int i = 0; i < labels.length; i++)
            labels[i] = '\'' + String.valueOf(characters.getChars()[i]) + '\'';
        return labels;
    }

    public String join(final List<String> labelList)
    {
        if (labelList.isEmpty())
            return "";
        final StringBuilder sb = new StringBuilder("one of: [");
        JOINER.appendTo(sb, labelList);
        sb.append(']');
        return ESCAPER.escape(sb.toString());
    }
}
