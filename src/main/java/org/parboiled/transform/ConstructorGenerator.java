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

import com.google.common.base.Preconditions;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.support.Checks;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.parboiled.transform.AsmUtils.createArgumentLoaders;

/**
 * Adds one constructor for each of the ParserClassNode.constructors,
 * which simply delegates to the respective super constructor.
 */
class ConstructorGenerator {

    public void process(final ParserClassNode classNode) {
        Preconditions.checkNotNull(classNode, "classNode");
        Checks.ensure(!classNode.getConstructors().isEmpty(),
                "Could not extend parser class '%s', no constructor visible to derived classes found",
                classNode.getParentType().getClassName());
        for (final MethodNode constructor : classNode.getConstructors()) {
            createConstuctor(classNode, constructor);
        }

        createNewInstanceMethod(classNode);
    }

    @SuppressWarnings("unchecked")
    private void createConstuctor(
        final ParserClassNode classNode, final MethodNode constructor) {
        final MethodNode newConstructor =
                new MethodNode(ACC_PUBLIC, constructor.name, constructor.desc, constructor.signature,
                        (String[]) constructor.exceptions.toArray(new String[constructor.exceptions.size()]));

        final InsnList instructions = newConstructor.instructions;
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(createArgumentLoaders(constructor.desc));
        instructions.add(new MethodInsnNode(INVOKESPECIAL,
            classNode.getParentType().getInternalName(), "<init>",
            constructor.desc, false));
        instructions.add(new InsnNode(RETURN));

        classNode.methods.add(newConstructor);
    }

    @SuppressWarnings("unchecked")
    private void createNewInstanceMethod(final ParserClassNode classNode) {
        final MethodNode method = new MethodNode(ACC_PUBLIC, "newInstance", "()L" + Types.BASE_PARSER.getInternalName() + ';',
                null, null);
        final InsnList instructions = method.instructions;
        instructions.add(new TypeInsnNode(NEW, classNode.name));
        instructions.add(new InsnNode(DUP));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, classNode.name,
            "<init>", "()V", false));
        instructions.add(new InsnNode(ARETURN));

        classNode.methods.add(method);
    }

}