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

import java.util.Collections;
import java.util.List;

/**
 * A {@link DataSet} implementation conforming to the null object pattern.
 *
 * @author The Stajistics Project
 */
public final class NullDataSet implements DataSet {

    private static final NullDataSet instance = new NullDataSet();

    private NullDataSet() {}

    public static NullDataSet getInstance() {
        return instance;
    }

    @Override
    public double getDouble(Field field) {
        return FieldUtils.doubleDefaultValue(field);
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public List<String> getFieldNames() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends Field> getFields() {
        return Collections.emptyList();
    }

    @Override
    public long getLong(Field field) {
        return FieldUtils.doubleDefaultValue(field);
    }
    
    @Override
    public boolean getBoolean(Field field) {
        return FieldUtils.booleanDefaultValue(field);
    }

    @Override
    public Object getObject(Field field) {
        return field.defaultValue();
    }

    @Override
    public Object getObject(String fieldName) {
        return null;
    }

}
