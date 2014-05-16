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

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.google.common.base.Preconditions;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.parboiled.transform.process.GroupClassGenerator;

import javax.annotation.Nonnull;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.parboiled.transform.Types.BASE_VAR_INIT;

// TODO: move to transform/ subpackage?
@WillBeFinal(version = "1.1")
public class VarInitClassGenerator
    extends GroupClassGenerator
{
    public VarInitClassGenerator(final boolean forceCodeBuilding)
    {
        super(forceCodeBuilding);
    }

    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Preconditions.checkNotNull(method, "method");
        return method.containsVars();
    }

    @Override
    protected boolean appliesTo(final InstructionGraphNode group)
    {
        return group.isVarInitRoot();
    }

    @Override
    protected Type getBaseType()
    {
        return BASE_VAR_INIT;
    }

    @Override
    protected void generateMethod(final InstructionGroup group,
        final ClassWriter cw)
    {
        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "create",
            "()Ljava/lang/Object;", null, null);
        convertXLoads(group);
        group.getInstructions().accept(mv);

        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0); // trigger automatic computing
    }
}