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

import org.parboiled.support.CharsEscaper;
import org.parboiled.common.ImmutableLinkedList;
import org.parboiled.matchers.Matcher;
import org.parboiled.trees.ImmutableTreeNode;

import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * An immutable implementation of the Node interface.
 */
class NodeImpl<V> extends ImmutableTreeNode<Node<V>> implements Node<V> {

    private final Matcher matcher;
    private final int startIndex;
    private final int endIndex;
    private final V value;
    private final boolean hasError;

    public NodeImpl(Matcher matcher, ImmutableLinkedList<Node<V>> children, int startIndex,
                    int endIndex, V value, boolean hasError) {
        super(children);
        this.matcher = checkArgNotNull(matcher, "matcher");
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.value = value;
        this.hasError = hasError;
    }

    @Override
    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public String getLabel() {
        return matcher.getLabel();
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public boolean hasError() {
        return hasError;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(getLabel());
        if (value != null) {
            sb.append(", {").append(value).append('}');
        }
        sb.append(']');
        if (hasError) sb.append('E'); 
        return CharsEscaper.INSTANCE.escape(sb.toString());
    }

}
