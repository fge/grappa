/*
 * Copyright (c) 2009-2010 Ken Wenzel and Mathias Doenitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.parboiled.transform;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.parboiled.BaseParser;
import org.parboiled.support.Var;
import org.parboiled.transform.method.RuleAnnotation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.parboiled.transform.AsmUtils.getClassForType;
import static org.parboiled.transform.AsmUtils.isActionRoot;
import static org.parboiled.transform.AsmUtils.isAssignableTo;
import static org.parboiled.transform.AsmUtils.isBooleanValueOfZ;
import static org.parboiled.transform.AsmUtils.isVarRoot;
import static org.parboiled.transform.method.RuleAnnotation.*;

public class RuleMethod
    extends MethodNode
{
    private final List<InstructionGroup> groups
        = new ArrayList<InstructionGroup>();
    private final List<LabelNode> usedLabels = new ArrayList<LabelNode>();
    private final Set<RuleAnnotation> ruleAnnotations = EnumSet
        .noneOf(RuleAnnotation.class);

    private final Class<?> ownerClass;
    private final int parameterCount;
    private boolean containsImplicitActions;
        // calls to Boolean.valueOf(boolean)
    private boolean containsExplicitActions;
        // calls to BaseParser.ACTION(boolean)
    private boolean containsVars; // calls to Var.<init>(T)
    private boolean containsPotentialSuperCalls;
    private int numberOfReturns;
    private InstructionGraphNode returnInstructionNode;
    private List<InstructionGraphNode> graphNodes;
    private List<LocalVariableNode> localVarVariables;
    private boolean bodyRewritten;
    private boolean skipGeneration;

    public RuleMethod(final Class<?> ownerClass, final int access,
        final String name, final String desc, final String signature,
        final String[] exceptions, final boolean hasExplicitActionOnlyAnno,
        final boolean hasDontLabelAnno,
        final boolean hasSkipActionsInPredicates)
    {
        super(Opcodes.ASM4, access, name, desc, signature, exceptions);
        this.ownerClass = ownerClass;
        parameterCount = Type.getArgumentTypes(desc).length;

        if (parameterCount == 0)
            ruleAnnotations.add(CACHED);
        if (hasDontLabelAnno)
            ruleAnnotations.add(DONT_LABEL);
        if (hasExplicitActionOnlyAnno)
            ruleAnnotations.add(EXPLICIT_ACTIONS_ONLY);
        if (hasSkipActionsInPredicates)
            ruleAnnotations.add(SKIP_ACTIONS_IN_PREDICATES);
        skipGeneration = isSuperMethod();
    }

    public List<InstructionGroup> getGroups()
    {
        return groups;
    }

    public List<LabelNode> getUsedLabels()
    {
        return usedLabels;
    }

    public Class<?> getOwnerClass()
    {
        return ownerClass;
    }

    public boolean hasDontExtend()
    {
        return ruleAnnotations.contains(DONT_EXTEND);
    }

    public int getParameterCount()
    {
        return parameterCount;
    }

    public boolean containsImplicitActions()
    {
        return containsImplicitActions;
    }

    public void setContainsImplicitActions(
        final boolean containsImplicitActions)
    {
        this.containsImplicitActions = containsImplicitActions;
    }

    public boolean containsExplicitActions()
    {
        return containsExplicitActions;
    }

    public void setContainsExplicitActions(
        final boolean containsExplicitActions)
    {
        this.containsExplicitActions = containsExplicitActions;
    }

    public boolean containsVars()
    {
        return containsVars;
    }

    public boolean containsPotentialSuperCalls()
    {
        return containsPotentialSuperCalls;
    }

    public boolean hasCachedAnnotation()
    {
        return ruleAnnotations.contains(CACHED);
    }

    public boolean hasDontLabelAnnotation()
    {
        return ruleAnnotations.contains(DONT_LABEL);
    }

    public boolean hasSuppressNodeAnnotation()
    {
        return ruleAnnotations.contains(SUPPRESS_NODE);
    }

    public boolean hasSuppressSubnodesAnnotation()
    {
        return ruleAnnotations.contains(SUPPRESS_SUBNODES);
    }

    public boolean hasSkipActionsInPredicatesAnnotation()
    {
        return ruleAnnotations.contains(SKIP_ACTIONS_IN_PREDICATES);
    }

    public boolean hasSkipNodeAnnotation()
    {
        return ruleAnnotations.contains(SKIP_NODE);
    }

    public boolean hasMemoMismatchesAnnotation()
    {
        return ruleAnnotations.contains(MEMO_MISMATCHES);
    }

    public int getNumberOfReturns()
    {
        return numberOfReturns;
    }

    public InstructionGraphNode getReturnInstructionNode()
    {
        return returnInstructionNode;
    }

    public void setReturnInstructionNode(
        final InstructionGraphNode returnInstructionNode)
    {
        this.returnInstructionNode = returnInstructionNode;
    }

    public List<InstructionGraphNode> getGraphNodes()
    {
        return graphNodes;
    }

    public List<LocalVariableNode> getLocalVarVariables()
    {
        return localVarVariables;
    }

    public boolean isBodyRewritten()
    {
        return bodyRewritten;
    }

    public void setBodyRewritten()
    {
        this.bodyRewritten = true;
    }

    public boolean isSuperMethod()
    {
        Preconditions.checkState(!name.isEmpty());
        return name.charAt(0) == '$';
    }

    public InstructionGraphNode setGraphNode(final AbstractInsnNode insn,
        final BasicValue resultValue, final List<BasicValue> predecessors)
    {
        if (graphNodes == null) {
            // initialize with a list of null values
            graphNodes = Lists
                .newArrayList(new InstructionGraphNode[instructions.size()]);
        }
        final int index = instructions.indexOf(insn);
        InstructionGraphNode node = graphNodes.get(index);
        if (node == null) {
            node = new InstructionGraphNode(insn, resultValue);
            graphNodes.set(index, node);
        }
        node.addPredecessors(predecessors);
        return node;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
        final boolean visible)
    {
        final  boolean recorded = recordDesc(ruleAnnotations, desc);
        // FIXME...
        if (ruleAnnotations.contains(DONT_SKIP_ACTIONS_IN_PREDICATES))
            ruleAnnotations.remove(SKIP_ACTIONS_IN_PREDICATES);
        if (recorded)
            return null;
        // only keep visible annotations
        return visible ? super.visitAnnotation(desc, true) : null;
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
        final String name, final String desc)
    {
        switch (opcode) {
            case INVOKESTATIC:
                if (!ruleAnnotations.contains(EXPLICIT_ACTIONS_ONLY)
                    && isBooleanValueOfZ(owner, name, desc)) {
                    containsImplicitActions = true;
                } else if (isActionRoot(owner, name)) {
                    containsExplicitActions = true;
                }
                break;

            case INVOKESPECIAL:
                if ("<init>".equals(name)) {
                    if (isVarRoot(owner, name, desc)) {
                        containsVars = true;
                    }
                } else if (isAssignableTo(owner, BaseParser.class)) {
                    containsPotentialSuperCalls = true;
                }
                break;
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitInsn(final int opcode)
    {
        if (opcode == ARETURN)
            numberOfReturns++;
        super.visitInsn(opcode);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label)
    {
        usedLabels.add(getLabelNode(label));
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
        final Label dflt, final Label[] labels)
    {
        usedLabels.add(getLabelNode(dflt));
        for (final Label label : labels)
            usedLabels.add(getLabelNode(label));
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
        final Label[] labels)
    {
        usedLabels.add(getLabelNode(dflt));
        for (final Label label : labels)
            usedLabels.add(getLabelNode(label));
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitLineNumber(final int line, final Label start)
    {
        // do not record line numbers
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
        final String signature, final Label start, final Label end,
        final int index)
    {
        // only remember the local variables of Type org.parboiled.support.Var that are not parameters
        if (index > parameterCount && Var.class
            .isAssignableFrom(getClassForType(Type.getType(desc)))) {
            if (localVarVariables == null)
                localVarVariables = new ArrayList<LocalVariableNode>();
            localVarVariables.add(
                new LocalVariableNode(name, desc, null, null, null, index));
        }
    }

    @Override
    public String toString()
    {
        return name;
    }

    public void moveFlagsTo(final RuleMethod method)
    {
        Preconditions.checkNotNull(method);
        moveTo(ruleAnnotations, method.ruleAnnotations);
    }

    public boolean isGenerationSkipped()
    {
        return skipGeneration;
    }

    public void dontSkipGeneration()
    {
        skipGeneration = false;
    }

    public void suppressNode()
    {
        ruleAnnotations.add(SUPPRESS_NODE);
    }

}
