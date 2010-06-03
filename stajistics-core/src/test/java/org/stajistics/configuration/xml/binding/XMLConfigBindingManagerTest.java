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
package org.stajistics.configuration.xml.binding;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author The Stajistics Project
 */
public class XMLConfigBindingManagerTest extends AbstractStajisticsTestCase {

    private static final String TEST_CONFIG_XML_FILE = "org/stajistics/configuration/xml/testConfig.xml";

    private XMLConfigBindingManager manager;

    @Before
    public void setUp() {
        manager = new XMLConfigBindingManager();
    }

    @Test
    public void testUnmarshalGetProperties() {
        InputStream in = getClass().getResourceAsStream(TEST_CONFIG_XML_FILE);
        XMLConfigDocument doc = manager.unmarshal(in);

        List<XMLProperty> props = doc.getProperties();
        assertEquals("name1", props.get(0).getName());
        assertEquals("value1", props.get(0).getValue());
        assertEquals("name2", props.get(1).getName());
        assertEquals("value2", props.get(1).getValue());
    }
}
