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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class NullStatsKeyBuilderTest extends AbstractStajisticsTestCase {

    private final StatsKeyBuilder nkb = NullStatsKeyBuilder.getInstance();

    @Test
    public void testInitialNewKey() {
        assertEquals(NullStatsKey.getInstance(),
                     nkb.newKey());
    }

    @Test
    public void testWithAttribute() {
        assertEquals(nkb, nkb.withAttribute("name", "value"));
        assertEquals(nkb, nkb.withAttribute("name", true));
        assertEquals(nkb, nkb.withAttribute("name", 1));
        assertEquals(nkb, nkb.withAttribute("name", 1L));
        assertEquals(NullStatsKey.getInstance(),
                     nkb.newKey());
    }

    @Test
    public void testWithoutAttribute() {
        assertEquals(nkb, nkb.withAttribute("name", "value"));
        assertEquals(NullStatsKey.getInstance(),
                     nkb.newKey());
        assertEquals(nkb, nkb.withoutAttribute("name"));
        assertEquals(NullStatsKey.getInstance(),
                     nkb.newKey());
    }

    @Test
    public void testWithNameSuffix() {
        assertEquals(nkb, nkb.withNameSuffix("suffix"));
        assertEquals(NullStatsKey.getInstance(),
                     nkb.newKey());
    }
}
