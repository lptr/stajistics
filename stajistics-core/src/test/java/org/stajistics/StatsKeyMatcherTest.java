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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class StatsKeyMatcherTest {

    private Mockery mockery;

    @Before
    public void setUp() {
        mockery = new Mockery();
    }

    private StatsKey newKey(final String name) {
        StatsKey mockKey = mockery.mock(StatsKey.class, name);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, name);
        return mockKey;
    }

    @Test
    public void testFilterWithCollection() {

        final StatsKey keyA = newKey("a");
        final StatsKey keyB = newKey("b");
        final StatsKey keyC = newKey("c");

        Collection<StatsKey> keys = new ArrayList<StatsKey>();
        keys.add(keyA);
        keys.add(keyB);
        keys.add(keyC);

        StatsKeyMatcher.equals("a").filter(keys);

        assertEquals(1, keys.size());
        assertTrue(keys.contains(keyA));
        assertFalse(keys.contains(keyB));
        assertFalse(keys.contains(keyC));
        assertEquals(keyA, keys.iterator().next());
    }

    @Test
    public void testFilterCopyWithCollection() {

        final StatsKey keyA = newKey("a");
        final StatsKey keyB = newKey("b");
        final StatsKey keyC = newKey("c");

        Collection<StatsKey> keys = new ArrayList<StatsKey>();
        keys.add(keyA);
        keys.add(keyB);
        keys.add(keyC);
        keys = Collections.unmodifiableCollection(keys);

        Collection<StatsKey> filteredKeys = StatsKeyMatcher.equals("a").filterCopy(keys);

        assertFalse(keys == filteredKeys);
        assertFalse(keys.equals(filteredKeys));

        assertEquals(1, filteredKeys.size());
        assertTrue(filteredKeys.contains(keyA));
        assertFalse(filteredKeys.contains(keyB));
        assertFalse(filteredKeys.contains(keyC));
        assertEquals(keyA, filteredKeys.iterator().next());
    }

    @Test
    public void testFilterWithMap() {
        final StatsKey keyA = newKey("a");
        final StatsKey keyB = newKey("b");

        final Object o1 = new Object();
        final Object o2 = new Object();

        Map<StatsKey,Object> keys = new HashMap<StatsKey,Object>();
        keys.put(keyA, o1);
        keys.put(keyB, o2);

        StatsKeyMatcher.equals("a").filter(keys);

        assertEquals(1, keys.size());
        assertTrue(keys.containsKey(keyA));
        assertFalse(keys.containsKey(keyB));
        assertEquals(keyA, keys.keySet().iterator().next());
    }

    @Test
    public void testFilterCopyWithMap() {

        final StatsKey keyA = newKey("a");
        final StatsKey keyB = newKey("b");

        final Object o1 = new Object();
        final Object o2 = new Object();

        Map<StatsKey,Object> keys = new HashMap<StatsKey,Object>();
        keys.put(keyA, o1);
        keys.put(keyB, o2);
        keys = Collections.unmodifiableMap(keys);

        Map<StatsKey,Object> filteredKeys = StatsKeyMatcher.equals("a").filterCopy(keys);

        assertFalse(keys == filteredKeys);
        assertFalse(keys.equals(filteredKeys));

        assertEquals(1, filteredKeys.size());
        assertTrue(filteredKeys.containsKey(keyA));
        assertFalse(filteredKeys.containsKey(keyB));
        assertEquals(keyA, filteredKeys.keySet().iterator().next());
    }

    @Test
    public void testFilterToCollectionWithMap() {

        final StatsKey keyA = newKey("a");
        final StatsKey keyB = newKey("b");

        final Object o1 = new Object();
        final Object o2 = new Object();

        Map<StatsKey,Object> map = new HashMap<StatsKey,Object>(2);
        map.put(keyA, o1);
        map.put(keyB, o2);

        Collection<Object> result = StatsKeyMatcher.equals("a").filterToCollection(map);

        assertEquals(1, result.size());
        assertTrue(result.contains(o1));
        assertFalse(result.contains(o2));
        assertEquals(o1, result.iterator().next());
    }

}
