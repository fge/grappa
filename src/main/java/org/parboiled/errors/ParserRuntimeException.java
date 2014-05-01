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

package org.parboiled.errors;

/**
 * Exception that is thrown for any problem during the parsing run that cannot be overcome automatically.
 */
public class ParserRuntimeException extends RuntimeException {

    public ParserRuntimeException() {
    }

    public ParserRuntimeException(final String message) {
        super(message);
    }

    public ParserRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParserRuntimeException(final String message, final Object... messageArgs) {
        super(String.format(message, messageArgs));
    }

    public ParserRuntimeException(final Throwable cause, final String message, final Object... messageArgs) {
        super(messageArgs.length > 0 ? String.format(message, messageArgs) : message, cause);
    }

    public ParserRuntimeException(final Throwable cause) {
        super(cause);
    }

}