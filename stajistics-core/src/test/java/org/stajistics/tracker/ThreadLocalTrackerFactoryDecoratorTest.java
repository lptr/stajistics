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
package org.stajistics.tracker;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;
import org.stajistics.event.EventHandler;
import org.stajistics.event.EventManager;
import org.stajistics.session.StatsSessionManager;

import static org.junit.Assert.*;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class ThreadLocalTrackerFactoryDecoratorTest extends AbstractStajisticsTestCase {

    private TrackerFactory<Tracker> mockDelegate;
    private StatsSessionManager mockSessionManager;
    private EventManager mockEventManager;

    private StatsKey mockKey1;
    private StatsKey mockKey2;

    private Tracker mockTracker1;
    private Tracker mockTracker2;

    private TrackerFactory<Tracker> decorator;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockDelegate = mockery.mock(TrackerFactory.class);
        mockKey1 = mockery.mock(StatsKey.class, "StatsKey1");
        mockKey2 = mockery.mock(StatsKey.class, "StatsKey2");

        mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockEventManager = mockery.mock(EventManager.class);

        mockTracker1 = mockery.mock(Tracker.class, "Tracker1");
        mockTracker2 = mockery.mock(Tracker.class, "Tracker2");

        decorator = new ThreadLocalTrackerFactoryDecorator<Tracker>(mockDelegate, mockEventManager);
    }

    @Test
    public void testConstructWithNullDelegate() {
        try {
            new ThreadLocalTrackerFactoryDecorator<Tracker>(null, mockEventManager);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("delegate", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new ThreadLocalTrackerFactoryDecorator<Tracker>(mockDelegate, null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testCreateTrackerOnce() {
        mockery.checking(new Expectations() {{
            one(mockDelegate).createTracker(with(mockKey1),
                                            with(mockSessionManager));
            will(returnValue(mockTracker1));

            one(mockEventManager).addEventHandler(with(mockKey1),
                                                  with(aNonNull(EventHandler.class)));
        }});

        Tracker t1 = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1);
    }

    @Test
    public void testCreateTrackerTwice() {
        mockery.checking(new Expectations() {{
            one(mockDelegate).createTracker(with(mockKey1),
                                            with(mockSessionManager));
            will(returnValue(mockTracker1));

            one(mockEventManager).addEventHandler(with(mockKey1),
                                                  with(aNonNull(EventHandler.class)));
        }});

        Tracker t1a = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1a);

        Tracker t1b = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1b);
    }

    @Test
    public void testCreateTrackerWithManyKeys() {
        mockery.checking(new Expectations() {{
            one(mockDelegate).createTracker(with(mockKey1),
                                            with(mockSessionManager));
            will(returnValue(mockTracker1));

            one(mockEventManager).addEventHandler(with(mockKey1),
                                                  with(aNonNull(EventHandler.class)));

            one(mockDelegate).createTracker(with(mockKey2),
                                            with(mockSessionManager));
            will(returnValue(mockTracker2));

            one(mockEventManager).addEventHandler(with(mockKey2),
                                                  with(aNonNull(EventHandler.class)));
        }});

        Tracker t1a = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1a);

        Tracker t1b = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1b);

        Tracker t2a = decorator.createTracker(mockKey2, mockSessionManager);
        assertSame(mockTracker2, t2a);

        Tracker t2b = decorator.createTracker(mockKey2, mockSessionManager);
        assertSame(mockTracker2, t2b);
    }

    @Test
    public void testCreateTrackerWithManyKeysAndThreads() throws InterruptedException {
        mockery.checking(new Expectations() {{
            one(mockDelegate).createTracker(mockKey1,
                                            mockSessionManager);
            will(returnValue(mockTracker1));

            one(mockEventManager).addEventHandler(with(mockKey1),
                                                  with(aNonNull(EventHandler.class)));

            one(mockDelegate).createTracker(mockKey2,
                                            mockSessionManager);
            will(returnValue(mockTracker2));

            one(mockEventManager).addEventHandler(with(mockKey2),
                                                  with(aNonNull(EventHandler.class)));
        }});

        Tracker t1a = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1a);

        Tracker t1b = decorator.createTracker(mockKey1, mockSessionManager);
        assertSame(mockTracker1, t1b);

        Tracker t2a = decorator.createTracker(mockKey2, mockSessionManager);
        assertSame(mockTracker2, t2a);

        Tracker t2b = decorator.createTracker(mockKey2, mockSessionManager);
        assertSame(mockTracker2, t2b);
    }
}
