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

package org.parboiled.transform.process;

import com.github.parboiled1.grappa.transform.cache.ClassCache;
import com.google.common.base.Preconditions;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.parboiled.support.Checks;
import org.parboiled.transform.InstructionGraphNode;
import org.parboiled.transform.InstructionGroup;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.parboiled.transform.AsmUtils.getClassConstructor;
import static org.parboiled.transform.AsmUtils.getClassField;
import static org.parboiled.transform.AsmUtils.getClassMethod;

public final class InstructionGroupCreator
    implements RuleMethodProcessor
{
    private final Map<String, Integer> memberModifiers
        = new HashMap<>();
    private RuleMethod method;

    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.containsExplicitActions() || method.containsVars();
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        this.method = Preconditions.checkNotNull(method, "method");

        // create groups
        createGroups();

        // prepare groups for later stages
        for (final InstructionGroup group: method.getGroups()) {
            sort(group);
            markUngroupedEnclosedNodes(group);
            verify(group);
        }

        // check all non-group node for illegal accesses
        for (final InstructionGraphNode node : method.getGraphNodes())
            if (node.getGroup() == null)
                verifyAccess(node);
    }

    private void createGroups()
    {
        InstructionGroup group;

        for (final InstructionGraphNode node: method.getGraphNodes()) {
            if (!(node.isActionRoot() || node.isVarInitRoot()))
                continue;

            group = new InstructionGroup(node);
            markGroup(node, group);
            method.getGroups().add(group);
        }
    }

    private void markGroup(
        final InstructionGraphNode node, final InstructionGroup group) {
        final boolean condition = node == group.getRoot()
            || !node.isActionRoot() && !node.isVarInitRoot();
        Checks.ensure(condition, "Method '%s' contains illegal nesting of "
            + "ACTION and/or Var initializer constructs", method.name);

        if (node.getGroup() != null)
            return; // already visited

        node.setGroup(group);
        if (node.isXLoad())
            return;

        if (node.isVarInitRoot()) {
            Preconditions.checkState(node.getPredecessors().size() == 2);
            // only color the second predecessor branch
            markGroup(node.getPredecessors().get(1), group);
        } else {
            for (final InstructionGraphNode pred : node.getPredecessors())
                markGroup(pred, group);
        }
    }

    // sort the group instructions according to their method index
    private void sort(final InstructionGroup group)
    {
        final Comparator<InstructionGraphNode> comparator
            = new MethodIndexComparator(method.instructions);
        Collections.sort(group.getNodes(), comparator);
    }

    // also capture all group nodes "hidden" behind xLoads
    private void markUngroupedEnclosedNodes(final InstructionGroup group)
    {
        InstructionGraphNode node;
        boolean keepGoing;
        List<InstructionGraphNode> graphNodes;
        int startIndex, endIndex;

        do {
            keepGoing = false;
            graphNodes = method.getGraphNodes();
            startIndex = getIndexOfFirstInsn(group);
            endIndex = getIndexOfLastInsn(group);

            for (int i = startIndex; i < endIndex; i++) {
                node = graphNodes.get(i);
                if (node.getGroup() != null)
                    continue;

                markGroup(node, group);
                sort(group);
                keepGoing = true;
            }
        } while (keepGoing);
    }

    private void verify(final InstructionGroup group)
    {
        final List<InstructionGraphNode> nodes = group.getNodes();
        final int sizeMinus1 = nodes.size() - 1;

        // verify all instruction except for the last one (which must be the
        // root)
        Preconditions.checkState(nodes.get(sizeMinus1) == group.getRoot());

        InstructionGraphNode node;

        for (int i = 0; i < sizeMinus1; i++) {
            node = nodes.get(i);
            Checks.ensure(!node.isXStore(), "An ACTION or Var initializer in "
                + "rule method '%s' contains illegal writes to a local variable"
                + " or parameter",
                method.name);
            verifyAccess(node);
        }

        final int i = getIndexOfLastInsn(group) - getIndexOfFirstInsn(group);

        Checks.ensure(i == sizeMinus1, "Error during bytecode analysis of" +
            " rule method '%s': discontinuous group block", method.name);
    }

    private void verifyAccess(final InstructionGraphNode node)
    {
        switch (node.getInstruction().getOpcode()) {
            case GETFIELD:
            case GETSTATIC:
                final FieldInsnNode field
                    = (FieldInsnNode) node.getInstruction();
                Checks.ensure(!isPrivateField(field.owner, field.name),
                    "Rule method '%s' contains an illegal access to private "
                    + "field '%s'.\nMark the field protected or package-private"
                    + " if you want to prevent public access!",
                    method.name, field.name
                );
                break;

            case INVOKEVIRTUAL:
            case INVOKESTATIC:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                final MethodInsnNode calledMethod
                    = (MethodInsnNode) node.getInstruction();
                Checks.ensure(!isPrivate(calledMethod.owner, calledMethod.name,
                    calledMethod.desc),
                    "Rule method '%s' contains an illegal call to private" +
                    " method '%s'.\nMark '%s' protected or "
                    + "package-private if you want to prevent public access!",
                    method.name, calledMethod.name, calledMethod.name
                );
                break;
        }
    }

    private int getIndexOfFirstInsn(final InstructionGroup group)
    {
        return method.instructions
            .indexOf(group.getNodes().get(0).getInstruction());
    }

    private int getIndexOfLastInsn(final InstructionGroup group)
    {
        final List<InstructionGraphNode> graphNodes = group.getNodes();
        return method.instructions
            .indexOf(graphNodes.get(graphNodes.size() - 1).getInstruction());
    }

    private boolean isPrivateField(final String owner, final String name)
    {
        final String key = owner + '#' + name;
        Integer modifiers = memberModifiers.get(key);
        if (modifiers == null) {
            modifiers = getClassField(owner, name).getModifiers();
            memberModifiers.put(key, modifiers);
        }
        return Modifier.isPrivate(modifiers);
    }

    private boolean isPrivate(final String owner, final String name,
        final String desc)
    {
        return "<init>".equals(name) ? isPrivateInstantiation(owner, desc)
            : isPrivateMethod(owner, name, desc);
    }

    private boolean isPrivateMethod(final String owner, final String name,
        final String desc)
    {
        final String key = owner + '#' + name + '#' + desc;
        Integer modifiers = memberModifiers.get(key);
        if (modifiers == null) {
            modifiers = getClassMethod(owner, name, desc).getModifiers();
            memberModifiers.put(key, modifiers);
        }
        return Modifier.isPrivate(modifiers);
    }

    private boolean isPrivateInstantiation(final String owner,
        final String desc)
    {
        // first check whether the class is private
        Integer modifiers = memberModifiers.get(owner);
        if (modifiers == null) {
            modifiers = ClassCache.INSTANCE.loadClass(owner).getModifiers();
            //modifiers = getClassForInternalName(owner).getModifiers();
            memberModifiers.put(owner, modifiers);
        }
        if (Modifier.isPrivate(modifiers))
            return true;

        // then check whether the selected constructor is private
        final String key = owner + "#<init>#" + desc;
        modifiers = memberModifiers.get(key);
        if (modifiers == null) {
            modifiers = getClassConstructor(owner, desc).getModifiers();
            memberModifiers.put(key, modifiers);
        }
        return Modifier.isPrivate(modifiers);
    }

    private static final class MethodIndexComparator
        implements Comparator<InstructionGraphNode>
    {
        private final InsnList instructions;

        private MethodIndexComparator(@Nonnull final InsnList instructions)
        {
            this.instructions = Preconditions.checkNotNull(instructions);
        }

        @Override
        public int compare(final InstructionGraphNode o1,
            final InstructionGraphNode o2)
        {
            final int i1 = instructions.indexOf(o1.getInstruction());
            final int i2 = instructions.indexOf(o2.getInstruction());
            return Integer.compare(i1, i2);
        }
    }
}