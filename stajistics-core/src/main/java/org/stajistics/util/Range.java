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
package org.stajistics.util;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class Range {

    private final double begin;
    private final double end;

    private final String name;

    public Range(final double begin,
                 final double end) {
        this(begin, end, null);
    }

    public Range(final double begin,
                 final double end,
                 final String name) {

        if (begin > end) {
            throw new IllegalArgumentException("begin must be <= end");
        }

        this.begin = begin;
        this.end = end;

        if (name == null) {
            this.name = formatDefaultName(begin, end);
        } else {
            this.name = name;
        }
    }

    private String formatDefaultName(final double begin,
                                     final double end) {
        //TODO: decimal format
        return "range(" + begin + "-" + end + ")";
    }
    
    public double getBegin() {
        return begin;
    }

    public double getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public boolean contains(final double value,
                            final boolean exclusiveRangeEnd) {
        if (value < begin) {
            return false;
        }

        if (exclusiveRangeEnd) {
            if (value >= end) {
                return false;
            }
        } else {
            if (value > end) {
                return false;
            }
        }

        return true;
    }

    public boolean overlaps(final Range other,
                            final boolean exclusiveRangeEnd) {
        if (begin <= other.begin) {
            if (exclusiveRangeEnd) {
                return other.begin < end;
            } else {
                return other.begin <= end;
            }
        } else if (begin <= other.end) {
            if (exclusiveRangeEnd) {
                return other.end < end;
            } else {
                return other.end <= end;
            }
        }

        return false;
    }

    public String toString() {
        return getName();
    }
}
