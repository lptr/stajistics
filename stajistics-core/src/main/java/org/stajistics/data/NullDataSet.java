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
package org.stajistics.data;

import java.util.Collections;
import java.util.Set;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public final class NullDataSet implements DataSet {

    private static final long serialVersionUID = 7206590288937897600L;

    private static final NullDataSet instance = new NullDataSet();

    private static final MetaDataSet NULL_META_DATA_SET = new MetaDataSet() {
        @Override
        public MetaData getMetaData(final String fieldName) {
            return NullMetaData.getInstance();
        }

        @Override
        public void clear() {}
    };

    private NullDataSet() {}

    public static NullDataSet getInstance() {
        return instance;
    }

    @Override
    public MetaData getMetaData() {
        return NullMetaData.getInstance();
    }

    @Override
    public MetaDataSet getFieldMetaDataSet() {
        return NULL_META_DATA_SET;
    }

    @Override
    public Object getField(String name) {
        return null;
    }

    @Override
    public <T> T getField(String name, Class<T> type) {
        return null;
    }

    @Override
    public Set<String> getFieldNames() {
        return Collections.emptySet();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {}

    @Override
    public Object removeField(String name) {
        return null;
    }

    @Override
    public void setField(String name, Object value) {}

}
