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
import java.util.Iterator;
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
        return FieldUtils.doubleDefault(field);
    }

    @Override
    public FieldSet getFieldSet() {
        return NullFieldSet.getInstance();
    }

    @Override
    public long getLong(Field field) {
        return FieldUtils.longDefault(field);
    }
    
    @Override
    public boolean getBoolean(Field field) {
        return FieldUtils.booleanDefault(field);
    }

    @Override
    public Object getObject(Field field) {
        return field.defaultValue();
    }

    @Override
    public Object getObject(String fieldName) {
        return null;
    }
    
    private static class NullFieldSet implements FieldSet {
        private static final FieldSet INSTANCE = new NullFieldSet();
        
        public static FieldSet getInstance() {
            return INSTANCE;
        }

        @Override
        public boolean contains(Field field) {
            return false;
        }

        @Override
        public boolean contains(String fieldName) {
            return false;
        }

        @Override
        public Field getField(String fieldName) {
            return null;
        }

        @Override
        public List<String> getFieldNames() {
            return Collections.emptyList();
        }

        @Override
        public List<Field> getFields() {
            return Collections.emptyList();
        }

        @Override
        public int indexOf(Field field) {
            return -1;
        }

        @Override
        public DataSetBuilder newDataSetBuilder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Field> iterator() {
            return Collections.<Field> emptyList().iterator();
        }
   }


}
