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

package org.parboiled.transform.process;

import com.google.common.base.Preconditions;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.parboiled.transform.AsmUtils.createArgumentLoaders;

/**
 * Replaces the method code with a simple call to the super method.
 */
//TODO: final in 1.1
public class BodyWithSuperCallReplacer
    implements RuleMethodProcessor
{
    @Override
    public boolean appliesTo(final ParserClassNode classNode,
        final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return !method.isBodyRewritten()
            && method.getOwnerClass() == classNode.getParentClass()
            && method.getLocalVarVariables().isEmpty();
            // if we have local variables we need to create a VarFramingMatcher
            // which needs access to the local variables
    }

    @Override
    public void process(final ParserClassNode classNode,
        final RuleMethod method)
        throws Exception
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        // replace all method code with a simple call to the super method
        method.instructions.clear();
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(createArgumentLoaders(method.desc));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL,
            classNode.getParentType().getInternalName(), method.name,
            method.desc, false));
        method.instructions.add(new InsnNode(ARETURN));
    }
}