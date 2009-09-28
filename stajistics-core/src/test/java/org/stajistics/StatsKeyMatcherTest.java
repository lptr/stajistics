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
import static org.junit.Assert.assertTrue;
import static org.stajistics.StatsKeyMatcher.all;
import static org.stajistics.StatsKeyMatcher.none;

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
    public void testFilterKeys() {
        Collection<StatsKey> keys = new ArrayList<StatsKey>();
        keys.add(newKey("a"));
        keys.add(newKey("b"));
        keys.add(newKey("c"));
        keys = Collections.unmodifiableCollection(keys);

        assertTrue(keys != all().filterKeys(keys));
        assertEquals(3, all().filterKeys(keys).size());
        assertTrue(none().filterKeys(keys).isEmpty());
    }

    @Test
    public void testFilterToCollectionWithMap() {
        final Object o1 = new Object();
        final Object o2 = new Object();

        Map<StatsKey,Object> map = new HashMap<StatsKey,Object>(2);
        map.put(newKey("a"), o1);
        map.put(newKey("b"), o2);

        assertEquals(2, all().filterToCollection(map).size());
        assertTrue(none().filterToCollection(map).isEmpty());
    }

    @Test
    public void testFilterToMapWithMap() {
        final Object o1 = new Object();
        final Object o2 = new Object();

        Map<StatsKey,Object> map = new HashMap<StatsKey,Object>(2);
        map.put(newKey("a"), o1);
        map.put(newKey("b"), o2);

        assertTrue(map != all().filterToMap(map));
        assertEquals(2, all().filterToCollection(map).size());
        assertTrue(none().filterToCollection(map).isEmpty());
    }
}
