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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public abstract class AbstractStatsKeyTestCase extends AbstractStajisticsTestCase {

    protected static final String TEST_NAMESPACE = "testNamespace";
    protected static final String TEST_NAME = "testName";
    protected static final String TEST_UNIT = "testUnit";
    protected static final Map<String,Object> TEST_ATTRIBUTES = new HashMap<String,Object>();

    protected StatsKeyFactory mockKeyFactory;

    @Before
    public void setUp() {
        mockKeyFactory = mockery.mock(StatsKeyFactory.class);
    }

    protected abstract StatsKey createStatsKey(String namespace,
                                               String name,
                                               StatsKeyFactory keyFactory,
                                               Map<String,Object> attributes);

    protected StatsKey createStatsKey(final String name) {
        return createStatsKey(TEST_NAMESPACE,
                              name,
                              mockKeyFactory,
                              TEST_ATTRIBUTES);
    }

    protected StatsKey createStatsKey(final String name,
                                      final String attrName,
                                      final String attrValue) {
        return createStatsKey(TEST_NAMESPACE,
                              name,
                              mockKeyFactory,
                              Collections.<String,Object>singletonMap(attrName, attrValue));
    }

    @Test
    public void testConstructWithNullName() {
        StatsKey key = createStatsKey(null);
        assertEquals("<null>", key.getName());
    }

    @Test
    public void testConstructWithNullKeyFactory() {
        StatsKey key = createStatsKey(TEST_NAMESPACE, TEST_NAME, null, Collections.<String,Object>emptyMap());
        assertSame(NullStatsKeyBuilder.getInstance(), key.buildCopy());
    }

    @Test
    public void testConstructWithNullAttributes() {
        try {
            createStatsKey(TEST_NAMESPACE, TEST_NAME, mockKeyFactory, null);

            fail("Allowed construction with null attributes");

        } catch (NullPointerException npe) {
            assertEquals("attributes", npe.getMessage());
        }
    }

    @Test
    public void testGetAttribute() {
        final Map<String,Object> testAttributes = new HashMap<String,Object>();
        testAttributes.put("test1", Boolean.TRUE);

        StatsKey key = createStatsKey(TEST_NAMESPACE, TEST_NAME, null, testAttributes);
        assertEquals(Boolean.TRUE, key.getAttribute("test1"));
        assertNull(key.getAttribute("test2"));
    }

    @Test
    public void testBuildCopy() {

        final StatsKey key = createStatsKey(TEST_NAME);
        final StatsKeyBuilder builder = mockery.mock(StatsKeyBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockKeyFactory).createKeyBuilder(key); will(returnValue(builder));
        }});

        assertSame(builder, key.buildCopy());
    }

    @Test
    public void testGetHierarchyDepth() {
        assertEquals(1, createStatsKey("a").getHierarchyDepth());
        assertEquals(2, createStatsKey("a.b").getHierarchyDepth());
        assertEquals(3, createStatsKey("a.b.c").getHierarchyDepth());
        assertEquals(4, createStatsKey("a.b.c.d").getHierarchyDepth());
        assertEquals(5, createStatsKey("a.b.c.d.e").getHierarchyDepth());
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
        StatsKey key1 = createStatsKey(TEST_NAMESPACE,
                                       TEST_NAME,
                                       mockKeyFactory,
                                       TEST_ATTRIBUTES);
        StatsKey key2 = createStatsKey(TEST_NAMESPACE,
                                       TEST_NAME,
                                       mockKeyFactory,
                                       Collections.<String,Object>singletonMap("test", "test"));
        assertFalse(key1.equals(key2));
    }

    @Test
    public void testHashCodeNotZero() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertFalse(0 == key.hashCode());
    }

    @Test
    public void testEqualsWithDifferentType() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertFalse(key.equals("hello"));
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

    @Test
    public void testToStringIsNotDefault() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertFalse(key.toString().startsWith(key.getClass().getName() + "@"));
    }

    @Test
    public void testToStringContainsName() {
        StatsKey key = createStatsKey(TEST_NAME);
        assertTrue(key.toString().contains("name=" + key.getName()));
    }

    @Test
    public void testToStringContainsAttributes() {
        StatsKey key = createStatsKey(TEST_NAMESPACE,
                                      TEST_NAME,
                                      mockKeyFactory,
                                      Collections.<String,Object>singletonMap("name", "value"));
        assertTrue(key.toString().contains("name=value"));
    }

    @Test
    public void testCompareToWithNames() {
        List<StatsKey> keyList =
            Arrays.asList(createStatsKey("b.2"),
                          createStatsKey("a.1"),
                          createStatsKey("a.3"),
                          createStatsKey("a.2"),
                          createStatsKey("b.1"),
                          createStatsKey("b.3"));

        Collections.sort(keyList);

        Iterator<StatsKey> itr = keyList.iterator();
        assertEquals("a.1", itr.next().getName());
        assertEquals("a.2", itr.next().getName());
        assertEquals("a.3", itr.next().getName());
        assertEquals("b.1", itr.next().getName());
        assertEquals("b.2", itr.next().getName());
        assertEquals("b.3", itr.next().getName());
    }

    @Test
    public void testCompareToWithAttributes() {
        List<StatsKey> keyList =
            Arrays.asList(createStatsKey("b.2"),
                          createStatsKey("a.1"),
                          createStatsKey("c", "b", "1"),
                          createStatsKey("a.3"),
                          createStatsKey("a.2"),
                          createStatsKey("c", "a", "1"),
                          createStatsKey("b.1"),
                          createStatsKey("b.3"),
                          createStatsKey("c", "a", "2"),
                          createStatsKey("c", "b", "2"));

        Collections.sort(keyList);

        Iterator<StatsKey> itr = keyList.iterator();
        assertEquals("a.1", itr.next().getName());
        assertEquals("a.2", itr.next().getName());
        assertEquals("a.3", itr.next().getName());
        assertEquals("b.1", itr.next().getName());
        assertEquals("b.2", itr.next().getName());
        assertEquals("b.3", itr.next().getName());

        StatsKey k = itr.next();
        assertEquals("c", k.getName());
        assertEquals("1", k.getAttribute("a"));

        k = itr.next();
        assertEquals("c", k.getName());
        assertEquals("2", k.getAttribute("a"));

        k = itr.next();
        assertEquals("c", k.getName());
        assertEquals("1", k.getAttribute("b"));

        k = itr.next();
        assertEquals("c", k.getName());
        assertEquals("2", k.getAttribute("b"));
    }
}
