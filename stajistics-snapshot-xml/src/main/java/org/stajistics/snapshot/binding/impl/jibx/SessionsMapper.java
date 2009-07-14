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
import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;
import org.stajistics.StatsKey;

/**
 * 
 * @author The Stajistics Project
 */
public class SessionsMapper implements IMarshaller, IUnmarshaller, IAliasable {

    private static final int DEFAULT_SIZE = 64;

    private static final String ATTR_SIZE = "size";
    private static final String ELEMENT_SESSION = "session";

    private final String uri;
    private final int index;
    private final String name;

    public SessionsMapper() {
        uri = null;
        index = 0;
        name = "sessions";
    }

    public SessionsMapper(final String uri, 
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
        Map<String,SessionImpl> map = (Map<String,SessionImpl>)obj;

        ctx.startTagAttributes(index, name);

        if (!map.isEmpty()) {
            // size attribute
            ctx.attribute(index, ATTR_SIZE, map.size());
            ctx.closeStartContent();

            for (Map.Entry<String,SessionImpl> entry : map.entrySet()) {
            	((IMarshallable)entry.getValue()).marshal(ctx);
            }
        }

        ctx.endTag(index, name);
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
        Map<StatsKey,SessionImpl> map = (Map<StatsKey,SessionImpl>)obj;
        if (map == null) {
            map = new HashMap<StatsKey,SessionImpl>(size);
        }

        // process all entries present in document
        ctx.parsePastStartTag(uri, name);
        while (ctx.isAt(uri, ELEMENT_SESSION)) {
            SessionImpl session = (SessionImpl)ctx.unmarshalElement();
            map.put(session.getKey(), session);
        }
        ctx.parsePastEndTag(uri, name);

        return map;
    }
    
}
