/* Copyright 2009 - 2010 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stajistics.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyBuilder;
import org.stajistics.TestUtil;

/**
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class StatsDecoratorsTest {

    private Mockery mockery;

    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");
    }

    @Test
    public void testWrapRunnable() {
        final Runnable mockRunnable = mockery.mock(Runnable.class);
        final Runnable wrappedRunnable = StatsDecorators.wrap(mockRunnable, mockKey);

        mockery.checking(new Expectations() {{
            one(mockRunnable).run();
        }});

        wrappedRunnable.run();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWrapCallable() throws Exception {
        final Callable<Long> mockCallable = mockery.mock(Callable.class);
        final Callable<Long> wrappedCallable = StatsDecorators.wrap(mockCallable, mockKey);

        mockery.checking(new Expectations() {{
            one(mockCallable).call(); will(returnValue(666L));
        }});

        assertEquals(666L, (long)wrappedCallable.call());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWrapCallableThrowsException() throws Exception {
        final Callable<Long> mockCallable = mockery.mock(Callable.class);
        final Callable<Long> wrappedCallable = StatsDecorators.wrap(mockCallable, mockKey);
        final Exception exception = new Exception();
        final StatsKeyBuilder mockKeyBuilder = mockery.mock(StatsKeyBuilder.class);
        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "test2");

        mockery.checking(new Expectations() {{
            one(mockCallable).call(); will(throwException(exception));
            one(mockKey).buildCopy(); will(returnValue(mockKeyBuilder));

            //TODO: this is more-so checking the exception key handling
            // rather than callable wrapper exception handling
            one(mockKeyBuilder).withNameSuffix(with("exception")); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withAttribute(with("threw"),
                                              with("java.lang.Exception")); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).newKey(); will(returnValue(mockKey2));
        }});

        try {
            wrappedCallable.call();
            fail();
        } catch (Exception e) {
            assertEquals(exception, e);
        }
    }

    @Test
    public void testWrapObserver() {
        final Observer mockObserver = mockery.mock(Observer.class);
        final Observer wrappedObserver = StatsDecorators.wrap(mockObserver, mockKey);
        final Observable observable = new Observable();

        mockery.checking(new Expectations() {{
            one(mockObserver).update(with(observable),
                                     with("hello"));
        }});

        wrappedObserver.update(observable, "hello");
    }

    @Test
    public void testWrapThreadFactory() {
        final ThreadFactory mockThreadFactory = mockery.mock(ThreadFactory.class);
        final ThreadFactory wrappedThreadFacrtory = StatsDecorators.wrap(mockThreadFactory, mockKey);
        final Runnable mockRunnable = mockery.mock(Runnable.class);
        final Thread thread = new Thread("test");

        mockery.checking(new Expectations() {{
            one(mockThreadFactory).newThread(with(aNonNull(Runnable.class))); will(returnValue(thread));
        }});

        assertEquals(thread, wrappedThreadFacrtory.newThread(mockRunnable));
    }
}
