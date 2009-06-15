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
package org.stajistics;

import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.tracker.ManualStatsTracker;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsTest {

    private Mockery mockery;
    private StatsManager mockManager;
    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockManager = mockery.mock(StatsManager.class);
        mockKey = mockery.mock(StatsKey.class);
        Stats.loadManager(mockManager);
    }

    @Test
    public void testGetManager() {
        assertSame(mockManager, Stats.getManager());
    }

    @Test
    public void testGetConfigManager() {
        mockery.checking(new Expectations() {{
            one(mockManager).getConfigManager();
        }});

        Stats.getConfigManager();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetSessionManager() {
        mockery.checking(new Expectations() {{
            one(mockManager).getSessionManager();
        }});

        Stats.getSessionManager();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetEventManager() {
        mockery.checking(new Expectations() {{
            one(mockManager).getEventManager();
        }});

        Stats.getEventManager();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testIsEnabled() {
        mockery.checking(new Expectations() {{
            one(mockManager).isEnabled();
        }});

        Stats.isEnabled();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetTrackerWithStatsKey() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
        }});

        assertSame(mockTracker, Stats.getTracker(mockKey));

        mockery.assertIsSatisfied();
    }

    @Test
    public void testTrackWithStatsKey() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).track();
        }});

        Stats.track(mockKey);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testIncidentWithStatsKey() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
            one(mockTracker).commit();
        }});

        Stats.incident(mockKey);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testManualWithStatsKey() {

        final ManualStatsTracker mockManualTracker = mockery.mock(ManualStatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getManualTracker(with(mockKey)); will(returnValue(mockManualTracker));
        }});

        assertSame(mockManualTracker, Stats.manual(mockKey));

        mockery.assertIsSatisfied();
    }

    @Test
    public void testNewKey() {
        final String keyName = "test";
        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(with(keyName)); will(returnValue(mockKey));
        }});

        assertSame(mockKey, Stats.newKey(keyName));

        mockery.assertIsSatisfied();
    }

    @Test
    public void testBuildKey() {
        final String keyName = "test";
        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);
        final StatsKeyBuilder mockKeyBuilder = mockery.mock(StatsKeyBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKeyBuilder(with(keyName)); will(returnValue(mockKeyBuilder));
        }});

        assertSame(mockKeyBuilder, Stats.buildKey(keyName));

        mockery.assertIsSatisfied();
    }

    @Test
    public void testBuildConfig() {
        final StatsConfigFactory mockConfigFactory = mockery.mock(StatsConfigFactory.class);
        final StatsConfigBuilder mockConfigBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getConfigFactory(); will(returnValue(mockConfigFactory));
            one(mockConfigFactory).createConfigBuilder(); will(returnValue(mockConfigBuilder));
        }});

        assertSame(mockConfigBuilder, Stats.buildConfig());

        mockery.assertIsSatisfied();
    }
}
