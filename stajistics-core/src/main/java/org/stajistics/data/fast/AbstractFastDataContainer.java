package org.stajistics.data.fast;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;

import org.stajistics.data.DataContainer;
import org.stajistics.data.Field;
import org.stajistics.data.FieldSet;
import org.stajistics.data.FieldUtils;

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
	    checkNotNull(field, "field");
		int index = fields.indexOf(field);
		if (index == -1) {
		    return null;
		}
        switch (field.type()) {
		case LONG:
			return Long.valueOf(longValues[index]);
		case DOUBLE:
			return Double.valueOf(doubleValues[index]);
		default:
			throw new AssertionError();
		}
	}

	@Override
	public Object getObject(String name) {
	    checkNotNull(name, "name");
		Field field = fields.getField(name);
		if (field == null) {
		    return null;
		}
        return getObject(field);
	}
	
	@Override
	public boolean getBoolean(Field field) {
	    return getLong(field) != 0;
	}

	@Override
	public long getLong(Field field) {
		int index = fields.indexOf(field);
		if (index == -1) {
		    return FieldUtils.longDefault(field);
		}
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
		if (index == -1) {
		    return FieldUtils.doubleDefault(field);
		}
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
	public FieldSet getFieldSet() {
	    return fields;
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
