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
public interface DataSet extends DataContainer,Serializable {

    /**
     * Common field names.
     */
    interface Field {
        public static final String HITS = "hits";
        public static final String FIRST_HIT_STAMP = "firstHitStamp";
        public static final String LAST_HIT_STAMP = "lastHitStamp";
        public static final String COMMITS = "commits";
        public static final String FIRST = "first";
        public static final String LAST = "last";
        public static final String MIN = "min";
        public static final String MAX = "max";
        public static final String SUM = "sum";

        /**
         * Default values for common field names. 
         */
        interface Default {
            public static final Long HITS = 0L;
            public static final Long FIRST_HIT_STAMP = -1L;
            public static final Long LAST_HIT_STAMP = -1L;
            public static final Long COMMITS = 0L;
            public static final Double FIRST = Double.NaN;
            public static final Double LAST = Double.NaN;
            public static final Double MIN = Double.NaN;
            public static final Double MAX = Double.NaN;
            public static final Double SUM = 0D;
        }
    }

    /**
     * Obtain a {@link MetaData} instance which contains meta data related to this DataSet.
     *
     * @return A {@link MetaData} instance, never <tt>null</tt>.
     */
    MetaData getMetaData();

    /**
     * Obtain a {@link FieldMetaDataSet} instance which provides access to meta data associated with
     * individual fields of this DataSet.
     *
     * @return A {@link FieldMetaDataSet} instance, never <tt>null</tt>.
     */
    FieldMetaDataSet getFieldMetaDataSet();

}
