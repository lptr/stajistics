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
package org.stajistics.snapshot.binding.impl.jibx.mapper;

import java.util.HashMap;
import java.util.Map;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.DefaultStatsKey;
import org.stajistics.StatsKey;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class StatsKeyMapper extends AbstractSimpleTypeMapper {

    private static final Logger logger = LoggerFactory.getLogger(StatsKeyMapper.class);

    private static final String ELEMENT_ATTRIBUTES = "attributes";
    private static final String ELEMENT_ATTRIBUTE = "attribute";

    public StatsKeyMapper() {
        this(null, 0, "key");
    }

    public StatsKeyMapper(final String uri, 
                          final int index, 
                          final String name) {
        super(uri, index, name);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isExtension(String mapname) {
        return false;
    }

    @Override
    public void marshal(final Object obj, final IMarshallingContext ictx) throws JiBXException {

        if (!(obj instanceof StatsKey)) {
            throw new JiBXException("Invalid object type for marshaller: " + obj.getClass().getName());
        }
        if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller: " + ictx.getClass().getName());
        }

        MarshallingContext ctx = (MarshallingContext)ictx;

        StatsKey key = (StatsKey)obj;

        ctx.startTagAttributes(index, name);

        ctx.startTag(index, ELEMENT_NAME);
        ctx.content(key.getName());
        ctx.endTag(index, ELEMENT_NAME);

        Map<String,Object> attributes = key.getAttributes();
        if (!attributes.isEmpty()) {
            ctx.startTag(index, ELEMENT_ATTRIBUTES);

            for (Map.Entry<String,Object> entry : attributes.entrySet()) {
                ctx.startTag(index, ELEMENT_ATTRIBUTE);
                marshalNameValuePair(ctx, entry.getKey(), entry.getValue());
                ctx.endTag(index, ELEMENT_ATTRIBUTE);
            }

            ctx.endTag(index, ELEMENT_ATTRIBUTES);
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

        Map<String,Object> attributes = new HashMap<String,Object>(4);

        // process all entries present in document
        ctx.parsePastStartTag(uri, name);

        ctx.parsePastStartTag(uri, ELEMENT_NAME);
        String keyName = ctx.accumulateText().trim();
        ctx.parsePastEndTag(uri, ELEMENT_NAME);

        if (ctx.isAt(uri, ELEMENT_ATTRIBUTES)) {
            ctx.parsePastStartTag(uri, ELEMENT_ATTRIBUTES);

            while (ctx.isAt(uri, ELEMENT_ATTRIBUTE)) {
                ctx.parsePastStartTag(uri, ELEMENT_ATTRIBUTE);
                unmarshalNameValuePair(ctx, attributes);
                ctx.parsePastEndTag(uri, ELEMENT_ATTRIBUTE);
            }

            ctx.parsePastEndTag(uri, ELEMENT_ATTRIBUTES);
        }

        ctx.parsePastEndTag(uri, name);

        return new DefaultStatsKey(keyName, null, attributes);
    }
}
