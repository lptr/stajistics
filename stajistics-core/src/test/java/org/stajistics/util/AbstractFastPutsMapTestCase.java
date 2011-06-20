package org.stajistics.util;

import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StajisticsAssert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * 
 *
 * @author The Stajistics Project
 */
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class AbstractFastPutsMapTestCase extends AbstractStajisticsTestCase {

    protected abstract Map createFastPutsMap();

    protected abstract Map createFastPutsMap(Map map);

    protected abstract void compact(Map map);

    @Test
    public void testConstruct() {
        Map m = createFastPutsMap();
        assertContents(Collections.emptyMap(), m);
    }

    @Test
    public void testConstructWithMap() {

        Map m1 = new HashMap();
        m1.put("one", 1);
        m1.put("two", 2);

        Map m2 = createFastPutsMap(m1);

        assertContents(m1, m2);
    }

    @Test
    public void testSizeWithDuplicates() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        assertEquals(2, m.size());
    }

    @Test
    public void testSizeAfterPuts() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        assertEquals(1, m.size());
        m.put("two", 2);
        assertEquals(2, m.size());
        m.put("three", 3);
        assertEquals(3, m.size());
    }

    @Test
    public void testSizeAfterRemove() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("two", 2);
        m.remove("one");

        assertEquals(1, m.size());
    }

    @Test
    public void testGetWithDuplicates() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        assertEquals(-1, m.get("one"));
        assertEquals(2, m.get("two"));
    }

    @Test
    public void testCompact() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        compact(m);

        Map expectedContents = new HashMap();
        expectedContents.put("one", -1);
        expectedContents.put("two", 2);

        assertContents(expectedContents, m);
    }

    @Test
    public void testRemove() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        m.remove("one");

        // Ensure duplicates removed
        int s = m.size();
        compact(m);
        assertEquals(s, m.size());

        assertContents(Collections.singletonMap("two", 2), m);
    }

    @Test
    public void testClear() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("two", 2);

        m.clear();

        assertContents(Collections.emptyMap(), m);
    }

    @Test
    public void testSerializableDeserializable() {
        Map m = createFastPutsMap();
        m.put("one", 1);
        m.put("two", 2);
        m.put("three", 3);

        StajisticsAssert.assertSerializable(m);
    }

    private void assertContents(final Map expectedContents, final Map fastPutsMap) {
        assertEquals(expectedContents.isEmpty(), fastPutsMap.isEmpty());
        assertEquals(expectedContents.size(), fastPutsMap.size());

        assertEquals(expectedContents.isEmpty(), fastPutsMap.entrySet().isEmpty());
        assertEquals(expectedContents.size(), fastPutsMap.entrySet().size());

        assertEquals(expectedContents.isEmpty(), fastPutsMap.keySet().isEmpty());
        assertEquals(expectedContents.size(), fastPutsMap.keySet().size());

        assertEquals(expectedContents.isEmpty(), fastPutsMap.values().isEmpty());
        assertEquals(expectedContents.size(), fastPutsMap.values().size());

        for (Map.Entry e : (Set<Map.Entry>) expectedContents.entrySet()) {
            assertEquals(e.getValue(), fastPutsMap.get(e.getKey()));
        }
    }
}
