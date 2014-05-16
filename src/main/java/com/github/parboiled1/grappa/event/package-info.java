/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

/**
 * Event-based parser
 *
 * <p>The base parser class ({@link
 * com.github.parboiled1.grappa.event.EventBusParser}) uses Guava's {@link
 * com.google.common.eventbus.EventBus} to dispatch events.</p>
 *
 * <p>The choice of this class over other implementations is performance;
 * <a href="https://github.com/bennidi/mbassador"
 * target="_blank">mbassador</a> was also considered, as it claimed to perform
 * better, but tests done in the <a
 * href="https://github.com/parboiled1/grappa-support" target="_blank">support
 * project</a> proved otherwise: Guava's {@code EventBus} is three times as
 * fast for the needs of this package. And speed matters here :)</p>
 *
 * <p>The basic architecture is simple: you create event classes accepting a
 * {@link org.parboiled.Context} as an argument, extract what you want from the
 * context, and fire events with a name associated to your event class; the bus
 * will then dispatch this event class to all callers accepting this event class
 * as an argument.</p>
 *
 * <p>This allows to reuse existing classes by the means of composition, which
 * is much easier than creating the bean in the parser and retrieving it
 * afterwards. For instance you can do this:</p>
 *
 * <pre>
 *     public class MyBeanListener
 *     {
 *         private final MyBean bean = new MyBean();
 *
 *         &#0040;Subscribe
 *         public void setValue(final MyEventClass event)
 *         {
 *             bean.setMyValue(event.getValue());
 *         }
 *     }
 * </pre>
 *
 * <p>You then register the event class by name to the parser, register the
 * listener, and fire the event by name when appropriate.</p>
 *
 * <p>See the documentation of individual classes for more details.</p>
 */
package com.github.parboiled1.grappa.event;