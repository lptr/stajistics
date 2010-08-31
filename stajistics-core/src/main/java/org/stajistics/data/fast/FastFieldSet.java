package org.stajistics.data.fast;

import static com.google.common.base.Preconditions.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.data.FieldSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

final class FastFieldSet implements FieldSet, Serializable {
    private final Map<Field,Integer> fieldIndexes;
    private final Map<String,Field> fieldsByName;
    private final List<Field> fields;
    private final List<String> fieldNames;
    private final long[] longDefaults;
    private final double[] doubleDefaults;

    public FastFieldSet(Field... fields) {
        this(Iterators.forArray(checkNotNull(fields, "fields")), fields.length);
    }

    public FastFieldSet(Collection<? extends Field> fields) {
        this(checkNotNull(fields, "fields").iterator(), fields.size());
    }

    private FastFieldSet(Iterator<? extends Field> iField, int count) {
        if (count == 0) {
            throw new IllegalArgumentException(
                    "Cannot create an empty field set");
        }
        ImmutableMap.Builder<Field,Integer> fieldsBuilder = ImmutableMap
                .builder();
        ImmutableMap.Builder<String,Field> fieldsByNameBuilder = ImmutableMap
                .builder();
        ImmutableList.Builder<Field> allFieldsBuilder = ImmutableList.builder();
        ImmutableList.Builder<String> allFieldNamesBuilder = ImmutableList
                .builder();
        long[] longDefaults = new long[count];
        double[] doubleDefaults = new double[count];
        int longCounter = 0;
        int doubleCounter = 0;
        while (iField.hasNext()) {
            Field field = iField.next();
            String name = field.name();

            allFieldsBuilder.add(field);
            allFieldNamesBuilder.add(name);
            fieldsByNameBuilder.put(name, field);

            switch (field.type()) {
            case LONG: {
                longDefaults[longCounter] = Long.class.cast(field
                        .defaultValue());
                Integer index = longCounter++;
                fieldsBuilder.put(field, index);
                break;
            }
            case DOUBLE: {
                doubleDefaults[doubleCounter] = Double.class.cast(field
                        .defaultValue());
                Integer index = doubleCounter++;
                fieldsBuilder.put(field, index);
                break;
            }
            default:
                throw new AssertionError();
            }
        }

        this.fieldIndexes = fieldsBuilder.build();
        this.fieldsByName = fieldsByNameBuilder.build();
        this.fields = allFieldsBuilder.build();
        this.fieldNames = allFieldNamesBuilder.build();
        this.longDefaults = longCounter == 0 ? null : Arrays.copyOf(
                longDefaults, longCounter);
        this.doubleDefaults = doubleCounter == 0 ? null : Arrays.copyOf(
                doubleDefaults, doubleCounter);
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.iterator();
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public List<String> getFieldNames() {
        return fieldNames;
    }

    long[] cloneLongDefaults() {
        if (longDefaults == null) {
            return null;
        }
        return Arrays.copyOf(longDefaults, longDefaults.length);
    }

    double[] cloneDoubleDefaults() {
        if (doubleDefaults == null) {
            return null;
        }
        return Arrays.copyOf(doubleDefaults, doubleDefaults.length);
    }

    @Override
    public boolean contains(Field field) {
        return fieldIndexes.containsKey(field);
    }

    @Override
    public boolean contains(String fieldName) {
        return fieldsByName.containsKey(fieldName);
    }

    @Override
    public Field getField(String fieldName) {
        Field field = fieldsByName.get(fieldName);
        if (field == null) {
            return null;
        }
        return field;
    }

    @Override
    public int indexOf(Field field) {
        Integer index = fieldIndexes.get(field);
        if (index == null) {
            return -1;
        }
        return index;
    }

    @Override
    public int size() {
        return fields.size();
    }

    @Override
    public DataSetBuilder newDataSetBuilder() {
        return new FastDataSetBuilder(this);
    }
}
