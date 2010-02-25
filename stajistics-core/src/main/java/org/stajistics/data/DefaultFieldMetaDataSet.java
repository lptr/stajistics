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
package org.stajistics.data;

import java.util.Map;

/**
 * 
 * @author The Stajistics Project
 */
class DefaultFieldMetaDataSet implements FieldMetaDataSet {

    private static final String PREFIX = "__" + DefaultFieldMetaDataSet.class.getSimpleName() + "__";

    private final Map<String,Object> metaDataMap;

    DefaultFieldMetaDataSet(final Map<String,Object> metaDataMap) {
        if (metaDataMap == null) {
            throw new NullPointerException("metaDataMap");
        }

        this.metaDataMap = metaDataMap;
    }

    private String keyFor(final String fieldName) {
        StringBuilder buf = new StringBuilder(fieldName.length() + PREFIX.length());
        buf.append(PREFIX);
        buf.append(fieldName);
        return buf.toString();
    }

    @Override
    public MetaData getMetaData(final String fieldName) {
        String key = keyFor(fieldName);
        MetaData metaData = (MetaData)metaDataMap.get(key);
        if (metaData == null) {
            metaData = new DefaultMetaData(metaDataMap, fieldName);
            metaDataMap.put(key, metaData);
        }
        return metaData;
    }

    @Override
    public void clear() {
        metaDataMap.clear();
    }
}
