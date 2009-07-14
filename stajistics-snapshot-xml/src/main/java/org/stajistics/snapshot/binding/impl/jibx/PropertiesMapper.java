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
package org.stajistics.snapshot.binding.impl.jibx;

import java.util.HashMap;
import java.util.Map;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class PropertiesMapper implements IMarshaller, IUnmarshaller, IAliasable {

    private static final int DEFAULT_SIZE = 32;

    private static final String ATTR_SIZE = "size";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ELEMENT_NAME = "name";
    private static final String ELEMENT_VALUE = "value";

    private final String uri;
    private final int index;
    private final String name;

    public PropertiesMapper() {
        uri = null;
        index = 0;
        name = "properties";
    }

    public PropertiesMapper(final String uri, 
                            final int index, 
                            final String name) {
        this.uri = uri;
        this.index = index;
        this.name = name;
    }

    @Override
    public boolean isExtension(String mapname) {
        return false;
    }

    @Override
    public void marshal(final Object obj, final IMarshallingContext ictx) throws JiBXException {

        if (!(obj instanceof Map<?,?>)) {
            throw new JiBXException("Invalid object type for marshaller: " + obj.getClass().getName());
        }
        if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller: " + ictx.getClass().getName());
        }

        MarshallingContext ctx = (MarshallingContext)ictx;

        @SuppressWarnings("unchecked")
        Map<String,String> map = (Map<String,String>)obj;
        if (!map.isEmpty()) {

            ctx.startTagAttributes(index, name);

            // size attribute
            ctx.attribute(index, ATTR_SIZE, map.size());
            ctx.closeStartContent();

            for (Map.Entry<String,String> entry : map.entrySet()) {
                ctx.startTagAttributes(index, ELEMENT_PROPERTY);

                // name
                ctx.startTagAttributes(index, ELEMENT_NAME);
                ctx.content(entry.getKey());
                ctx.endTag(index, ELEMENT_NAME);

                // value
                ctx.startTag(index, ELEMENT_VALUE);
                ctx.content(entry.getValue());
                ctx.endTag(index, ELEMENT_VALUE);

                ctx.endTag(index, ELEMENT_PROPERTY);
            }

            ctx.endTag(index, name);
        }
    }

    @Override
    public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException {
        return ctx.isAt(uri, name);
    }

    @Override
    public Object unmarshal(final Object obj, final IUnmarshallingContext ictx)
        throws JiBXException {

        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }

        int size = ctx.attributeInt(uri, ATTR_SIZE, DEFAULT_SIZE);

        @SuppressWarnings("unchecked")
        Map<String,String> map = (Map<String,String>)obj;
        if (map == null) {
            map = new HashMap<String,String>(size);
        }

        // process all entries present in document
        ctx.parsePastStartTag(uri, name);
        while (ctx.isAt(uri, ELEMENT_PROPERTY)) {
            ctx.parsePastStartTag(uri, ELEMENT_PROPERTY);

            // name
            ctx.parsePastStartTag(uri, ELEMENT_NAME);
            String key = ctx.accumulateText().trim();
            ctx.parsePastEndTag(uri, ELEMENT_NAME);

            // value
            ctx.parsePastStartTag(uri, ELEMENT_VALUE);
            String value = ctx.accumulateText();
            if (value != null) {
                value = value.trim();
            }
            map.put(key, value);
            ctx.parsePastEndTag(uri, ELEMENT_VALUE);

            ctx.parsePastEndTag(uri, ELEMENT_PROPERTY);
        }
        ctx.parsePastEndTag(uri, name);

        return map;
    }
    
}
