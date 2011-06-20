package org.stajistics.util;

import java.util.Map;

/**
 * @author The Stajistics Project
 */
public class FastPutsLinkedMapTest extends AbstractFastPutsMapTestCase {

    @Override
    protected Map createFastPutsMap() {
        return new FastPutsLinkedMap();
    }

    @Override
    protected Map createFastPutsMap(Map map) {
        return new FastPutsLinkedMap(map);
    }

    @Override
    protected void compact(Map map) {
        ((FastPutsLinkedMap)map).compact();
    }
}
