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

package org.parboiled.common;

import com.google.common.base.Joiner;
import org.testng.annotations.Test;

import static org.parboiled.common.ImmutableLinkedList.nil;
import static org.testng.Assert.assertEquals;

@Test(enabled = false)
public class ImmutableLinkedListTest
{
    private static final Joiner COMMA = Joiner.on(',');

    @Test
    public void testImmutableLinkedList() {
        assertEquals(nil().size(), 0);
        assertEquals(nil().prepend(5).size(), 1);
        assertEquals(nil().prepend(5).prepend(7).size(), 2);

        final ImmutableLinkedList<Object> abc = nil().prepend("c").prepend("b").prepend("a");
        assertEquals(COMMA.join(abc), "a,b,c");
        assertEquals(COMMA.join(abc.reverse()), "c,b,a");
    }
}
