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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsKeyFactoryTest {

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
        try {
            keyFactory.createKey(null);
            fail("Allowed createKey with null name");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testCreateKeyBuilder() {
        assertNotNull(keyFactory.createKey("test"));
    }

    @Test
    public void testCreateKeyBuilderWithNullName() {
        try {
            keyFactory.createKeyBuilder((String)null);
            fail("Allowed createKeyBuilder with null name");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testCreateKeyBuilderWithTemplate() {
        assertNotNull(keyFactory.createKeyBuilder(keyFactory.createKey("test")));
    }

    @Test
    public void testCreateKeyBuilderWithNullTemplate() {
        try {
            keyFactory.createKeyBuilder((StatsKey)null);
            fail("Allowed createKeyBuilder with null template");
        } catch (NullPointerException npe) {
            // expected
        }
    }
    
}
