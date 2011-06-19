package org.stajistics.util;

import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;

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
public class FastPutsTableMapTest extends AbstractStajisticsTestCase {

    @Test
    public void testConstruct() {
        FastPutsTableMap m = new FastPutsTableMap();
        assertContents(Collections.emptyMap(), m);
    }

    @Test
    public void testConstructWithMap() {

        Map m1 = new HashMap();
        m1.put("one", 1);
        m1.put("two", 2);

        FastPutsTableMap m2 = new FastPutsTableMap(m1);

        assertContents(m1, m2);
    }

    @Test
    public void testSizeWithDuplicates() {
        FastPutsTableMap m = new FastPutsTableMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        assertEquals(2, m.size());
    }

    @Test
    public void testSizeAfterPuts() {
        FastPutsTableMap m = new FastPutsTableMap();
        m.put("one", 1);
        assertEquals(1, m.size());
        m.put("two", 2);
        assertEquals(2, m.size());
        m.put("three", 3);
        assertEquals(3, m.size());
    }

    @Test
    public void testSizeAfterRemove() {
        FastPutsTableMap m = new FastPutsTableMap();
        m.put("one", 1);
        m.put("two", 2);
        m.remove("one");

        assertEquals(1, m.size());
    }

    @Test
    public void testGetWithDuplicates() {
        FastPutsTableMap m = new FastPutsTableMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        assertEquals(-1, m.get("one"));
        assertEquals(2, m.get("two"));
    }

    @Test
    public void testCompact() {
        FastPutsTableMap m = new FastPutsTableMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        assertEquals(3, m.entries.size());

        m.compact();

        assertEquals(2, m.entries.size());

        Map expectedContents = new HashMap();
        expectedContents.put("one", -1);
        expectedContents.put("two", 2);

        assertContents(expectedContents, m);
    }

    @Test
    public void testRemove() {
        FastPutsTableMap m = new FastPutsTableMap();
        m.put("one", 1);
        m.put("one", -1);
        m.put("two", 2);

        m.remove("one");

        assertEquals(1, m.entries.size()); // Ensure duplicates removed
        assertContents(Collections.singletonMap("two", 2), m);
    }
    
    private void assertContents(final Map expectedContents, final FastPutsTableMap fastPutsTableMap) {
        assertEquals(expectedContents.isEmpty(), fastPutsTableMap.isEmpty());
        assertEquals(expectedContents.size(), fastPutsTableMap.size());

        assertEquals(expectedContents.isEmpty(), fastPutsTableMap.entrySet().isEmpty());
        assertEquals(expectedContents.size(), fastPutsTableMap.entrySet().size());

        assertEquals(expectedContents.isEmpty(), fastPutsTableMap.keySet().isEmpty());
        assertEquals(expectedContents.size(), fastPutsTableMap.keySet().size());

        assertEquals(expectedContents.isEmpty(), fastPutsTableMap.values().isEmpty());
        assertEquals(expectedContents.size(), fastPutsTableMap.values().size());

        for (Map.Entry e : (Set<Map.Entry>) expectedContents.entrySet()) {
            assertEquals(e.getValue(), fastPutsTableMap.get(e.getKey()));
        }
    }
}
