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
package org.stajistics.util;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.util.testbinding.TestBinding;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author The Stajistics Project
 */
public class AbstractXMLBindingManagerTest {

    private static final String TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                           "<testBinding><message>%message%</message></testBinding>";

    private AbstractXMLBindingManager<TestBinding> manager;

    @Before
    public void setUp() {
        manager = new AbstractXMLBindingManager<TestBinding>() {
            @Override
            protected Class<TestBinding> getRootElementType() {
                return TestBinding.class;
            }
        };
    }

    private String testXML(final String message) {
        return TEST_XML.replaceAll("%message%", message);
    }

    @Test
    public void testUnmarshalWithString() {
        TestBinding b = manager.unmarshal(testXML("aString"));
        assertEquals(new TestBinding("aString"), b);
    }

    @Test
    public void testUnmarshalWithReader() {
        TestBinding b = manager.unmarshal(new StringReader(testXML("aReader")));
        assertEquals(new TestBinding("aReader"), b);
    }

    @Test
    public void testUnmarshalWithInputStream() throws UnsupportedEncodingException {
        byte[] bytes = testXML("anInputStream").getBytes(XMLBindingManager.DEFAULT_ENCODING);
        TestBinding b = manager.unmarshal(new ByteArrayInputStream(bytes));
        assertEquals(new TestBinding("anInputStream"), b);
    }

    @Test
    public void testUnmarshalWithFile() throws IOException {
        File f = File.createTempFile(TestBinding.class.getSimpleName(),
                                     getClass().getSimpleName());
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(testXML("aFile"));
            fw.close();

            TestBinding b = manager.unmarshal(f);
            assertEquals(new TestBinding("aFile"), b);
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }

    @Test
    public void testMarshalWithString() {
        String xml = manager.marshal(new TestBinding("aString"));
        assertEquals(testXML("aString"), xml);
    }

    @Test
    public void testMarshalWithReader() {
        StringWriter out = new StringWriter();
        manager.marshal(new TestBinding("aReader"), out);
        assertEquals(testXML("aReader"), out.toString());
    }

    @Test
    public void testMarshalWithOutputStream() throws UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manager.marshal(new TestBinding("anOutputStream"), out);
        assertTrue(Arrays.equals(testXML("anOutputStream").getBytes(XMLBindingManager.DEFAULT_ENCODING),
                   out.toByteArray()));
    }

    /*@Test
    public void testMarshalWithFile() throws IOException {
        File f = File.createTempFile(TestBinding.class.getSimpleName(),
                                     getClass().getSimpleName());
        try {
            manager.marshal(new TestBinding("aFile"));
            final String xml = testXML("aFile");

            FileReader fr = new FileReader(f);
            char[] buf = new char[xml.length()];
            fr.read(buf);
            fr.close();

            assertEquals(xml, new String(buf));
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }*/
}
