package org.stajistics.util;

import java.util.Map;

/**
 * @author The Stajistics Project
 */
public class FastPutsArrayMapTest extends AbstractFastPutsMapTestCase {

    @Override
    protected Map createFastPutsMap() {
        return new FastPutsArrayMap();
    }

    @Override
    protected Map createFastPutsMap(Map map) {
        return new FastPutsArrayMap(map);
    }

    @Override
    protected void compact(Map map) {
        ((FastPutsArrayMap)map).compact();
    }
}
