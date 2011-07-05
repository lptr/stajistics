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

import static org.stajistics.StatsKeyMatcher.all;
import static org.stajistics.StatsKeyMatcher.attrNameContains;
import static org.stajistics.StatsKeyMatcher.attrNameEquals;
import static org.stajistics.StatsKeyMatcher.attrNameLength;
import static org.stajistics.StatsKeyMatcher.attrNameMatchesRegEx;
import static org.stajistics.StatsKeyMatcher.attrNamePrefix;
import static org.stajistics.StatsKeyMatcher.attrNameSuffix;
import static org.stajistics.StatsKeyMatcher.attrValueContains;
import static org.stajistics.StatsKeyMatcher.attrValueEquals;
import static org.stajistics.StatsKeyMatcher.attrValueLength;
import static org.stajistics.StatsKeyMatcher.attrValueMatchesRegEx;
import static org.stajistics.StatsKeyMatcher.attrValuePrefix;
import static org.stajistics.StatsKeyMatcher.attrValueSuffix;
import static org.stajistics.StatsKeyMatcher.attributeCount;
import static org.stajistics.StatsKeyMatcher.contains;
import static org.stajistics.StatsKeyMatcher.depth;
import static org.stajistics.StatsKeyMatcher.descendentOf;
import static org.stajistics.StatsKeyMatcher.exactMatch;
import static org.stajistics.StatsKeyMatcher.length;
import static org.stajistics.StatsKeyMatcher.matchesRegEx;
import static org.stajistics.StatsKeyMatcher.nameEquals;
import static org.stajistics.StatsKeyMatcher.none;
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
        { all(), newKey("a"),     true },
        { all(), newKey("a.1"),   true },
        { all(), newKey("a.1.b"), true },

        // None
        { none(), newKey("a"),     false },
        { none(), newKey("a.1"),   false },
        { none(), newKey("a.1.b"), false },

        // Not (can't use all().not() because it has an optimization that bypasses the regular not() behaviour)
        { nameEquals("a").not(), newKey("a"),     false },
        { nameEquals("a.1").not(), newKey("a.1"),   false },
        { nameEquals("a.1.b").not(), newKey("a.1.b"), false },

        // And
        { all().and(all()), newKey("and.a"),   true },
        { all().and(none()), newKey("and.b"),  false },
        { none().and(all()), newKey("and.c"),  false },
        { none().and(none()), newKey("and.d"), false },

        // Or
        { all().or(all()), newKey("or.a"),   true },
        { all().or(none()), newKey("or.b"),  true },
        { none().or(all()), newKey("or.c"),  true },
        { none().or(none()), newKey("or.d"), false },

        // XOr
        { all().xor(all()), newKey("xor.a"),   false },
        { all().xor(none()), newKey("xor.b"),  true },
        { none().xor(all()), newKey("xor.c"),  true },
        { none().xor(none()), newKey("xor.d"), false },

        // Exact match (these tests needs non-mocked keys for key.equals() to work)
        { exactMatch(newRealKey("a")), newRealKey("a"), true },
        { exactMatch(newRealKey("a")), newRealKey("b"), false },

        // Equals
        { nameEquals("a"), newKey("a"),   true },
        { nameEquals("a"), newKey("b"),   false },
        { nameEquals("a"), newKey("ab"),  false },
        { nameEquals("a"), newKey("bc"),  false },
        { nameEquals("a"), newKey("abc"), false },

        // Attribute name equals
        { attrNameEquals("a"), newKey("x", "a", "x"),   true },
        { attrNameEquals("a"), newKey("x", "b", "x"),   false },
        { attrNameEquals("a"), newKey("x", "ab", "x"),  false },
        { attrNameEquals("a"), newKey("x", "bc", "x"),  false },
        { attrNameEquals("a"), newKey("x", "abc", "x"), false },

        // Attribute value equals
        { attrValueEquals("a"), newKey("x", "x", "a"),   true },
        { attrValueEquals("a"), newKey("x", "x", "b"),   false },
        { attrValueEquals("a"), newKey("x", "x", "ab"),  false },
        { attrValueEquals("a"), newKey("x", "x", "bc"),  false },
        { attrValueEquals("a"), newKey("x", "x", "abc"), false },

        // Prefix
        { prefix("a"),  newKey("a"),     true },
        { prefix("a"),  newKey("a.1"),   true },
        { prefix("a."), newKey("a.2"),   true },
        { prefix("a"),  newKey("b"),     false },
        { prefix("b"),  newKey("a.b.c"), false },

        // ChildOf
        { descendentOf("a"), newKey("a"),     false },
        { descendentOf("a"), newKey("b"),     false },
        { descendentOf("a"), newKey("ab"),    false },
        { descendentOf("a"), newKey("a."),    true },
        { descendentOf("a"), newKey("a.b"),   true },
        { descendentOf("a"), newKey("a.b.c"), true },

        // Attribute name prefix
        { attrNamePrefix("a"), newKey("a", "a", "x"),  true },
        { attrNamePrefix("a"), newKey("a", "ab", "x"), true },
        { attrNamePrefix("a"), newKey("a", "ba", "x"), false },

        // Attribute value prefix
        { attrValuePrefix("a"), newKey("a", "x", "a"),  true },
        { attrValuePrefix("a"), newKey("a", "x", "ab"), true },
        { attrValuePrefix("a"), newKey("a", "x", "ba"), false },

        // Suffix
        { suffix("a"),  newKey("a"),     true },
        { suffix("1"),  newKey("a.1"),   true },
        { suffix(".2"), newKey("a.2"),   true },
        { suffix("a"),  newKey("b"),     false },
        { suffix("b"),  newKey("a.b.c"), false },

        // Attribute name suffix
        { attrNameSuffix("a"), newKey("a", "a", "x"),  true },
        { attrNameSuffix("b"), newKey("a", "ab", "x"), true },
        { attrNameSuffix("b"), newKey("a", "ba", "x"), false },

        // Attribute value suffix
        { attrValueSuffix("a"), newKey("a", "x", "a"),  true },
        { attrValueSuffix("b"), newKey("a", "x", "ab"), true },
        { attrValueSuffix("b"), newKey("a", "x", "ba"), false },

        // Contains
        { contains("a"), newKey("a"),     true },
        { contains("1"), newKey("a.1"),   true },
        { contains("a"), newKey("a.2"),   true },
        { contains("a"), newKey("b"),     false },
        { contains("b"), newKey("a.b.c"), true },
        { contains("a"), newKey("b.c.d"), false },

        // Attribute name contains
        { attrNameContains("a"), newKey("x", "a", "x"),     true },
        { attrNameContains("1"), newKey("x", "a.1", "x"),   true },
        { attrNameContains("a"), newKey("x", "a.2", "x"),   true },
        { attrNameContains("a"), newKey("x", "b", "x"),     false },
        { attrNameContains("b"), newKey("x", "a.b.c", "x"), true },
        { attrNameContains("a"), newKey("x", "b.c.d", "x"), false },

        // Attribute value contains
        { attrValueContains("a"), newKey("x", "x", "a"),     true },
        { attrValueContains("1"), newKey("x", "x", "a.1"),   true },
        { attrValueContains("a"), newKey("x", "x", "a.2"),   true },
        { attrValueContains("a"), newKey("x", "x", "b"),     false },
        { attrValueContains("b"), newKey("x", "x", "a.b.c"), true },
        { attrValueContains("a"), newKey("x", "x", "b.c.d"), false },

        // Length
        { length(1), newKey("a"),  true },
        { length(2), newKey("b"),  false },
        { length(1), newKey("ab"), false },

        // Attribute name length
        { attrNameLength(1), newKey("x", "a", "x"),  true },
        { attrNameLength(2), newKey("x", "b", "x"),  false },
        { attrNameLength(1), newKey("x", "ab", "x"), false },

        // Attribute value length
        { attrValueLength(1), newKey("x", "x", "a"),  true },
        { attrValueLength(2), newKey("x", "x", "b"),  false },
        { attrValueLength(1), newKey("x", "x", "ab"), false },

        // Depth
        { depth(1), newKey("a"),     true },
        { depth(2), newKey("a.b"),   true },
        { depth(3), newKey("a.b.c"), true },
        { depth(1), newKey("b.c"),   false },
        { depth(2), newKey("b"),     false },

        // Attribute count
        { attributeCount(0), newKey("a"),           true },
        { attributeCount(1), newKey("b", "k", "v"), true },
        { attributeCount(0), newKey("c", "k", "v"), false },
        { attributeCount(1), newKey("d"),           false },

        // Matches RegEx
        { matchesRegEx("[a][b][c]"),    newKey("abc"), true },
        { matchesRegEx("[b][c][d]{2}"), newKey("bcd"), false },

        // Attribute name matches RegEx
        { attrNameMatchesRegEx("[a][b][c]"),    newKey("x", "abc", "x"), true },
        { attrNameMatchesRegEx("[b][c][d]{2}"), newKey("x", "bcd", "x"), false },

        // Attribute value matches RegEx
        { attrValueMatchesRegEx("[a][b][c]"),    newKey("x", "x", "abc"), true },
        { attrValueMatchesRegEx("[b][c][d]{2}"), newKey("x", "x", "bcd"), false },
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
        TestSuite suite = new TestSuite(StatsKeyMatcherMatchTest.class.getName());
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

        assertEquals(matcher, matcher); // Yeah, not really part of the test, but convenient
    }

    /*
     * Create a non-mock key, so that equals() and hashCode() work
     */
    private static StatsKey newRealKey(final String name) {
        return new SimpleStatsKey(null, name, new Mockery().mock(StatsKeyFactory.class));
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
