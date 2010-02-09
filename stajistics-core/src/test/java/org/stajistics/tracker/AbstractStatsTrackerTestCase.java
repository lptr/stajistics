/* Copyright 2009 The Stajistics Project
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public abstract class AbstractStatsTrackerTestCase<T extends StatsTracker> {

    protected StatsSession mockSession;

    protected Mockery mockery;

    protected abstract T createStatsTracker();

    @Before
    public void setUp() throws Exception {
        mockery = new Mockery();
        mockSession = mockery.mock(StatsSession.class);
    }

    @Test
    public void testCreate() {
        final StatsTracker tracker = createStatsTracker();

        assertEquals(mockSession, tracker.getSession());
        assertEquals(0, tracker.getValue(), 0);
    }

    @Test
    public void testConstantFactoryField() throws Exception {

        final StatsTracker tracker = createStatsTracker();

        Class<?> trackerClass = tracker.getClass();
        Field field = trackerClass.getDeclaredField("FACTORY");

        Object fieldValue = field.get(0);

        assertNotNull(fieldValue);
        assertTrue(fieldValue instanceof StatsTrackerFactory<?>);

        final StatsKey mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final StatsSessionManager mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockery.checking(new Expectations() {{
            allowing(mockSessionManager).getOrCreateSession(mockKey);
            will(returnValue(mockSession));
        }});

        StatsTrackerFactory<?> factory = (StatsTrackerFactory<?>)fieldValue;
        StatsTracker tracker2 = factory.createTracker(mockKey, mockSessionManager);

        assertEquals(trackerClass, tracker2.getClass());
    }

}