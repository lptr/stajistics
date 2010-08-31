package org.stajistics.data.fast;

import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;

final class FastDataSetBuilder extends AbstractFastDataContainer implements DataSetBuilder {
	private boolean built;

	public FastDataSetBuilder(FastFieldSet fields) {
		super(fields, fields.cloneLongDefaults(), fields.cloneDoubleDefaults());
	}

	@Override
	public DataSet build() {
		checkState();
		FastDataSet dataSet = new FastDataSet(fields, longValues, doubleValues);
		this.built = true;
		return dataSet;
	}

	private void checkState() {
		if (built) {
			throw new IllegalStateException("DataSet already built");
		}
	}

	@Override
	public Object getObject(Field field) {
		checkState();
		return super.getObject(field);
	}

	@Override
	public long getLong(Field field) {
		checkState();
		return super.getLong(field);
	}

	@Override
	public double getDouble(Field field) {
		checkState();
		return super.getDouble(field);
	}

	@Override
	public void set(Field field, long value) {
		checkState();
		int index = fields.indexOf(field);
		switch (field.type()) {
		case LONG:
			longValues[index] = (long) value;
			break;
		case DOUBLE:
			doubleValues[index] = value;
			break;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public void set(Field field, double value) {
		checkState();
		int index = fields.indexOf(field);
		switch (field.type()) {
		case LONG:
			longValues[index] = (long) value;
			break;
		case DOUBLE:
			doubleValues[index] = value;
			break;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public void set(String fieldName, long value) {
		checkState();
		set(fields.getField(fieldName), value);
	}

	@Override
	public void set(String fieldName, double value) {
		checkState();
		set(fields.getField(fieldName), value);
	}

	@Override
	public void set(Field field, boolean value) {
	    checkState();
	    set(field, value ? 0L : 1L);
	}
	
	@Override
	public void set(String field, boolean value) {
	    checkState();
	    set(fields.getField(field), value);
	}
}
