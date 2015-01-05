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

import com.github.parboiled1.grappa.annotations.DoNotUse;
import org.objectweb.asm.Type;
import org.parboiled.BaseParser;
import org.parboiled.Rule;

/**
 * DON'T USE
 */
@DoNotUse
public interface Types
{
    Type BASE_ACTION = Type.getType(BaseAction.class);
    Type BASE_VAR_INIT = Type.getType(BaseVarInit.class);
    Type BASE_PARSER = Type.getType(BaseParser.class);
    Type RULE = Type.getType(Rule.class);
}
