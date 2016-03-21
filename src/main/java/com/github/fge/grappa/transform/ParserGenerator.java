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

package com.github.fge.grappa.transform;

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.transform.base.ParserClassNode;
import com.github.fge.grappa.transform.base.RuleMethod;
import com.github.fge.grappa.transform.generate.ActionClassGenerator;
import com.github.fge.grappa.transform.generate.ClassNodeBootstrap;
import com.github.fge.grappa.transform.generate.ConstructorGenerator;
import com.github.fge.grappa.transform.generate.VarInitClassGenerator;
import com.github.fge.grappa.transform.load.ClassLoaderList;
import com.github.fge.grappa.transform.load.ReflectiveClassLoader;
import com.github.fge.grappa.transform.process.BodyWithSuperCallReplacer;
import com.github.fge.grappa.transform.process.CachingGenerator;
import com.github.fge.grappa.transform.process.ImplicitActionsConverter;
import com.github.fge.grappa.transform.process.InstructionGraphCreator;
import com.github.fge.grappa.transform.process.InstructionGroupCreator;
import com.github.fge.grappa.transform.process.InstructionGroupPreparer;
import com.github.fge.grappa.transform.process.LabellingGenerator;
import com.github.fge.grappa.transform.process.ReturnInstructionUnifier;
import com.github.fge.grappa.transform.process.RuleMethodProcessor;
import com.github.fge.grappa.transform.process.RuleMethodRewriter;
import com.github.fge.grappa.transform.process.SuperCallRewriter;
import com.github.fge.grappa.transform.process.UnusedLabelsRemover;
import com.github.fge.grappa.transform.process.VarFramingGenerator;
import com.google.common.collect.ImmutableList;
import org.objectweb.asm.ClassWriter;

import java.util.List;
import java.util.Objects;

import static com.github.fge.grappa.misc.AsmUtils.getExtendedParserClassName;

public final class ParserGenerator<V, P extends BaseParser<V>>
{
    private static final List<RuleMethodProcessor> PROCESSORS;

    static {
        PROCESSORS = ImmutableList.of(
            new UnusedLabelsRemover(),
            new ReturnInstructionUnifier(),
            new InstructionGraphCreator(),
            new ImplicitActionsConverter(),
            new InstructionGroupCreator(),
            new InstructionGroupPreparer(),
            new ActionClassGenerator(false),
            new VarInitClassGenerator(false),
            new RuleMethodRewriter(),
            new SuperCallRewriter(),
            new BodyWithSuperCallReplacer(),
            new VarFramingGenerator(),
            new LabellingGenerator(),
            new CachingGenerator()
        );
    }

    private final Class<P> parserClass;
    private final ClassLoaderList classLoaders;

    public ParserGenerator(final Class<P> parserClass,
        final ClassLoaderList classLoaders)
    {
        this.parserClass = Objects.requireNonNull(parserClass);
        this.classLoaders = classLoaders;
    }

    public Class<? extends P> transformParser()
        throws Exception
    {
        Objects.requireNonNull(parserClass, "parserClass");
        // first check whether we did not already create and load the extension
        // of the given parser class
        final String name = getExtendedParserClassName(parserClass.getName());

        final Class<?> extendedClass;

        try (
            final ReflectiveClassLoader loader
                = new ReflectiveClassLoader(parserClass.getClassLoader());
        ) {
            extendedClass = loader.findClass(name);
        }

        final Class<?> ret = extendedClass != null
            ? extendedClass
            : extendParserClass().getExtendedClass();
        return (Class<? extends P>) ret;
    }

    /**
     * Dump the bytecode of a transformed parser class
     *
     * <p>This method will run all bytecode transformations on the parser class
     * then return a dump of the bytecode as a byte array.</p>
     *
     * @return a bytecode dump
     *
     * @throws Exception FIXME
     */
    // TODO: poor exception specification
    public byte[] getByteCode()
        throws Exception
    {
        final ParserClassNode node = extendParserClass();
        return node.getClassCode();
    }

    private ParserClassNode extendParserClass()
        throws Exception
    {
        final ParserClassNode classNode = new ParserClassNode(parserClass);
        new ClassNodeBootstrap(classNode, classLoaders).process();
        runMethodTransformers(classNode);
        new ConstructorGenerator().process(classNode);
        defineExtendedParserClass(classNode);
        return classNode;
    }

    // TODO: poor exception handling again
    private static void runMethodTransformers(final ParserClassNode classNode)
        throws Exception
    {
        // TODO: comment above may be right, but it's still dangerous
        // iterate through all rule methods
        // since the ruleMethods map on the classnode is a treemap we get the
        // methods sorted by name which puts all super methods first (since they
        // are prefixed with one or more '$')
        for (final RuleMethod ruleMethod: classNode.getRuleMethods().values()) {
            if (ruleMethod.hasDontExtend())
                continue;

            for (final RuleMethodProcessor methodProcessor: PROCESSORS)
                if (methodProcessor.appliesTo(classNode, ruleMethod))
                    methodProcessor.process(classNode, ruleMethod);
        }

        for (final RuleMethod ruleMethod: classNode.getRuleMethods().values())
            if (!ruleMethod.isGenerationSkipped())
                classNode.methods.add(ruleMethod);
    }

    private static void defineExtendedParserClass(final ParserClassNode node)
    {
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        node.accept(writer);
        node.setClassCode(writer.toByteArray());

        final String className = node.name.replace('/', '.');
        final byte[] bytecode = node.getClassCode();

        final ClassLoader classLoader = node.getParentClass().getClassLoader();
        final Class<?> extendedClass;

        try (
            final ReflectiveClassLoader loader
                = new ReflectiveClassLoader(classLoader);
        ) {
            extendedClass = loader.loadClass(className, bytecode);
        }

        node.setExtendedClass(extendedClass);
    }
}
