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
import static org.stajistics.TestUtil.buildStatsKeyExpectations;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class DefaultStatsKeyBuilderTest {

    private Mockery mockery;
    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
    }

    private void buildStatsKeyBuildCopyExpectations() {
        mockery.checking(new Expectations() {{
            ignoring(mockKey).buildCopy(); will(returnValue(new DefaultStatsKeyBuilder(mockKey)));
        }});
    }

    @Test
    public void testCopyKeyNotNull() {
        buildStatsKeyExpectations(mockery, mockKey, "test");
        buildStatsKeyBuildCopyExpectations();

        StatsKey key = mockKey.buildCopy()
                              .newKey();

        assertTrue(key != null);

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
    }
}
