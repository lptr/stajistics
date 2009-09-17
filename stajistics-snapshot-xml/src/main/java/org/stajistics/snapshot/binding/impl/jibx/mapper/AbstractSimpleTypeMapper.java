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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.Utility;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;
import org.stajistics.data.DataContainer;

/**
 * 
 * @author The Stajistics Project
 * 
 */
public abstract class AbstractSimpleTypeMapper implements IMarshaller,IUnmarshaller,IAliasable {

    protected static final String ELEMENT_NAME = "name";
    protected static final String ELEMENT_VALUE = "value";
    protected static final String ELEMENT_TYPE = "type";

    protected final String uri;
    protected final int index;
    protected final String name;

    protected static final ValueMarshaller DEFAULT_VALUE_MARSHALLER = new ValueMarshaller() {
        @Override
        public String marshal(final Object value) throws Exception {
            return value.toString();
        }
    };

    protected static final ValueUnmarshaller DEFAULT_VALUE_UNMARSHALLER = new ValueUnmarshaller() {
        @Override
        public Object unmarshal(final String value) throws Exception {
            return Utility.parseDouble(value);
        }
    };

    protected static final Map<String,ValueMarshaller> VALUE_MARSHALLERS = new HashMap<String,ValueMarshaller>();
    static {
        VALUE_MARSHALLERS.put(Float.class.getName(), new ValueMarshaller() {
            @Override
            public String marshal(final Object value) throws Exception {
                return Utility.serializeFloat((Float) value);
            }
        });
        VALUE_MARSHALLERS.put(Double.class.getName(), new ValueMarshaller() {
            @Override
            public String marshal(final Object value) throws Exception {
                return Utility.serializeDouble((Double) value);
            }
        });
        VALUE_MARSHALLERS.put(Date.class.getName(), new ValueMarshaller() {
            @Override
            public String marshal(final Object value) throws Exception {
                return Utility.serializeDateTime((Date) value);
            }
        });
    }

    protected static final Map<String,ValueUnmarshaller> VALUE_UNMARSHALLERS = new HashMap<String,ValueUnmarshaller>();
    static {
        VALUE_UNMARSHALLERS.put(Byte.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value) throws Exception {
                return Utility.parseByte(value);
            }
        });
        VALUE_UNMARSHALLERS.put(Character.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value)
                    throws Exception {
                return Utility.parseChar(value);
            }
        });
        VALUE_UNMARSHALLERS.put(Boolean.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value)
                    throws Exception {
                return Utility.parseBoolean(value);
            }
        });
        VALUE_UNMARSHALLERS.put(Short.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value) throws Exception {
                return Utility.parseShort(value);
            }
        });
        VALUE_UNMARSHALLERS.put(Integer.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value)
                    throws Exception {
                return Utility.parseInt(value);
            }
        });
        VALUE_UNMARSHALLERS.put(Long.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value) throws Exception {
                return Utility.parseLong(value);
            }
        });
        VALUE_UNMARSHALLERS.put(Double.class.getName(), DEFAULT_VALUE_UNMARSHALLER);
        VALUE_UNMARSHALLERS.put(Date.class.getName(), new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value) throws Exception {
                return Utility.deserializeDateTime(value);
            }
        });
        VALUE_UNMARSHALLERS.put(String.class.getName(),new ValueUnmarshaller() {
            @Override
            public Object unmarshal(final String value)
                    throws Exception {
                return value;
            }
        });
    }

    public AbstractSimpleTypeMapper(final String uri, final int index, final String name) {
        this.uri = uri;
        this.index = index;
        this.name = name;
    }

    protected abstract Logger getLogger();

    @Override
    public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException {
        return ctx.isAt(uri, name);
    }

    protected void marshalNameValuePair(final MarshallingContext ctx,
                                        final String name,
                                        final Object value) throws JiBXException {
        // name
        ctx.startTag(index, ELEMENT_NAME);
        ctx.content(name);
        ctx.endTag(index, ELEMENT_NAME);

        String type = value.getClass().getName();

        ValueMarshaller marshaller = getValueMarshaller(type);

        // value
        ctx.startTag(index, ELEMENT_VALUE);
        try {
            ctx.content(marshaller.marshal(value));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, 
                            "Failed to marshal field: " + name + " = " + value, 
                            e);
        }
        ctx.endTag(index, ELEMENT_VALUE);

        // type
        if (!type.equals(Double.class.getName())) {
            ctx.startTag(index, ELEMENT_TYPE);
            ctx.content(type);
            ctx.endTag(index, ELEMENT_TYPE);
        }
    }

    private ValueMarshaller getValueMarshaller(final String type) {
        ValueMarshaller vm = VALUE_MARSHALLERS.get(type);
        if (vm == null) {
            vm = DEFAULT_VALUE_MARSHALLER;
        }
        return vm;
    }

    protected String unmarshalNameValuePair(final UnmarshallingContext ctx,
                                            final Object target) throws JiBXException {
        // name
        ctx.parsePastStartTag(uri, ELEMENT_NAME);
        String name = ctx.accumulateText();
        if (name != null) {
            name = name.trim();
        }
        ctx.parsePastEndTag(uri, ELEMENT_NAME);

        // value
        ctx.parsePastStartTag(uri, ELEMENT_VALUE);
        String strValue = ctx.accumulateText();
        if (strValue != null) {
            strValue = strValue.trim();
        }
        ctx.parsePastEndTag(uri, ELEMENT_VALUE);

        // type
        String type = null;
        if (ctx.isAt(uri, ELEMENT_TYPE)) {
            ctx.parsePastStartTag(uri, ELEMENT_TYPE);
            type = ctx.accumulateText();
            if (type != null) {
                type = type.trim();
            }
            ctx.parsePastEndTag(uri, ELEMENT_TYPE);
        }

        ValueUnmarshaller unmarshaller = getValueUnmarshaller(type);

        try {
            Object value = unmarshaller.unmarshal(strValue);

            if (target instanceof DataContainer) {
                ((DataContainer)target).setField(name, value);

            } else if (target instanceof Map) {
                ((Map)target).put(name, value);

            } else {
                throw new IllegalArgumentException("Unsupported unmarshal target: " + target.getClass());
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE,
                            "Failed to unmarshal value field: " + name + " = " + strValue, 
                            e);
        }

        return name;
    }

    private ValueUnmarshaller getValueUnmarshaller(final String type) {
        ValueUnmarshaller vu = null;
        if (type != null) {
            vu = VALUE_UNMARSHALLERS.get(type);
        }
        if (vu == null) {
            vu = DEFAULT_VALUE_UNMARSHALLER;
        }
        return vu;
    }

    /* NESTED CLASSES */

    protected static interface ValueMarshaller {
        String marshal(Object value) throws Exception;
    }

    protected static interface ValueUnmarshaller {
        Object unmarshal(String value) throws Exception;
    }

}
