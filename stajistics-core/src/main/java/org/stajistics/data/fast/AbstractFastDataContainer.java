package org.stajistics.data.fast;

import java.util.Arrays;
import java.util.List;

import org.stajistics.data.DataContainer;
import org.stajistics.data.Field;

abstract class AbstractFastDataContainer implements DataContainer {
	protected final FastFieldSet fields;
	protected final long[] longValues;
	protected final double[] doubleValues;

	protected AbstractFastDataContainer(FastFieldSet fields, long[] longValues, double[] doubleValues) {
		this.fields = fields;
		this.longValues = longValues;
		this.doubleValues = doubleValues;
	}

	@Override
	public Object getObject(Field field) {
		switch (field.type()) {
		case LONG:
			return Long.valueOf(longValues[fields.indexOf(field)]);
		case DOUBLE:
			return Double.valueOf(doubleValues[fields.indexOf(field)]);
		default:
			throw new AssertionError();
		}
	}

	@Override
	public Object getObject(String fieldName) {
		return getObject(fields.getField(fieldName));
	}
	
	@Override
	public boolean getBoolean(Field field) {
	    return getLong(field) != 0;
	}

	@Override
	public long getLong(Field field) {
		int index = fields.indexOf(field);
		switch (field.type()) {
		case LONG:
			return longValues[index];
		case DOUBLE:
			return (long) doubleValues[index];
		default:
			throw new AssertionError();
		}
	}

	@Override
	public double getDouble(Field field) {
		int index = fields.indexOf(field);
		switch (field.type()) {
		case LONG:
			return longValues[index];
		case DOUBLE:
			return doubleValues[index];
		default:
			throw new AssertionError();
		}
	}

	@Override
	public int getFieldCount() {
	    return fields.size();
	}

	@Override
	public List<? extends Field> getFields() {
		return fields.getFields();
	}

	@Override
	public List<String> getFieldNames() {
		return fields.getFieldNames();
	}
	

	/**
	 * Do not use {@link FastDataSet} as keys in hash tables.
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FastDataSet other = (FastDataSet) obj;
		if (fields == null) {
			if (other.fields != null) {
				return false;
			}
		} else if (!fields.equals(other.fields)) {
			return false;
		}
		if (!Arrays.equals(doubleValues, other.doubleValues)) {
			return false;
		}
		if (!Arrays.equals(longValues, other.longValues)) {
			return false;
		}
		return true;
	}
}
