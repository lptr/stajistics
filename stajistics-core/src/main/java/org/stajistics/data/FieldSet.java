package org.stajistics.data;

import java.util.List;

public interface FieldSet extends Iterable<Field> {

    List<Field> getFields();

    List<String> getFieldNames();

    boolean contains(Field field);

    boolean contains(String fieldName);

    Field getField(String fieldName);

    int indexOf(Field field);

    int size();

    DataSetBuilder newDataSetBuilder();
}
