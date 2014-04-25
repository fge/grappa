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

import com.google.common.collect.ImmutableList;
import org.objectweb.asm.ClassWriter;
import org.parboiled.transform.process.BodyWithSuperCallReplacer;
import org.parboiled.transform.process.CachingGenerator;
import org.parboiled.transform.process.FlagMarkingGenerator;
import org.parboiled.transform.process.ImplicitActionsConverter;
import org.parboiled.transform.process.InstructionGraphCreator;
import org.parboiled.transform.process.InstructionGroupCreator;
import org.parboiled.transform.process.InstructionGroupPreparer;
import org.parboiled.transform.process.LabellingGenerator;
import org.parboiled.transform.process.ReturnInstructionUnifier;
import org.parboiled.transform.process.RuleMethodProcessor;
import org.parboiled.transform.process.RuleMethodRewriter;
import org.parboiled.transform.process.SuperCallRewriter;
import org.parboiled.transform.process.UnusedLabelsRemover;
import org.parboiled.transform.process.VarFramingGenerator;

import java.util.List;

import static org.parboiled.common.Preconditions.checkArgNotNull;
import static org.parboiled.transform.AsmUtils.findLoadedClass;
import static org.parboiled.transform.AsmUtils.getExtendedParserClassName;
import static org.parboiled.transform.AsmUtils.loadClass;

public class ParserTransformer {

    private ParserTransformer() {}

    @SuppressWarnings({"unchecked"})
    public static synchronized <T> Class<? extends T> transformParser(Class<T> parserClass) throws Exception {
        checkArgNotNull(parserClass, "parserClass");
        // first check whether we did not already create and load the extension of the given parser class
        Class<?> extendedClass = findLoadedClass(
                getExtendedParserClassName(parserClass.getName()), parserClass.getClassLoader()
        );
        return (Class<? extends T>)
                (extendedClass != null ? extendedClass : extendParserClass(parserClass).getExtendedClass());
    }

    /**
     * Dump the bytecode of a transformed parser class
     *
     * <p>This method will run all bytecode transformations on the parser class
     * then return a dump of the bytecode as a byte array.</p>
     *
     * @param parserClass the parser class
     * @return a bytecode dump
     * @throws Exception FIXME
     *
     * @see #extendParserClass(Class)
     */
    public static byte[] getByteCode(final Class<?> parserClass)
        throws Exception
    {
        final ParserClassNode node = extendParserClass(parserClass);
        return node.getClassCode();
    }

    static ParserClassNode extendParserClass(Class<?> parserClass) throws Exception {
        ParserClassNode classNode = new ParserClassNode(parserClass);
        new ClassNodeInitializer().process(classNode);
        runMethodTransformers(classNode);
        new ConstructorGenerator().process(classNode);
        defineExtendedParserClass(classNode);
        return classNode;
    }

    @SuppressWarnings({"unchecked"})
    private static void runMethodTransformers(ParserClassNode classNode) throws Exception {
        List<RuleMethodProcessor> methodProcessors = createRuleMethodProcessors();

        // iterate through all rule methods
        // since the ruleMethods map on the classnode is a treemap we get the methods sorted by name which puts
        // all super methods first (since they are prefixed with one or more '$')
        for (RuleMethod ruleMethod : classNode.getRuleMethods().values()) {
            if (!ruleMethod.hasDontExtend()) {
                for (RuleMethodProcessor methodProcessor : methodProcessors) {
                    if (methodProcessor.appliesTo(classNode, ruleMethod)) {
                        methodProcessor.process(classNode, ruleMethod);
                    }
                }
            }
        }

        for (RuleMethod ruleMethod : classNode.getRuleMethods().values()) {
            if (!ruleMethod.isGenerationSkipped()) {
                classNode.methods.add(ruleMethod);
            }
        }
    }

    static List<RuleMethodProcessor> createRuleMethodProcessors() {
        return ImmutableList.of(
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
                new FlagMarkingGenerator(),
                new CachingGenerator()
        );
    }

    private static void defineExtendedParserClass(ParserClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        classNode.setClassCode(classWriter.toByteArray());
        classNode.setExtendedClass(loadClass(
                classNode.name.replace('/', '.'),
                classNode.getClassCode(),
                classNode.getParentClass().getClassLoader()
        ));
    }

}
