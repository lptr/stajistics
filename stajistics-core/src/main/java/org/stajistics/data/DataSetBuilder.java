package org.stajistics.data;

public interface DataSetBuilder extends DataContainer {
    Object set(Field field, Object value);

    Object set(String field, Object value);

    long set(Field field, long value);

	double set(Field field, double value);

	boolean set(Field field, boolean value);

	DataSet build();
}
