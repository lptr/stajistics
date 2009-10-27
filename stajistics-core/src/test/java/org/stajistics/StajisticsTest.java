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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class StajisticsTest {

    @Before
    public void setUp() throws IOException {
        Stajistics.loadProperties(Stajistics.PROPS_FILE);
    }

    @Test
    public void testLoadProperties() throws IOException {
        Stajistics.loadProperties("certainly.does.not.exist.at.all.for.sure.properties");
        assertEquals("", Stajistics.getName());
        assertEquals("", Stajistics.getVersion());
    }

    @Test
    public void testGetName() {
        assertNotNull(Stajistics.getName());
        assertTrue(Stajistics.getName().length() > 0);
    }

    @Test
    public void testGetVersion() {
        assertNotNull(Stajistics.getVersion());
        assertTrue(Stajistics.getVersion().length() > 0);
    }

}
