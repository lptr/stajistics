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
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsKeyFactoryTest extends AbstractStajisticsTestCase {

    // TODO: more

    private StatsKeyFactory keyFactory;

    @Before
    public void setUp() {
        keyFactory = new DefaultStatsKeyFactory();
    }

    @Test
    public void testCreateKey() {
        assertNotNull(keyFactory.createKey("test"));
    }

    @Test
    public void testCreateKeyWithNullName() {
        StatsKey key = keyFactory.createKey(null);
        assertEquals("<null>", key.getName());
    }

    @Test
    public void testCreateKeyBuilder() {
        assertNotNull(keyFactory.createKey("test"));
    }

    @Test
    public void testCreateKeyBuilderWithNullName() {
        StatsKeyBuilder builder = keyFactory.createKeyBuilder((String)null);
        StatsKey key = builder.newKey();
        assertEquals("<null>", key.getName());
    }

    @Test
    public void testCreateKeyBuilderWithTemplate() {
        assertNotNull(keyFactory.createKeyBuilder(keyFactory.createKey("test")));
    }

    @Test
    public void testCreateKeyBuilderWithNullTemplate() {
        StatsKeyBuilder builder = keyFactory.createKeyBuilder((StatsKey)null);
        StatsKey key = builder.newKey();
        assertEquals("<null>", key.getName());
    }

}
