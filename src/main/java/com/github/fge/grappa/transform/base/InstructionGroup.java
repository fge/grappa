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

package com.github.fge.grappa.transform.base;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of instructions belonging to a ACTION or Var initializer
 */
public final class InstructionGroup
{

    private final List<InstructionGraphNode> nodes
        = new ArrayList<>();
    private final InsnList instructions = new InsnList();
    private final InstructionGraphNode root;
    private final List<FieldNode> fields = new ArrayList<>();
    private String name;
    private Type groupClassType;
    private byte[] groupClassCode;

    public InstructionGroup(final InstructionGraphNode root)
    {
        this.root = root;
    }

    public List<InstructionGraphNode> getNodes()
    {
        return nodes;
    }

    public InsnList getInstructions()
    {
        return instructions;
    }

    public InstructionGraphNode getRoot()
    {
        return root;
    }

    public List<FieldNode> getFields()
    {
        return fields;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Type getGroupClassType()
    {
        return groupClassType;
    }

    public void setGroupClassType(final Type groupClassType)
    {
        this.groupClassType = groupClassType;
    }

    public byte[] getGroupClassCode()
    {
        return groupClassCode;
    }

    public void setGroupClassCode(final byte[] groupClassCode)
    {
        this.groupClassCode = groupClassCode;
    }

    @Override
    public String toString()
    {
        return name != null ? name : super.toString();
    }
}
