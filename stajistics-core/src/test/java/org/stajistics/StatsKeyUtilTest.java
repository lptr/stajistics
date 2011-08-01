package org.stajistics;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsKeyUtilTest extends AbstractStajisticsTestCase {

    @Test
    public void testParentKeyName() {
        assertEquals("a", StatsKeyUtil.parentKeyName("a.b"));
        assertEquals("a.b", StatsKeyUtil.parentKeyName("a.b.c"));
        assertEquals("aa", StatsKeyUtil.parentKeyName("aa.bb"));
        assertEquals("aa.bb", StatsKeyUtil.parentKeyName("aa.bb.cc"));
    }

    @Test
    public void testKeyNameHierarchyAscendingWithNoHierarchy() {
        List<String> h = StatsKeyUtil.keyNameHierarchy("a", true);

        assertEquals(1, h.size());
        assertEquals("a", h.get(0));
    }

    @Test
    public void testKeyNameHierarchyAscending() {
        List<String> h = StatsKeyUtil.keyNameHierarchy("a.b.c.d", true);

        assertEquals(4, h.size());
        assertEquals("a", h.get(0));
        assertEquals("a.b", h.get(1));
        assertEquals("a.b.c", h.get(2));
        assertEquals("a.b.c.d", h.get(3));
    }

    @Test
    public void testKeyNameHierarchyDescendingWithNoHierarchy() {
        List<String> h = StatsKeyUtil.keyNameHierarchy("a", false);

        assertEquals(1, h.size());
        assertEquals("a", h.get(0));
    }

    @Test
    public void testKeyNameHierarchyDescending() {
        List<String> h = StatsKeyUtil.keyNameHierarchy("a.b.c.d", false);

        assertEquals(4, h.size());
        assertEquals("a.b.c.d", h.get(0));
        assertEquals("a.b.c", h.get(1));
        assertEquals("a.b", h.get(2));
        assertEquals("a", h.get(3));
    }

}
