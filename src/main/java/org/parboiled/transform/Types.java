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

package org.parboiled.transform;

import org.objectweb.asm.Type;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.ContextAware;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.Cached;
import org.parboiled.annotations.DontExtend;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.DontSkipActionsInPredicates;
import org.parboiled.annotations.ExplicitActionsOnly;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SkipActionsInPredicates;
import org.parboiled.annotations.SkipNode;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.ProxyMatcher;
import org.parboiled.matchers.VarFramingMatcher;
import org.parboiled.support.Var;
import org.parboiled.transform.method.ParserAnnotation;

public interface Types {
    Type BASE_ACTION = Type.getType(BaseAction.class);
    Type BASE_VAR_INIT = Type.getType(BaseVarInit.class);
    Type BASE_PARSER = Type.getType(BaseParser.class);
    Type CONTEXT_AWARE = Type.getType(ContextAware.class);
    Type LABEL = Type.getType(Label.class);
    Type MATCHER = Type.getType(Matcher.class);
    Type PROXY_MATCHER = Type.getType(ProxyMatcher.class);
    Type RULE = Type.getType(Rule.class);
    Type VAR = Type.getType(Var.class);
    Type VAR_FRAMING_MATCHER = Type.getType(VarFramingMatcher.class);

    String ACTION_DESC = Type.getType(Action.class).getDescriptor();
    String CONTEXT_DESC = Type.getType(Context.class).getDescriptor();
    String LABEL_DESC = LABEL.getDescriptor();
    String MATCHER_DESC = MATCHER.getDescriptor();
    String RULE_DESC = RULE.getDescriptor();
    String VAR_DESC = VAR.getDescriptor();

    /**
     * @deprecated use {@link ParserAnnotation#DONT_LABEL} instead
     */
    @Deprecated
    String DONT_LABEL_DESC = Type.getType(DontLabel.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#EXPLICIT_ACTIONS_ONLY} instead
     */
    @Deprecated
    String EXPLICIT_ACTIONS_ONLY_DESC
        = Type.getType(ExplicitActionsOnly.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#SKIP_ACTIONS_IN_PREDICATES}
     * instead
     */
    @Deprecated
    String SKIP_ACTIONS_IN_PREDICATES_DESC
        = Type.getType(SkipActionsInPredicates.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#BUILD_PARSE_TREE} instead
     */
    @Deprecated
    String BUILD_PARSE_TREE_DESC = Type.getType(BuildParseTree.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#CACHED} instead
     */
    @Deprecated
    String CACHED_DESC = Type.getType(Cached.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#DONT_EXTEND} instead
     */
    @Deprecated
    String DONT_EXTEND_DESC = Type.getType(DontExtend.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#SUPPRESS_NODE} instead
     */
    @Deprecated
    String SUPPRESS_NODE_DESC = Type.getType(SuppressNode.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#SUPPRESS_SUBNODES} instead
     */
    @Deprecated
    String SUPPRESS_SUBNODES_DESC = Type.getType(SuppressSubnodes.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#DONT_SKIP_ACTIONS_IN_PREDICATES}
     * instead
     */
    @Deprecated
    String DONT_SKIP_ACTIONS_IN_PREDICATES_DESC = Type.getType(DontSkipActionsInPredicates.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#SKIP_NODE} instead
     */
    @Deprecated
    String SKIP_NODE_DESC = Type.getType(SkipNode.class).getDescriptor();
    /**
     * @deprecated use {@link ParserAnnotation#MEMO_MISMATCHES} instead
     */
    @Deprecated
    String MEMO_MISMATCHES_DESC = Type.getType(MemoMismatches.class).getDescriptor();
}
