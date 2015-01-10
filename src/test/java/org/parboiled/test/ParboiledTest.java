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

package org.parboiled.test;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import org.parboiled.Rule;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

import static org.assertj.core.api.Fail.fail;
import static org.parboiled.trees.ParseTreeUtils.printNodeTree;
import static org.testng.Assert.assertEquals;

public abstract class ParboiledTest<V> {

    public class TestResult<V> {
        public final ParsingResult<V> result;

        public TestResult(final ParsingResult<V> result) {
            this.result = result;
        }

        public TestResult<V> hasNoErrors() {
//            resultAssert.isSuccess();
            if (result.isSuccess())
                return this;
            fail("Errors detected");
            return this;
        }

        public TestResult<V> hasParseTree(final String expectedTree) {
            assertEquals(printNodeTree(result), expectedTree);
            return this;
        }
    }

    public TestResult<V> test(final Rule rule, final String input) {
        return new TestResult<>(new BasicParseRunner<V>(rule).run(input));
    }
    
    public TestResult<V> test(final Rule rule, final InputBuffer inputBuffer) {
        return new TestResult<>(new BasicParseRunner<V>(rule).run(inputBuffer));
    }
}