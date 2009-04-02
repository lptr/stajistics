/* Copyright 2009 The Stajistics Project
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

import org.stajistics.session.StatsSession;
import org.stajistics.session.data.DataSet;
import org.stajistics.session.data.MutableDataSet;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.AtomicDouble;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DistributionDataRecorder implements DataRecorder {

    protected final AtomicDouble product = new AtomicDouble(1); // For geometric mean
    protected final AtomicDouble sumOfInverses = new AtomicDouble(0); // For harmonic mean
    protected final AtomicDouble sumOfSquares = new AtomicDouble(0); // For standard deviation and quadratic mean

    @Override
    public void update(final StatsSession session, 
                       final StatsTracker tracker, 
                       final long now) {

        final double currentValue = tracker.getValue();
        double tmp;

        // Sum of squares (for standard deviation and quadratic mean calculation)
        sumOfSquares.getAndAdd(currentValue * currentValue);

        // Product (for geometric mean calculation)
        for (;;) {
            tmp = product.get();
            double newProduct = tmp * currentValue;
            if (product.compareAndSet(tmp, newProduct)) {
                break;
            }
        }

        // Sum of inverses (for harmonic mean calculation)
        sumOfInverses.getAndAdd(1 / currentValue);
    }

    @Override
    public void collectData(final StatsSession session, final MutableDataSet dataSet) {
        dataSet.setField(DataSet.Field.ARITHMETIC_MEAN,
                         getArithmeticMean(session));
        dataSet.setField(DataSet.Field.GEOMETRIC_MEAN,
                         getGeometricMean(session));
        dataSet.setField(DataSet.Field.HARMONIC_MEAN,
                         getHarmonicMean(session));
        dataSet.setField(DataSet.Field.QUADRATIC_MEAN,
                         getQuadraticMean(session));
        dataSet.setField(DataSet.Field.STANDARD_DEVIATION,
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
        if (n == 0) {
            return 0.0;
        }

        return session.getSum() / n;
    }

    protected double getGeometricMean(final StatsSession session) {
        final long n = session.getCommits();
        if (n == 0) {
            return 0.0;
        }

        return Math.pow(product.get(), 1.0 / n);
    }

    protected double getHarmonicMean(final StatsSession session) {
        final long n = session.getCommits();
        if (n == 0) {
            return 0.0;
        }

        return n / sumOfInverses.get();
    }

    protected double getQuadraticMean(final StatsSession session) {
        final long n = session.getCommits();
        if (n == 0) {
            return 0.0;
        }

        return Math.sqrt(sumOfSquares.get() / n);
    }

    protected double getStandardDeviation(final StatsSession session) {
        final long n = session.getCommits();
        if (n == 0) {
            return 0.0;
        }

        double valueSum = session.getSum();
        double nMinus1 = (n <= 1) ? 1 : n - 1;
        double numerator = sumOfSquares.get() - ((valueSum * valueSum) / n);

        return Math.sqrt(numerator / nMinus1);
    }

}
