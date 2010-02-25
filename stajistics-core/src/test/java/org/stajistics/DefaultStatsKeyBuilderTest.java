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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.stajistics.TestUtil.buildStatsKeyExpectations;

import org.jmock.Expectations;
import org.jmock.Mockery;
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
public class DefaultStatsKeyBuilderTest {

    private Mockery mockery;
    private StatsKey mockKey;
    private StatsKeyFactory mockKeyFactory;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
        mockKeyFactory = new DefaultStatsKeyFactory(); // TODO: actually mock this
    }

    private void buildStatsKeyBuildCopyExpectations() {
        mockery.checking(new Expectations() {{
            ignoring(mockKey).buildCopy(); will(returnValue(new DefaultStatsKeyBuilder(mockKey, mockKeyFactory)));
        }});
    }

    @Test
    public void testConstructWithNullName() {
        try {
            new DefaultStatsKeyBuilder((String)null, mockKeyFactory);

        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNameAndNullKeyFactory() {
        try {
            new DefaultStatsKeyBuilder("test", null);

        } catch (NullPointerException npe) {
            assertEquals("keyFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullTemplate() {
        try {
            new DefaultStatsKeyBuilder((StatsKey)null, mockKeyFactory);

        } catch (NullPointerException npe) {
            assertEquals("template", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithTemplateAndNullKeyFactory() {
        try {
            new DefaultStatsKeyBuilder(mockKey, null);

        } catch (NullPointerException npe) {
            assertEquals("keyFactory", npe.getMessage());
        }
    }

    @Test
    public void testCopyKeyNotNull() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .newKey();

        assertTrue(key != null);
    }

    @Test
    public void testCopyKeyWithNullNameSuffix() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        try {
            mockKey.buildCopy().withNameSuffix(null);
            fail("Allowed null name suffix");

        } catch (NullPointerException npe) {
            assertEquals("nameSuffix", npe.getMessage());
        }
    }

    @Test
    public void testCopyKeyWithEmptyNameSuffix() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withNameSuffix("")
                              .newKey();

        assertEquals("test", key.getName());
    }

    @Test
    public void testCopyKeyWithNameSuffix1() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withNameSuffix("suffix")
                              .newKey();

        assertEquals("test.suffix", key.getName());
    }

    @Test
    public void testCopyKeyWithNameSuffix2() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withNameSuffix("suffix1")
                              .withNameSuffix("suffix2")
                              .newKey();

        assertEquals("test.suffix1.suffix2", key.getName());
    }

    @Test
    public void testCopyKeyWithAttribute1() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("test", "value")
                              .newKey();

        assertEquals(1, key.getAttributeCount());
        assertEquals(1, key.getAttributes().size());
        assertEquals("value", key.getAttribute("test"));
        assertEquals("value", key.getAttributes().get("test"));
    }

    @Test
    public void testCopyKeyWithAttribute2() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("test", "value")
                              .newKey();

        assertEquals(2, key.getAttributeCount());
        assertEquals(2, key.getAttributes().size());
        assertEquals("attribute", key.getAttribute("existing"));
        assertEquals("value", key.getAttribute("test"));
        assertEquals("attribute", key.getAttributes().get("existing"));
        assertEquals("value", key.getAttributes().get("test"));
    }

    @Test
    public void testCopyKeyWithTwoAttributes1() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("test1", "value1")
                              .withAttribute("test2", "value2")
                              .newKey();

        assertEquals(2, key.getAttributeCount());
        assertEquals(2, key.getAttributes().size());
        assertEquals("value1", key.getAttribute("test1"));
        assertEquals("value2", key.getAttribute("test2"));
        assertEquals("value1", key.getAttributes().get("test1"));
        assertEquals("value2", key.getAttributes().get("test2"));
    }

    @Test
    public void testCopyKeyWithTwoAttributes2() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("test1", "value1")
                              .withAttribute("test2", "value2")
                              .newKey();

        assertEquals(3, key.getAttributeCount());
        assertEquals(3, key.getAttributes().size());
        assertEquals("attribute", key.getAttribute("existing"));
        assertEquals("value1", key.getAttribute("test1"));
        assertEquals("value2", key.getAttribute("test2"));
        assertEquals("attribute", key.getAttributes().get("existing"));
        assertEquals("value1", key.getAttributes().get("test1"));
        assertEquals("value2", key.getAttributes().get("test2"));
    }

    @Test
    public void testCopyKeyWithThreeAttributes1() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("test1", "value1")
                              .withAttribute("test2", "value2")
                              .withAttribute("test3", "value3")
                              .newKey();

        assertEquals(3, key.getAttributeCount());
        assertEquals(3, key.getAttributes().size());
        assertEquals("value1", key.getAttribute("test1"));
        assertEquals("value2", key.getAttribute("test2"));
        assertEquals("value3", key.getAttribute("test3"));
        assertEquals("value1", key.getAttributes().get("test1"));
        assertEquals("value2", key.getAttributes().get("test2"));
        assertEquals("value3", key.getAttributes().get("test3"));
    }

    @Test
    public void testCopyKeyWithThreeAttributes2() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("test1", "value1")
                              .withAttribute("test2", "value2")
                              .withAttribute("test3", "value3")
                              .newKey();

        assertEquals(4, key.getAttributeCount());
        assertEquals(4, key.getAttributes().size());
        assertEquals("attribute", key.getAttribute("existing"));
        assertEquals("value1", key.getAttribute("test1"));
        assertEquals("value2", key.getAttribute("test2"));
        assertEquals("value3", key.getAttribute("test3"));
        assertEquals("attribute", key.getAttributes().get("existing"));
        assertEquals("value1", key.getAttributes().get("test1"));
        assertEquals("value2", key.getAttributes().get("test2"));
        assertEquals("value3", key.getAttributes().get("test3"));
    }

    @Test
    public void testWithoutAttribute1() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withoutAttribute("existing")
                              .newKey();

        assertEquals(0, key.getAttributeCount());
        assertTrue(key.getAttributes().isEmpty());
    }

    @Test
    public void testWithoutAttribute2() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withoutAttribute("existing")
                              .withAttribute("new", "thing")
                              .newKey();

        assertEquals(1, key.getAttributeCount());
        assertEquals(1, key.getAttributes().size());
        assertNull(key.getAttribute("existing"));
        assertEquals("thing", key.getAttribute("new"));
    }

    @Test
    public void testWithoutAttribute3() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("new", "thing")
                              .withoutAttribute("existing")
                              .newKey();

        assertEquals(1, key.getAttributeCount());
        assertEquals(1, key.getAttributes().size());
        assertNull(key.getAttribute("existing"));
        assertEquals("thing", key.getAttribute("new"));
    }

    @Test
    public void testWithoutAttributeWithNullName() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .withAttribute("new", "thing")
                              .withoutAttribute(null)
                              .newKey();

        assertEquals(1, key.getAttributeCount());
        assertEquals(1, key.getAttributes().size());
        assertEquals("thing", key.getAttribute("new"));
    }

    @Test
    public void testPutAttributeWithNullName() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        try {
            mockKey.buildCopy()
                   .withAttribute(null, "value");
            fail("Allowed withAttribute with null name");

        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
    }

    @Test
    public void testPutAttributeWithInvalidName() {

        String[] invalidNames = { "*",   "?",   ",",   "=",   ":",
                                  "a*",  "a?",  "a,",  "a=",  "a:",
                                  "*a",  "?a",  ",a",  "=a",  ":a",
                                  "*a*", "?a?", ",a,", "=a=", ":a:",
                                  "a*a", "a?a", "a,a", "a=a", "a:a" };

        for (String invalidName : invalidNames) {
            setUp();

            buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
            buildStatsKeyBuildCopyExpectations();

            try {
                mockKey.buildCopy()
                       .withAttribute(invalidName, "value");
                fail("Allowed withAttribute with invalid name: '" + invalidName + "'");

            } catch (IllegalArgumentException iae) {
                // expected
            }
        }
    }

    @Test
    public void testPutAttributeWithNullStringValue() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        try {
            mockKey.buildCopy()
                   .withAttribute("name", (String)null);
            fail("Allowed withAttribute with null String value");

        } catch (NullPointerException npe) {
            assertEquals("value for name: name", npe.getMessage());
        }
    }

    @Test
    public void testPutAttributeWithNullBooleanValue() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        try {
            mockKey.buildCopy()
                   .withAttribute("name", (Boolean)null);
            fail("Allowed withAttribute with null Boolean value");

        } catch (NullPointerException npe) {
            assertEquals("value for name: name", npe.getMessage());
        }
    }

    @Test
    public void testPutAttributeWithNullLongValue() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        try {
            mockKey.buildCopy()
                   .withAttribute("name", (Long)null);
            fail("Allowed withAttribute with null Long value");

        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testPutAttributeWithNullIntegerValue() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        try {
            mockKey.buildCopy()
                   .withAttribute("name", (Integer)null);
            fail("Allowed withAttribute with null Integer value");

        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testWithAttributeReturnsThis() {
        buildStatsKeyExpectations(mockery, mockKey, "test", "existing", "attribute");
        buildStatsKeyBuildCopyExpectations();

        StatsKeyBuilder kb = mockKey.buildCopy();

        assertSame(kb, kb.withAttribute("string", "string"));
        assertSame(kb, kb.withAttribute("int", 0));
        assertSame(kb, kb.withAttribute("boolean", false));
        assertSame(kb, kb.withAttribute("long", 0L));
    }
}
