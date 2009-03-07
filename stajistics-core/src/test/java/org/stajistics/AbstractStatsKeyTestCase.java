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
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsKeyTestCase {

    protected static final String TEST_NAME = "testName";
    protected static final String TEST_UNIT = "testUnit";
    protected static final Map<String,Object> TEST_ATTRIBUTES = new HashMap<String,Object>();
    protected static final Class<? extends StatsTracker> TEST_TRACKER_CLASS = Constants.DEFAULT_TRACKER_CLASS;
    protected static final Class<? extends StatsSession> TEST_SESSION_CLASS = Constants.DEFAULT_SESSION_CLASS;

    protected abstract StatsKey createStatsKey(String name,
                                               Map<String,Object> attributes);

    protected StatsKey createStatsKey(final String name) {
        return createStatsKey(name, 
                              TEST_ATTRIBUTES);
    }

    @Test
    public void testConstructWithNullName() {
        try {
            createStatsKey(null);

            fail("Allowed construction with null name");

        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullAttributes() {
        try {
            createStatsKey(TEST_NAME, null);

            fail("Allowed construction with null attributes");

        } catch (NullPointerException npe) {
            assertEquals("attributes", npe.getMessage());
        }
    }

    @Test
    public void testEqualsSameInstance() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertEquals(key, key);
    }

    @Test
    public void testEqualsNull() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertFalse(key.equals(null));
    }

    @Test
    public void testEqualsDifferentType() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertFalse(key.equals("123"));
    }

    @Test
    public void testEqualsKeyWithSameName() {
        StatsKey key1 = createStatsKey(TEST_NAME);
        StatsKey key2 = createStatsKey(TEST_NAME);
        assertEquals(key1, key2);
    }

    @Test
    public void testEqualsKeyWithDifferentName() {
        StatsKey key1 = createStatsKey(TEST_NAME);
        StatsKey key2 = createStatsKey(TEST_NAME + "2");
        assertFalse(key1.equals(key2));
    }

    @Test
    public void testEqualsKeyWithDifferentAttributes() {
        StatsKey key1 = createStatsKey(TEST_NAME, TEST_ATTRIBUTES);
        StatsKey key2 = createStatsKey(TEST_NAME, 
                                       Collections.<String,Object>singletonMap("test", "test"));
        assertFalse(key1.equals(key2));
    }

    @Test
    public void testHashCodeWithSameName() {
        StatsKey key1 = createStatsKey(TEST_NAME);
        StatsKey key2 = createStatsKey(TEST_NAME);
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void testHashCodeWithDifferentName() {
        StatsKey key1 = createStatsKey(TEST_NAME);
        StatsKey key2 = createStatsKey(TEST_NAME + "2");
        assertFalse(key1.hashCode() == key2.hashCode());
    }
}
