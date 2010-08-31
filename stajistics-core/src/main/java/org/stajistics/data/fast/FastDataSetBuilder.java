package org.stajistics.data.fast;

import static com.google.common.base.Preconditions.*;

import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.data.FieldUtils;

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
	public long set(Field field, long value) {
	    checkNotNull(field, "field");
		checkState();
		int index = fields.indexOf(field);
		if (index == -1) {
		    return FieldUtils.longDefault(field);
		}
		long oldValue;
		switch (field.type()) {
		case LONG:
            oldValue = longValues[index];
            longValues[index] = (long) value;
			break;
		case DOUBLE:
		    oldValue = (long) doubleValues[index];
			doubleValues[index] = value;
			break;
		default:
			throw new AssertionError();
		}
		return oldValue;
	}

	@Override
	public double set(Field field, double value) {
        checkNotNull(field, "field");
		checkState();
		int index = fields.indexOf(field);
        if (index == -1) {
            return FieldUtils.doubleDefault(field);
        }
		double oldValue;
		switch (field.type()) {
		case LONG:
		    oldValue = longValues[index];
			longValues[index] = (long) value;
			break;
		case DOUBLE:
		    oldValue = doubleValues[index];
			doubleValues[index] = value;
			break;
		default:
			throw new AssertionError();
		}
		return oldValue;
	}

	@Override
	public boolean set(Field field, boolean value) {
	    return set(field, value ? 1L : 0L) != 0;
	}

	@Override
	public Object set(String name, Object value) {
	    checkNotNull(name, "name");
	    checkArgument(name.length() > 0, "empty name");
	    Field field = fields.getField(name);
	    if (field == null) {
	        return null;
	    }
	    return set(field, value);
	}

	@Override
	public Object set(Field field, Object value) {
	    checkNotNull(field, "field");
	    checkNotNull(value, "value");
	    switch (field.type()) {
        case LONG:
            return set(field, ((Number) value).longValue());
        case DOUBLE:
            return set(field, ((Number) value).doubleValue());
        default:
            throw new AssertionError();
	    }
	}
}
