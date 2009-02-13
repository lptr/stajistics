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
package org.stajistics.session;

import java.util.concurrent.atomic.AtomicReference;

import org.stajistics.StatsKey;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.AtomicDouble;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class DefaultStatsSession extends AbstractStatsSession {

    private static final long serialVersionUID = -6959191477629645419L;

    protected final AtomicReference<Double> first = new AtomicReference<Double>(null);
    protected final AtomicDouble last = new AtomicDouble(Double.NaN);
    protected final AtomicDouble min = new AtomicDouble(Double.MAX_VALUE);
    protected final AtomicDouble max = new AtomicDouble(Double.MIN_VALUE);
    protected final AtomicDouble sum = new AtomicDouble(0);

    protected final AtomicDouble product = new AtomicDouble(1); // For geometric mean
    protected final AtomicDouble sumOfInverses = new AtomicDouble(0); // For harmonic mean
    protected final AtomicDouble sumOfSquares = new AtomicDouble(0); // For standard deviation and quadratic mean

    public DefaultStatsSession(final StatsKey key) {
        super(key);
    }

    protected void updateImpl(final StatsTracker tracker,
                              final long now) {

        double tmp;
        double currentValue = tracker.getValue();

        // First
        if (first.get() == null) {
            first.compareAndSet(null, new Double(currentValue));
        }

        // Last
        last.set(currentValue);

        // Min
        for (;;) {
            tmp = min.get();
            if (currentValue < tmp) {
                if (min.compareAndSet(tmp, currentValue)) {
                    break;
                }
            } else {
                break;
            }
        }

        // Max
        for (;;) {
            tmp = max.get();
            if (currentValue > tmp) {
                if (max.compareAndSet(tmp, currentValue)) {
                    break;
                }
            } else {
                break;
            }
        }

        // Sum
        sum.getAndAdd(currentValue);

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
    public double getFirst() {
        Double firstValue = first.get();

        if (firstValue == null) {
            return Double.NaN;
        }

        return firstValue.doubleValue();
    }

    @Override
    public double getLast() {
        return this.last.get();
    }

    @Override
    public double getMin() {
        return this.min.get();
    }

    @Override
    public double getMax() {
        return this.max.get();
    }

    public double getSum() {
        return this.sum.get();
    }

    public double getArithmeticMean() {
        long n = getCommits();
        if (n == 0) {
            return 0.0;
        }

        return sum.get() / n;
    }

    public double getGeometricMean() {
        long n = getCommits();
        if (n == 0) {
            return 0.0;
        }

        return Math.pow(product.get(), 1.0 / n);
    }

    public double getHarmonicMean() {
        long n = getCommits();
        if (n == 0) {
            return 0.0;
        }

        return n / sumOfInverses.get();
    }

    public double getQuadraticMean() {
        long n = getCommits();
        if (n == 0) {
            return 0.0;
        }

        return Math.sqrt(sumOfSquares.get() / n);
    }

    public double getStandardDeviation() {
        long n = getCommits();
        if (n == 0) {
            return 0.0;
        }

        double valueSum = getSum();
        double nMinus1 = (n <= 1) ? 1 : n - 1;
        double numerator = sumOfSquares.get() - ((valueSum * valueSum) / n);

        return Math.sqrt(numerator / nMinus1);
    }

    @Override
    protected void appendStats(final StringBuilder buf) {
        appendStat(buf, Attributes.FIRST, getFirst());
        appendStat(buf, Attributes.LAST, getLast());
        appendStat(buf, Attributes.MIN, getMin());
        appendStat(buf, Attributes.MAX, getMax());
        appendStat(buf, Attributes.SUM, getSum());
        appendStat(buf, Attributes.ARITHMETIC_MEAN, getArithmeticMean());
        appendStat(buf, Attributes.GEOMETRIC_MEAN, getGeometricMean());
        appendStat(buf, Attributes.HARMONIC_MEAN, getHarmonicMean());
        appendStat(buf, Attributes.QUADRATIC_MEAN, getQuadraticMean());
        appendStat(buf, Attributes.STANDARD_DEVIATION, getStandardDeviation());
    }

}
