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
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface DataSet extends DataContainer,Serializable {

    /**
     * Common field names
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

    MetaData getMetaData();

    MetaDataSet getFieldMetaDataSet();

}
