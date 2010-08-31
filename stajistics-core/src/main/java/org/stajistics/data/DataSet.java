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
package org.stajistics.data;

import java.io.Serializable;

/**
 * <p>Stores statistical data and meta data as a mapping of key-value pairs known as fields. 
 * Statistical data is stored as fields, while meta data that is not directly related to
 * statistics can be stored at the scope of the DataSet instance, as well as at the scope
 * of a single field of the DataSet instance.</p>
 *
 * <p>DataSet implementations are generally optimized for reduced memory consumption
 * and fast reads and writes of single fields, while iteration of all fields or meta data
 * fields may be less performant than a regular O(n) iteration.</p>
 *
 * @author The Stajistics Project
 */
public interface DataSet extends DataContainer, Serializable {
    
    public static final double UNINITIALIZED_VALUE = Double.NaN;
    public static final long UNINITIALIZED_TIMESTAMP = -1L;

    public enum StandardField implements Field {
        hits(0L),
        firstHitStamp(UNINITIALIZED_TIMESTAMP),
        lastHitStamp(UNINITIALIZED_TIMESTAMP),
        commits(0L),
        first(UNINITIALIZED_VALUE),
        last(UNINITIALIZED_VALUE),
        min(UNINITIALIZED_VALUE),
        max(UNINITIALIZED_VALUE),
        sum(0.0D);

        private final Type type;
        private final Object defaultValue;

        StandardField(long defaultValue) {
            this.type = Type.LONG;
            this.defaultValue = defaultValue;
        }

        StandardField(double defaultValue) {
            this.type = Type.DOUBLE;
            this.defaultValue = defaultValue;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }

    }

    public enum StandardMetaField implements Field {
        collectionStamp(UNINITIALIZED_TIMESTAMP),
        drainedSession(0L);

        private final Type type;
        private final Object defaultValue;

        StandardMetaField(long defaultValue) {
            this.type = Type.LONG;
            this.defaultValue = defaultValue;
        }

        StandardMetaField(double defaultValue) {
            this.type = Type.DOUBLE;
            this.defaultValue = defaultValue;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }

    }
}
