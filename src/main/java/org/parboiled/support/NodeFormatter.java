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

package org.parboiled.support;

import com.github.fge.grappa.buffers.InputBuffer;
import com.google.common.base.Preconditions;
import org.parboiled.Node;
import org.parboiled.common.Formatter;
import org.parboiled.trees.ParseTreeUtils;

import javax.annotation.Nonnull;

/**
 * A simple Formatter&lt;Node&gt; that provides String representation for parse
 * tree
 * nodes.
 */
public final class NodeFormatter<V>
    implements Formatter<Node<V>>
{

    private final InputBuffer inputBuffer;

    /**
     * Creates a new NodeFormatter.
     *
     * @param inputBuffer the input buffer underlying the parse tree whose nodes
     * are to be formatted.
     */
    public NodeFormatter(@Nonnull final InputBuffer inputBuffer)
    {
        this.inputBuffer = Preconditions.checkNotNull(inputBuffer);
    }

    @Override
    public String format(final Node<V> node)
    {
        final String nodeLabel = node.toString();
        final String text = ParseTreeUtils.getNodeText(node, inputBuffer);
        final String nodeText = CharsEscaper.INSTANCE.escape(text);
        return nodeText.isEmpty() ? nodeLabel
            : nodeLabel + " '" + nodeText + '\'';
    }

}
