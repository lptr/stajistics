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
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyBuilder;
import org.stajistics.StatsManager;
import org.stajistics.TestUtil;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 * @author The Stajistics Project
 */
public class StatsDecoratorsTest extends AbstractStajisticsTestCase {

    private StatsDecorators decorators;

    private SpanTracker mockSpanTracker;
    private StatsKey mockKey;
    
    private StatsFactory mockFactory;
    

    @Before
    public void setUp() {
        mockSpanTracker = mockery.mock(SpanTracker.class);

        mockFactory = mockery.mock(StatsFactory.class);

        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "key");

        mockery.checking(new Expectations() {{
            /*allowing(mockManager).getTrackerLocator();
            will(returnValue(mockTrackerLocator));

            allowing(mockTrackerLocator).getSpanTracker(mockKey);
            will(returnValue(mockSpanTracker));*/
        }});

        decorators = new StatsDecorators(mockFactory);
    }

    @Test
    public void testWrapRunnable() {
        final Runnable mockRunnable = mockery.mock(Runnable.class);
        final Runnable wrappedRunnable = decorators.wrap(mockRunnable, mockKey);

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey);
            will(returnValue(mockSpanTracker));

            one(mockRunnable).run();

            one(mockSpanTracker).commit();
        }});

        wrappedRunnable.run();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWrapCallable() throws Exception {
        final Callable<Long> mockCallable = mockery.mock(Callable.class);
        final Callable<Long> wrappedCallable = decorators.wrap(mockCallable, mockKey);

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey);
            will(returnValue(mockSpanTracker));

            one(mockCallable).call();
            will(returnValue(666L));

            one(mockSpanTracker).commit();
        }});

        assertEquals(666L, (long)wrappedCallable.call());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWrapCallableThrowsException() throws Exception {
        final Callable<Long> mockCallable = mockery.mock(Callable.class);
        final Callable<Long> wrappedCallable = decorators.wrap(mockCallable, mockKey);
        final Exception exception = new Exception();

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey);
            will(returnValue(mockSpanTracker));

            one(mockCallable).call();
            will(throwException(exception));

            one(mockSpanTracker).commit();

            one(mockFactory).failure(exception, mockKey);
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
        final Observer wrappedObserver = decorators.wrap(mockObserver, mockKey);
        final Observable observable = new Observable();

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey);
            will(returnValue(mockSpanTracker));

            one(mockObserver).update(observable, "hello");

            one(mockSpanTracker).commit();
        }});

        wrappedObserver.update(observable, "hello");
    }

    @Test
    public void testWrapThreadFactory() {
        final ThreadFactory mockThreadFactory = mockery.mock(ThreadFactory.class);
        final ThreadFactory wrappedThreadFacrtory = decorators.wrap(mockThreadFactory, mockKey);
        final Runnable mockRunnable = mockery.mock(Runnable.class);
        final Thread thread = new Thread("test");

        mockery.checking(new Expectations() {{
            one(mockThreadFactory).newThread(with(aNonNull(Runnable.class)));
            will(returnValue(thread));
        }});

        assertEquals(thread, wrappedThreadFacrtory.newThread(mockRunnable));
    }
}
