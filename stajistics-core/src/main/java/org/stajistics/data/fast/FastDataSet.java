package org.stajistics.data.fast;

import org.stajistics.data.DataSet;

final class FastDataSet extends AbstractFastDataContainer implements DataSet {

	public FastDataSet(FastFieldSet fields, long[] longValues, double[] doubleValues) {
		super(fields, longValues, doubleValues);
	}
	
}
