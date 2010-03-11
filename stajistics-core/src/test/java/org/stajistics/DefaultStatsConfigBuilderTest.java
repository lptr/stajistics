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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.session.recorder.DataRecorderFactory;
import org.stajistics.tracker.TrackerFactory;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class DefaultStatsConfigBuilderTest {

    private Mockery mockery;
    private StatsConfigManager configManager;
    private StatsConfigBuilder builder;

    @Before
    public void setUp() {
        mockery = new Mockery();
        configManager = mockery.mock(StatsConfigManager.class);
        builder = new DefaultStatsConfigBuilder(configManager);   
    }

    @Test
    public void testConstruct() {
        StatsConfig config = builder.newConfig();
        assertNotNull(config.getTrackerFactory());
        assertNotNull(config.getSessionFactory());
        assertNotNull(config.getUnit());
        assertNull(config.getDescription());
    }

    @Test
    public void testConstructWithConfig() {
        final TrackerFactory<?> trackerFactory = mockery.mock(TrackerFactory.class);
        final StatsSessionFactory sessionFactory = mockery.mock(StatsSessionFactory.class);
        final DataRecorderFactory dataRecorderFactory = mockery.mock(DataRecorderFactory.class);
        final StatsConfig template = mockery.mock(StatsConfig.class);
        mockery.checking(new Expectations() {{
            atLeast(1).of(template).isEnabled(); will(returnValue(true));
            atLeast(1).of(template).getTrackerFactory(); will(returnValue(trackerFactory));
            atLeast(1).of(template).getSessionFactory(); will(returnValue(sessionFactory));
            atLeast(1).of(template).getDataRecorderFactory(); will(returnValue(dataRecorderFactory));
            atLeast(1).of(template).getUnit(); will(returnValue("testUnit"));
            atLeast(1).of(template).getDescription(); will(returnValue("testDescription"));
        }});

        builder = new DefaultStatsConfigBuilder(configManager, template);
        StatsConfig config = builder.newConfig();
        assertTrue(config.isEnabled());
        assertSame(trackerFactory, config.getTrackerFactory());
        assertSame(sessionFactory, config.getSessionFactory());
        assertEquals("testUnit", config.getUnit());
        assertEquals("testDescription", config.getDescription());
    }

    @Test
    public void testWithTrackerFactory() {
        TrackerFactory<?> trackerFactory = mockery.mock(TrackerFactory.class);
        builder.withTrackerFactory(trackerFactory);
        assertSame(trackerFactory, builder.newConfig().getTrackerFactory());
    }

    @Test
    public void testWithNullTrackerFactory() {
        try {
            builder.withTrackerFactory(null);
            fail("Allowed null TrackerFactory");
        } catch (NullPointerException npe) {
            assertEquals("trackerFactory", npe.getMessage());
        }
    }

    @Test
    public void testWithSessionFactory() {
        StatsSessionFactory sessionFactory = mockery.mock(StatsSessionFactory.class);
        builder.withSessionFactory(sessionFactory);
        assertSame(sessionFactory, builder.newConfig().getSessionFactory());
    }

    @Test
    public void testWithNullSessionFactory() {
        try {
            builder.withSessionFactory(null);
            fail("Allowed null StatsSessionFactory");
        } catch (NullPointerException npe) {
            assertEquals("sessionFactory", npe.getMessage());
        }
    }

    @Test
    public void testWithUnit() {
        builder.withUnit("test");
        assertEquals("test", builder.newConfig().getUnit());
    }

    @Test
    public void testWithNullUnit() {
        try {
            builder.withUnit(null);
            fail("Allowed null unit");
        } catch (NullPointerException npe) {
            assertEquals("unit", npe.getMessage());
        }
    }

    @Test
    public void testWithDescription() {
        builder.withDescription("test");
        assertEquals("test", builder.newConfig().getDescription());
    }

    @Test
    public void testWithNullDescription() {
        builder.withDescription(null);
        assertNull(builder.newConfig().getDescription());
    }
}
