package org.stajistics.data;

public interface DataSetBuilder extends DataContainer {
	void set(Field field, long value);
	void set(String field, long value);

	void set(Field field, double value);
	void set(String field, double value);
	
	void set(Field field, boolean value);
	void set(String field, boolean value);
	
	DataSet build();
}
