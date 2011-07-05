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
package org.stajistics;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.Tracker;
import org.stajistics.tracker.TrackerLocator;
import org.stajistics.tracker.manual.ManualTracker;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public abstract class TestUtil {

    public static final double DELTA = 0.0000000000001;

    private TestUtil() {}

    public static void buildStatsKeyExpectations(final Mockery mockery,
                                                 final StatsKey mockKey,
                                                 final String keyName) {
        buildStatsKeyExpectations(mockery, mockKey, keyName, null, null);
    }

    public static void buildStatsKeyExpectations(final Mockery mockery,
                                                 final StatsKey mockKey,
                                                 final String keyName,
                                                 final String attrName,
                                                 final String attrValue) {
        mockery.checking(new Expectations() {{
            allowing(mockKey).getNamespace(); will(returnValue(StatsConstants.DEFAULT_NAMESPACE));
            allowing(mockKey).getName(); will(returnValue(keyName));
            allowing(mockKey).getAttribute(with((String)null)); will(returnValue(null));

            if (attrName == null) {
                allowing(mockKey).getAttribute((String)with(anything())); will(returnValue(null));
                allowing(mockKey).getAttributeCount(); will(returnValue(0));
                allowing(mockKey).getAttributes(); will(returnValue(Collections.emptyMap()));
            } else {
                allowing(mockKey).getAttribute(with(attrName)); will(returnValue(attrValue));
                allowing(mockKey).getAttributeCount(); will(returnValue(1));
                allowing(mockKey).getAttributes(); will(returnValue(Collections.singletonMap(attrName, attrValue)));
            }
        }});
    }

    /**
     * TODO: this needs more work to be useful
     *
     * @param mockery
     * @param mockKey
     * @return
     */
    public static StatsManager createMockStatsManager(final Mockery mockery,
                                                      final StatsKey mockKey) {
        final StatsManager mockManager = mockery.mock(StatsManager.class);

        final StatsConfigManager mockConfigManager = mockery.mock(StatsConfigManager.class);
        final EventManager mockEventManager = mockery.mock(EventManager.class);
        final StatsSessionManager mockSessionManager = mockery.mock(StatsSessionManager.class);
        final TrackerLocator mockTrackerLocator = mockery.mock(TrackerLocator.class);
        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);
        final StatsConfigBuilderFactory mockConfigBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        final Tracker mockTracker = mockery.mock(Tracker.class);
        final ManualTracker mockManualTracker = mockery.mock(ManualTracker.class);

        mockery.checking(new Expectations() {{
            allowing(mockManager).getConfigBuilderFactory(); will(returnValue(mockConfigBuilderFactory));
            allowing(mockManager).getConfigManager(); will(returnValue(mockConfigManager));
            allowing(mockManager).getEventManager(); will(returnValue(mockEventManager));
            allowing(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            allowing(mockManager).getSessionManager(); will(returnValue(mockSessionManager));
            allowing(mockManager).getTrackerLocator(); will(returnValue(mockTrackerLocator));

            allowing(mockTrackerLocator).getTracker(with(mockKey)); will(returnValue(mockTracker));
            allowing(mockTrackerLocator).getManualTracker(with(mockKey)); will(returnValue(mockManualTracker));
        }});

        return mockManager;
    }
}
