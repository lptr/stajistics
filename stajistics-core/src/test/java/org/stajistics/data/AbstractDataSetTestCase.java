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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionContaining.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.TestUtil;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public abstract class AbstractDataSetTestCase extends AbstractStajisticsTestCase {

    private DataSetBuilder dataSetBuilder;

    protected abstract DataSetBuilder createDataSetBuilder(Field... fields);

    protected DataContainer dsb() {
        return dataSetBuilder;
    }

    @Before
    public void setUp() {
        dataSetBuilder = createDataSetBuilder(TestField.values());
    }

    @Test
    public void testInitializedState() {
        testInitializedState(dataSetBuilder);
        testInitializedState(dataSetBuilder.build());
    }

    private void testInitializedState(DataContainer dataContainer) {
        FieldSet fieldSet = dataContainer.getFieldSet();
        assertNotNull(fieldSet);
        assertThat(fieldSet.size(), equalTo(TestField.values().length));
        assertThat(fieldSet, hasItems((Field[]) TestField.values()));
        
        List<String> fieldNames = fieldSet.getFieldNames();
        assertThat(fieldNames.size(), equalTo(TestField.values().length));
        for (Field field : TestField.values()) {
            assertThat(fieldNames, hasItem(field.name()));
        }
        
        assertThat(dataContainer.getObject("test1"), equalTo((Object) 12L));
        assertThat(dataContainer.getObject("test2"), equalTo((Object) 3.14D));
        assertThat(dataContainer.getLong(TestField.test1), equalTo(12L));
        assertThat(dataContainer.getDouble(TestField.test1), equalTo(12D));
        assertThat(dataContainer.getBoolean(TestField.test1), equalTo(true));
        assertThat(dataContainer.getBoolean(TestField.test3), equalTo(false));
        
        assertThat(dataContainer.getObject(RogueField.INSTANCE), nullValue());
        assertThat(dataContainer.getObject("rogue!"), nullValue());
        assertThat(dataContainer.getLong(RogueField.INSTANCE), equalTo(-1L));
        assertThat(dataContainer.getDouble(RogueField.INSTANCE), equalTo(-1.23D));
    }

    @Test
    public void testSetGetField() {
        dataSetBuilder.set(TestField.test1, 3.5);
        assertEquals(3L, dataSetBuilder.getLong(TestField.test1));
        assertEquals(3.0D, dataSetBuilder.getDouble(TestField.test1), TestUtil.DELTA);

        dataSetBuilder.set(TestField.test2, 3.5);
        assertEquals(3L, dataSetBuilder.getLong(TestField.test2));
        assertEquals(3.5D, dataSetBuilder.getDouble(TestField.test2), TestUtil.DELTA);

        DataSet dataSet = dataSetBuilder.build();
        assertEquals(3L, dataSet.getObject(TestField.test1));
        assertEquals(3L, dataSet.getObject(TestField.test1.name()));
        assertEquals(3L, dataSet.getLong(TestField.test1));
        assertEquals(3.0D, dataSet.getDouble(TestField.test1), TestUtil.DELTA);

        assertEquals(3.5D, (Double) dataSet.getObject(TestField.test2), TestUtil.DELTA);
        assertEquals(3.5D, (Double) dataSet.getObject(TestField.test2.name()), TestUtil.DELTA);
        assertEquals(3L, dataSet.getLong(TestField.test2), TestUtil.DELTA);
        assertEquals(3.5D, dataSet.getDouble(TestField.test2), TestUtil.DELTA);
    }

    @Test(expected = NullPointerException.class)
    public void testGetLongWithNullField() {
        dataSetBuilder.getLong(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetDoubleWithNullField() {
        dataSetBuilder.getDouble(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testGetObjectWithNullField() {
        dataSetBuilder.getObject((Field) null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetObjectWithNullFieldName() {
        dataSetBuilder.getObject((String) null);
    }
    
    @Test
    public void testSetFieldWithNullValue() {
        try {
            dataSetBuilder.set("test", null);
        } catch (NullPointerException npe) {
            assertEquals("value", npe.getMessage());
        }
    }

    @Test
    public void testSetFieldWithEmptyName() {
        try {
            dataSetBuilder.set("", 123L);
        } catch (IllegalArgumentException iae) {
            assertEquals("empty name", iae.getMessage());
        }
    }

    @Test
    public void testSetWithNullField() {
        try {
            dataSetBuilder.set((Field) null, 123L);
        } catch (NullPointerException npe) {
            assertEquals("field", npe.getMessage());
        }
        try {
            dataSetBuilder.set((Field) null, null);
        } catch (NullPointerException npe) {
            assertEquals("field", npe.getMessage());
        }
    }
    
    @Test
    public void testSetFieldWithNullName() {
        try {
            dataSetBuilder.set((String) null, 123L);
        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
        try {
            dataSetBuilder.set((String) null, null);
        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
    }

    public enum SimpleField implements Field {
        test;

        @Override
        public Long defaultValue() {
            return 1L;
        }

        @Override
        public Type type() {
            return Type.LONG;
        }
    }

    public enum TestField implements Field {
        test1(12L), test2(3.14D), test3(0L);

        private final Type type;
        private final Object defaultValue;

        TestField(long defaultValue) {
            this.type = Type.LONG;
            this.defaultValue = defaultValue;
        }

        TestField(double defaultValue) {
            this.type = Type.DOUBLE;
            this.defaultValue = defaultValue;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }
    }

    public static final class RogueField implements Field {
        public static final Field INSTANCE = new RogueField();
        
        private RogueField() {
            // Forbidding constructor
        }

        @Override
        public Object defaultValue() {
            return -1.23D;
        }

        @Override
        public String name() {
            return "rogue!";
        }

        @Override
        public Type type() {
            return Type.DOUBLE;
        }
    }
}
