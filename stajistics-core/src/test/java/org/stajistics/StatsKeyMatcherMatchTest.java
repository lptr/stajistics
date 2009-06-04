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

import static org.stajistics.StatsKeyMatcher.all;
import static org.stajistics.StatsKeyMatcher.attrNamePrefix;
import static org.stajistics.StatsKeyMatcher.attrValuePrefix;
import static org.stajistics.StatsKeyMatcher.contains;
import static org.stajistics.StatsKeyMatcher.none;
import static org.stajistics.StatsKeyMatcher.not;
import static org.stajistics.StatsKeyMatcher.prefix;
import static org.stajistics.StatsKeyMatcher.suffix;

import java.lang.reflect.Field;

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
        { all(), newKey("a"), true },
        { all(), newKey("a.1"), true },
        { all(), newKey("a.1.b"), true },

        // None
        { none(), newKey("a"), false },
        { none(), newKey("a.1"), false },
        { none(), newKey("a.1.b"), false },

        // Not
        { not(all()), newKey("a"), false },
        { not(all()), newKey("a.1"), false },
        { not(all()), newKey("a.1.b"), false },

        // Prefix
        { prefix("a"), newKey("a"), true },
        { prefix("a"), newKey("a.1"), true },
        { prefix("a"), newKey("b"), false },

        // Attribute name prefix
        { attrNamePrefix("a"), newKey("a", "a", "x"), true },
        { attrNamePrefix("a"), newKey("a", "ab", "x"), true },
        { attrNamePrefix("a"), newKey("a", "ba", "x"), false },

        // Attribute value prefix
        { attrValuePrefix("a"), newKey("a", "x", "a"), true },
        { attrValuePrefix("a"), newKey("a", "x", "ab"), true },
        { attrValuePrefix("a"), newKey("a", "x", "ba"), false },

        // Suffix
        { suffix("a"), newKey("a"), true },
        { suffix("1"), newKey("a.1"), true },
        { suffix(".2"), newKey("a.2"), true },
        { suffix("a"), newKey("b"), false },
        { suffix("b"), newKey("a.b.c"), false },

        // Contains
        { contains("a"), newKey("a"), true },
        { contains("1"), newKey("a.1"), true },
        { contains(".2"), newKey("a.2"), true },
        { contains("a"), newKey("b"), false },
        { contains("b"), newKey("a.b.c"), true },
    };

    private final StatsKeyMatcher matcher;
    private final StatsKey key;
    private final boolean expectedResult;

    public StatsKeyMatcherMatchTest(final StatsKeyMatcher matcher,
                                    final StatsKey key,
                                    final boolean expectedResult) {
        super(buildTestName(matcher, key));
        this.matcher = matcher;
        this.key = key;
        this.expectedResult = expectedResult;
    }

    private static String buildTestName(final StatsKeyMatcher matcher,
                                        final StatsKey key) {
        StringBuilder buf = new StringBuilder(64);
        buf.append(matcher.getClass().getSimpleName());

        StatsKeyMatcher.MatchTarget target = null;

        try {
            Field field = matcher.getClass().getDeclaredField("target");
            boolean oldAccessible = field.isAccessible();
            field.setAccessible(true);
            target = (StatsKeyMatcher.MatchTarget)field.get(matcher);
            field.setAccessible(oldAccessible);

        } catch (Exception e) {}

        if (target != null) {
            buf.append('_');
            buf.append(target);
            buf.append("_matches_");

            switch (target) {
            case KEY_NAME:
                buf.append(key.getName());
                break;
            case ATTR_NAME:
                buf.append(key.getAttributes().keySet().iterator().next());
                break;
            case ATTR_VALUE:
                buf.append(key.getAttributes().values().iterator().next());
                break;
            }
        } else {
            buf.append("_matches_");
            buf.append(key.getName());
        }

        return buf.toString();
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
        return newKey(name, null, null);
    }

    private static StatsKey newKey(final String name, 
                                   final String attrName, 
                                   final String attrVaule) {
        Mockery mockery = new Mockery();
        StatsKey mockKey = mockery.mock(StatsKey.class, name);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, name, attrName, attrVaule);
        return mockKey;
    }
}
