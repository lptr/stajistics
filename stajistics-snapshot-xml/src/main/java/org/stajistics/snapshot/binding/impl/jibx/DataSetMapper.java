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

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.data.DataSet;
import org.stajistics.data.DefaultDataSet;
import org.stajistics.data.MetaData;
import org.stajistics.data.MetaDataSet;

/**
 * 
 * @author The Stajistics Project
 * 
 */
public class DataSetMapper extends AbstractSimpleTypeMapper {

    private static final Logger logger = LoggerFactory.getLogger(DataSetMapper.class);

    //private static final int DEFAULT_SIZE = 32;

    //private static final String ATTR_SIZE = "size";
    private static final String ELEMENT_FIELD = "field";
    private static final String ELEMENT_META = "meta";

    public DataSetMapper() {
        this(null, 0, "dataSet");
    }

    public DataSetMapper(final String uri, final int index, final String name) {
        super(uri, index, name);
    }

    @Override
    public boolean isExtension(String mapname) {
        return false;
    }

    @Override
    public void marshal(final Object obj, final IMarshallingContext ictx)
            throws JiBXException {

        if (!(obj instanceof DataSet)) {
            throw new JiBXException("Invalid object type for marshaller: "
                    + obj.getClass().getName());
        }
        if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller: "
                    + ictx.getClass().getName());
        }

        MarshallingContext ctx = (MarshallingContext) ictx;

        DataSet dataSet = (DataSet) obj;

        ctx.startTagAttributes(index, name);

        if (!dataSet.isEmpty()) {

            // size attribute
            //ctx.attribute(index, ATTR_SIZE, dataSet.size());
            //ctx.closeStartContent();

            MetaData metaData = dataSet.getMetaData();
            if (!metaData.isEmpty()) {

                ctx.startTag(index, ELEMENT_META);

                for (String name : metaData.getFieldNames()) {
                    ctx.startTag(index, ELEMENT_FIELD);
                    marshalNameValuePair(ctx, name, metaData.getField(name));
                    ctx.endTag(index, ELEMENT_FIELD);
                }

                ctx.endTag(index, ELEMENT_META);
            }

            MetaDataSet fieldMetaDataSet = dataSet.getFieldMetaDataSet();

            for (String fieldName : dataSet.getFieldNames()) {
                ctx.startTag(index, ELEMENT_FIELD);
                marshalNameValuePair(ctx, fieldName, dataSet.getField(fieldName));

                // meta

                MetaData fieldMetaData = fieldMetaDataSet.getMetaData(fieldName);
                if (!fieldMetaData.isEmpty()) {
                    ctx.startTag(index, ELEMENT_META);

                    for (String metaName : fieldMetaData.getFieldNames()) {
                        ctx.startTag(index, ELEMENT_FIELD);
                        marshalNameValuePair(ctx, metaName, metaData.getField(metaName));
                        ctx.endTag(index, ELEMENT_FIELD);
                    }

                    ctx.endTag(index, ELEMENT_META);
                }

                ctx.endTag(index, ELEMENT_FIELD);
            }
        }

        ctx.endTag(index, name);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Object unmarshal(final Object obj, final IUnmarshallingContext ictx)
            throws JiBXException {

        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }

        //int size = ctx.attributeInt(uri, ATTR_SIZE, DEFAULT_SIZE); // TODO

        DataSet dataSet = (DataSet)obj;
        if (dataSet == null) {
            dataSet = new DefaultDataSet();
        }

        ctx.parsePastStartTag(uri, name);

        // meta

        if (ctx.isAt(uri, ELEMENT_META)) {
            MetaData metaData = dataSet.getMetaData();

            ctx.parsePastStartTag(uri, ELEMENT_META);

            while (ctx.isAt(uri, ELEMENT_FIELD)) {
                ctx.parsePastStartTag(uri, ELEMENT_FIELD);
                unmarshalNameValuePair(ctx, metaData);
                ctx.parsePastEndTag(uri, ELEMENT_FIELD);
            }

            ctx.parsePastEndTag(uri, ELEMENT_META);
        }

        MetaDataSet fieldMetaDataSet = dataSet.getFieldMetaDataSet();

        while (ctx.isAt(uri, ELEMENT_FIELD)) {
            ctx.parsePastStartTag(uri, ELEMENT_FIELD);

            String fieldName = unmarshalNameValuePair(ctx, dataSet);

            if (ctx.isAt(uri, ELEMENT_META)) {
                ctx.parsePastStartTag(uri, ELEMENT_META);
                MetaData fieldMetaData = fieldMetaDataSet.getMetaData(fieldName);
                unmarshalNameValuePair(ctx, fieldMetaData);
                ctx.parsePastEndTag(uri, ELEMENT_META);
            }

            ctx.parsePastEndTag(uri, ELEMENT_FIELD);
        }
        ctx.parsePastEndTag(uri, name);

        return dataSet;
    }

}