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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author The Stajistics Project
 */
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
    public void testAllMatcherMatches() {
        StatsKeyMatcher all = StatsKeyMatcher.all();
        assertTrue(all.matches(newKey("a")));
        assertTrue(all.matches(newKey("a.1")));
        assertTrue(all.matches(newKey("a.1.x")));
    }

    @Test
    public void testNoneMatcherMatches() {
        StatsKeyMatcher none = StatsKeyMatcher.none();
        assertFalse(none.matches(newKey("a")));
        assertFalse(none.matches(newKey("a.1")));
        assertFalse(none.matches(newKey("a.1.x")));
    }

	//TODO: way more tests
}
