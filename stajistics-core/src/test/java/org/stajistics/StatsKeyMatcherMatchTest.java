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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jmock.Mockery;

/**
 * 
 * @author The Stajistics Project
 */
public class StatsKeyMatcherMatchTest extends TestCase {

    private static final Object[][] TEST_DATA = {
        // All
        { StatsKeyMatcher.all(), newKey("a"), true },
        { StatsKeyMatcher.all(), newKey("a.1"), true },
        { StatsKeyMatcher.all(), newKey("a.1.b"), true },

        // None
        { StatsKeyMatcher.none(), newKey("a"), false },
        { StatsKeyMatcher.none(), newKey("a.1"), false },
        { StatsKeyMatcher.none(), newKey("a.1.b"), false },

        // Not
        { StatsKeyMatcher.not(StatsKeyMatcher.all()), newKey("a"), false },
        { StatsKeyMatcher.not(StatsKeyMatcher.all()), newKey("a.1"), false },
        { StatsKeyMatcher.not(StatsKeyMatcher.all()), newKey("a.1.b"), false },

        // Prefix
        { StatsKeyMatcher.prefix("a"), newKey("a"), true },
        { StatsKeyMatcher.prefix("a"), newKey("a.1"), true },
        { StatsKeyMatcher.prefix("a"), newKey("b"), false },

        // Suffix
        { StatsKeyMatcher.suffix("a"), newKey("a"), true },
        { StatsKeyMatcher.suffix("1"), newKey("a.1"), true },
        { StatsKeyMatcher.suffix(".1"), newKey("a.1"), true },
        { StatsKeyMatcher.suffix("a"), newKey("b"), false },
        { StatsKeyMatcher.suffix("b"), newKey("a.b.c"), false },

        // Contains
        { StatsKeyMatcher.contains("a"), newKey("a"), true },
        { StatsKeyMatcher.contains("1"), newKey("a.1"), true },
        { StatsKeyMatcher.contains(".1"), newKey("a.1"), true },
        { StatsKeyMatcher.contains("a"), newKey("b"), false },
        { StatsKeyMatcher.contains("b"), newKey("a.b.c"), true },
    };

    private final StatsKeyMatcher matcher;
    private final StatsKey key;
    private final boolean expectedResult;

    public StatsKeyMatcherMatchTest(final StatsKeyMatcher matcher,
                                    final StatsKey key,
                                    final boolean expectedResult) {
        super(matcher.getClass().getSimpleName() + " matches " + key);
        this.matcher = matcher;
        this.key = key;
        this.expectedResult = expectedResult;
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        for (int i = 0; i < TEST_DATA.length; i++) {
            StatsKeyMatcher matcher = (StatsKeyMatcher)TEST_DATA[i][0];
            StatsKey key = (StatsKey)TEST_DATA[i][1];
            boolean expectedResult = (Boolean)TEST_DATA[i][2];

            suite.addTest(new StatsKeyMatcherMatchTest(matcher, key, expectedResult));    
        }
        return suite;
    }

    @Override
    protected void runTest() throws Throwable {
        assertEquals(expectedResult, matcher.matches(key)); 
    }

    private static StatsKey newKey(final String name) {
        Mockery mockery = new Mockery();
        StatsKey mockKey = mockery.mock(StatsKey.class, name);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, name);
        return mockKey;
    }

}
