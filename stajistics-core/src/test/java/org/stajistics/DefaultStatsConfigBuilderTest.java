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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
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
        final StatsTrackerFactory trackerFactory = mockery.mock(StatsTrackerFactory.class);
        final StatsSessionFactory sessionFactory = mockery.mock(StatsSessionFactory.class);
        final StatsConfig template = mockery.mock(StatsConfig.class);
        mockery.checking(new Expectations() {{
            atLeast(1).of(template).isEnabled(); will(returnValue(true));
            atLeast(1).of(template).getTrackerFactory(); will(returnValue(trackerFactory));
            atLeast(1).of(template).getSessionFactory(); will(returnValue(sessionFactory));
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
        mockery.assertIsSatisfied();
    }

    @Test
    public void testWithTrackerFactory() {
        StatsTrackerFactory trackerFactory = mockery.mock(StatsTrackerFactory.class);
        builder.withTrackerFactory(trackerFactory);
        assertSame(trackerFactory, builder.newConfig().getTrackerFactory());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testWithNullTrackerFactory() {
        try {
            builder.withTrackerFactory(null);
            fail("Allowed null StatsTrackerFactory");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testWithSessionFactory() {
        StatsSessionFactory sessionFactory = mockery.mock(StatsSessionFactory.class);
        builder.withSessionFactory(sessionFactory);
        assertSame(sessionFactory, builder.newConfig().getSessionFactory());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testWithNullSessionFactory() {
        try {
            builder.withSessionFactory(null);
            fail("Allowed null StatsSessionFactory");
        } catch (NullPointerException npe) {
            // expected
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
            // expected
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
