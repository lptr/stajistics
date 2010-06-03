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
package org.stajistics.session;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.*;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.session.recorder.DataRecorderFactory;

import static org.junit.Assert.*;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsSessionManagerTestCase extends AbstractStajisticsTestCase {

    protected StatsKey mockKey;
    protected StatsConfigManager mockConfigManager;
    protected EventManager mockEventManager;

    protected StatsSessionManager sessionManager;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        mockConfigManager = mockery.mock(StatsConfigManager.class);
        mockEventManager = mockery.mock(EventManager.class);

        sessionManager = createSessionManager();
    }

    protected abstract StatsSessionManager createSessionManager();

    protected StatsSession mockGetOrCreate(final StatsKey key) {
        final StatsConfig mockConfig = mockery.mock(StatsConfig.class);
        final DataRecorderFactory mockDataRecorderFactory = mockery.mock(DataRecorderFactory.class);
        final DataRecorder mockDataRecorder = mockery.mock(DataRecorder.class);
        final DataRecorder[] mockDataRecorders = new DataRecorder[] { mockDataRecorder };
        final StatsSessionFactory mockSessionFactory = mockery.mock(StatsSessionFactory.class);
        final StatsSession mockSession = mockery.mock(StatsSession.class);

        mockery.checking(new Expectations() {{
            one(mockConfigManager).getOrCreateConfig(key); will(returnValue(mockConfig));
            one(mockConfig).getDataRecorderFactory(); will(returnValue(mockDataRecorderFactory));
            one(mockDataRecorderFactory).createDataRecorders(); will(returnValue(mockDataRecorders));
            one(mockConfig).getSessionFactory(); will(returnValue(mockSessionFactory));
            one(mockSessionFactory).createSession(with(key),
                                                  with(aNonNull(StatsManager.class)),
                                                  with(mockDataRecorders));
            will(returnValue(mockSession));
            allowing(mockSession).getKey(); will(returnValue(key));

            ignoring(mockEventManager);
        }});

        return sessionManager.getOrCreateSession(key);
    }

    protected void assertInitialState(final StatsSessionManager sessionManager) {
        assertEquals(0, sessionManager.getSessionCount());
        assertNotNull(sessionManager.getKeys());
        assertTrue(sessionManager.getKeys().isEmpty());
        assertTrue(sessionManager.getSessions().isEmpty());
        assertNull(sessionManager.getSession(mockKey));
    }

    @Test
    public void testInitialState() {
        assertInitialState(sessionManager);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetKeysRetunsImmutableSet() {
        sessionManager.getKeys().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionsRetunsImmutableCollection() {
        sessionManager.getSessions().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionsWithKeyMatcherRetunsImmutableCollection() {
        sessionManager.getSessions(StatsKeyMatcher.all()).clear();
    }

    @Test
    public void testGetOrCreateSessionWithNullKey() {
        try {
            sessionManager.getOrCreateSession(null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testGetOrCreateSession() {
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        assertNull(sessionManager.getSession(mockKey));

        final StatsSession mockSession = mockGetOrCreate(mockKey);

        assertEquals(mockSession, sessionManager.getOrCreateSession(mockKey));
        assertEquals(mockSession, sessionManager.getSession(mockKey));
    }

    @Test
    public void testRemoveStatsSession() {
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final StatsSession mockSession = mockGetOrCreate(mockKey);

        assertTrue(sessionManager.remove(mockSession));
        assertNull(sessionManager.getSession(mockKey));
    }

    @Test
    public void testRemoveStatsKey() {
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final StatsSession mockSession = mockGetOrCreate(mockKey);

        assertEquals(mockSession, sessionManager.remove(mockKey));
    }

    @Test
    public void testClear() {
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");
        mockGetOrCreate(mockKey);

        sessionManager.clear();

        assertInitialState(sessionManager);
    }

    @Test
    public void testClearAllSessions() {
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");
        final StatsSession mockSession = mockGetOrCreate(mockKey);

        mockery.checking(new Expectations() {{
            one(mockSession).clear();
        }});

        sessionManager.clearAllSessions();

        // Ensure we didn't StatsSessionManager#clear() somehow.
        assertEquals(mockSession, sessionManager.getSession(mockKey));
    }
}
