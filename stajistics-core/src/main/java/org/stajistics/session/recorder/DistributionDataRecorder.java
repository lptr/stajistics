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
package org.stajistics.session.recorder;

import java.util.Arrays;
import java.util.List;

import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.Tracker;
import org.stajistics.util.AtomicDouble;
import org.stajistics.util.ThreadSafe;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
@ThreadSafe
public class DistributionDataRecorder implements DataRecorder {

    protected final AtomicDouble product = new AtomicDouble(1); // For geometric mean
    protected final AtomicDouble sumOfInverses = new AtomicDouble(0); // For harmonic mean
    protected final AtomicDouble sumOfSquares = new AtomicDouble(0); // For standard deviation and quadratic mean

    @Override
    public List<? extends Field> getSupportedFields() {
        return Arrays.<Field> asList(RecorderField.values());
    }

    @Override
    public void update(final StatsSession session,
                       final Tracker tracker,
                       final long now) {

        final double currentValue = tracker.getValue();
        double tmp;

        // Sum of squares (for standard deviation and quadratic mean calculation)
        sumOfSquares.addAndGet(currentValue * currentValue);

        // Product (for geometric mean calculation)
        for (;;) {
            tmp = product.get();
            double newProduct = tmp * currentValue;
            if (product.compareAndSet(tmp, newProduct)) {
                break;
            }
        }

        // Sum of inverses (for harmonic mean calculation)
        sumOfInverses.addAndGet(1 / currentValue);
    }

    @Override
    public void restore(final DataSet dataSet) {
        product.set(dataSet.getDouble(RecorderField.product));
        sumOfSquares.set(dataSet.getDouble(RecorderField.sumOfSquares));
        sumOfInverses.set(dataSet.getDouble(RecorderField.sumOfInverses));
    }
    
    @Override
    public Object getObject(StatsSession session,
                            org.stajistics.data.Field field) {
        switch (field.type()) {
        case LONG:
            return getLong(session, field);
        case DOUBLE:
            return getDouble(session, field);
        default:
            throw new AssertionError();
        }
    }
    
    @Override
    public long getLong(StatsSession session, org.stajistics.data.Field field) {
        return 0;
    }

    @Override
    public double getDouble(StatsSession session, Field field) {
        if (!(field instanceof RecorderField)) {
            throw new IllegalArgumentException("Unknown field: " + field);
        }
        switch ((RecorderField) field) {
        case product:
            return product.get();
        case sumOfSquares:
            return sumOfSquares.get();
        case sumOfInverses:
            return sumOfInverses.get();
        case aMean:
            return getArithmeticMean(session);
        case gMean:
            return getGeometricMean(session);
        case hMean:
            return getHarmonicMean(session);
        case qMean:
            return getQuadraticMean(session);
        case stdDev:
            return getStandardDeviation(session);
        default:
            throw new AssertionError();
        }
    }

    @Override
    public void collectData(final StatsSession session, final DataSetBuilder dataSet) {
        dataSet.set(RecorderField.product, product.get());
        dataSet.set(RecorderField.sumOfSquares, sumOfSquares.get());
        dataSet.set(RecorderField.sumOfInverses, sumOfInverses.get());
        dataSet.set(RecorderField.aMean,
                         getArithmeticMean(session));
        dataSet.set(RecorderField.gMean,
                         getGeometricMean(session));
        dataSet.set(RecorderField.hMean,
                         getHarmonicMean(session));
        dataSet.set(RecorderField.qMean,
                         getQuadraticMean(session));
        dataSet.set(RecorderField.stdDev,
                         getStandardDeviation(session));
    }

    @Override
    public void clear() {
        product.set(1);
        sumOfInverses.set(0);
        sumOfSquares.set(0);
    }

    protected double getArithmeticMean(final StatsSession session) {
        final long n = session.getCommits();
        if (n <= 0) {
            return 0.0;
        }

        return session.getSum() / n;
    }

    protected double getGeometricMean(final StatsSession session) {
        final long n = session.getCommits();
        if (n <= 0) {
            return 0.0;
        }

        return Math.pow(product.get(), 1.0 / n);
    }

    protected double getHarmonicMean(final StatsSession session) {
        final long n = session.getCommits();
        final double soi = sumOfInverses.get();
        if (n <= 0 || soi <= 0) {
            return 0.0;
        }

        return n / soi;
    }

    protected double getQuadraticMean(final StatsSession session) {
        final long n = session.getCommits();
        if (n <= 0) {
            return 0.0;
        }

        return Math.sqrt(sumOfSquares.get() / n);
    }

    protected double getStandardDeviation(final StatsSession session) {
        final long n = session.getCommits();
        if (n <= 0) {
            return 0.0;
        }

        double valueSum = session.getSum();
        double nMinus1 = (n <= 1) ? 1 : n - 1;
        double numerator = sumOfSquares.get() - ((valueSum * valueSum) / n);

        return Math.sqrt(numerator / nMinus1);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /* NESTED CLASSES */

    public static enum RecorderField implements Field {
        product,
        sumOfInverses,
        sumOfSquares,

        aMean,
        gMean,
        hMean,
        qMean,
        stdDev;

        @Override
        public Type type() {
            return Type.DOUBLE;
        }

        @Override
        public Object defaultValue() {
            return Double.NaN;
        }

    }
}
