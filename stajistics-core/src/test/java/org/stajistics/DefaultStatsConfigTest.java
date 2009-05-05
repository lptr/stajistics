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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Mockery;
import org.junit.Test;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.tracker.NanoTimeDurationTracker;
import org.stajistics.tracker.TimeDurationTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigTest {

    @Test
    public void testConstructWithNullTrackerFactory() {
        try {
            new DefaultStatsConfig(true,
                                   null, 
                                   DefaultSessionFactory.getInstance(), 
                                   "unit", 
                                   "description");
            fail("Allowed construction with null StatsTrackerFactory");
        } catch (NullPointerException npe) {
            assertEquals("trackerFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullSessionFactory() {
        try {
            new DefaultStatsConfig(true,
                                   TimeDurationTracker.FACTORY, 
                                   null, 
                                   "unit", 
                                   "description");
            fail("Allowed construction with null StatsSessionFactory");
        } catch (NullPointerException npe) {
            assertEquals("sessionFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructionWithNullUnit() {
        try {
            new DefaultStatsConfig(true,
                                   TimeDurationTracker.FACTORY, 
                                   DefaultSessionFactory.getInstance(), 
                                   null, 
                                   "description");
            fail("Allowed construction with null unit");
        } catch (NullPointerException npe) {
            assertEquals("unit", npe.getMessage());
        }
    }

    @Test
    public void testConstructionWithEmptyUnit() {
        try {
            new DefaultStatsConfig(true,
                                   TimeDurationTracker.FACTORY, 
                                   DefaultSessionFactory.getInstance(), 
                                   "", 
                                   "description");
            fail("Allowed construction with empty unit");
        } catch (IllegalArgumentException iae) {
            assertEquals("empty unit", iae.getMessage());
        }
    }

    @Test
    public void testConstructionWithNullDescription() {
        new DefaultStatsConfig(true,
                               TimeDurationTracker.FACTORY, 
                               DefaultSessionFactory.getInstance(), 
                               "unit", 
                               null);
    }

    @Test
    public void testIsEnabled() {
        StatsConfig config = new DefaultStatsConfig(true,
                                                    TimeDurationTracker.FACTORY, 
                                                    DefaultSessionFactory.getInstance(), 
                                                    "unit", 
                                                    "description");
        assertTrue(config.isEnabled());

        config = new DefaultStatsConfig(false,
                                        TimeDurationTracker.FACTORY, 
                                        DefaultSessionFactory.getInstance(), 
                                        "unit", 
                                        "description");
        assertFalse(config.isEnabled());
    }
    
    @Test
    public void testGetTrackerFactory() {
        StatsConfig config = new DefaultStatsConfig(true,
                                                    TimeDurationTracker.FACTORY, 
                                                    DefaultSessionFactory.getInstance(), 
                                                    "unit", 
                                                    "description");
        assertSame(TimeDurationTracker.FACTORY, config.getTrackerFactory());
    }

    @Test
    public void testGetSessionFactory() {
        StatsConfig config = new DefaultStatsConfig(true,
                                                    TimeDurationTracker.FACTORY, 
                                                    DefaultSessionFactory.getInstance(), 
                                                    "unit", 
                                                    "description");
        assertSame(DefaultSessionFactory.getInstance(), config.getSessionFactory());
    }

    @Test
    public void testGetUnit() {
        StatsConfig config = new DefaultStatsConfig(true,
                                                    TimeDurationTracker.FACTORY, 
                                                    DefaultSessionFactory.getInstance(), 
                                                    "unit", 
                                                    "description");
        assertEquals("unit", config.getUnit());
    }

    @Test
    public void testGetDescription() {
        StatsConfig config = new DefaultStatsConfig(true,
                                                    TimeDurationTracker.FACTORY, 
                                                    DefaultSessionFactory.getInstance(), 
                                                    "unit", 
                                                    "description");
        assertEquals("description", config.getDescription());
    }

    @Test
    public void testEqualsWithSameEverything() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        assertEquals(config1, config2);
    }

    @Test
    public void testEqualsWithDifferentEnabled() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(false,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        assertFalse(config1.equals(config2));
    }

    @Test
    public void testEqualsWithDifferentTrackerFactory() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     NanoTimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        assertFalse(config1.equals(config2));
    }

    @Test
    public void testEqualsWithDifferentSessionFactory() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     new Mockery().mock(StatsSessionFactory.class), 
                                                     "unit", 
                                                     "description");
        assertFalse(config1.equals(config2));
    }

    @Test
    public void testEqualsWithDifferentUnit() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit1", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit2", 
                                                     "description");
        assertFalse(config1.equals(config2));
    }

    @Test
    public void testEqualsWithDifferentDescription() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description1");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description2");
        assertFalse(config1.equals(config2));
    }

    @Test
    public void testHashcodeWithSameEverything() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    public void testHashcodeWithDifferentEnabled() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(false,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        assertFalse(config1.hashCode() == config2.hashCode());
    }

    @Test
    public void testHashcodeWithDifferentTrackerFactory() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     NanoTimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        assertFalse(config1.hashCode() == config2.hashCode());
    }

    @Test
    public void testHashcodeWithDifferentSessionFactory() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     new Mockery().mock(StatsSessionFactory.class), 
                                                     "unit", 
                                                     "description");
        assertFalse(config1.hashCode() == config2.hashCode());
    }

    @Test
    public void testHashcodeWithDifferentUnit() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit1", 
                                                     "description");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit2", 
                                                     "description");
        assertFalse(config1.hashCode() == config2.hashCode());
    }

    @Test
    public void testHashcodeWithDifferentDescription() {
        StatsConfig config1 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description1");
        StatsConfig config2 = new DefaultStatsConfig(true,
                                                     TimeDurationTracker.FACTORY, 
                                                     DefaultSessionFactory.getInstance(), 
                                                     "unit", 
                                                     "description2");
        assertFalse(config1.hashCode() == config2.hashCode());
    }
}
