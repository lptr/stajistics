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

/**
 * Provides access to {@link MetaData} instances which are associated with single field names.
 * The associated field names are typically those of a {@link DataSet} instance.
 *
 * @see DataSet#getFieldMetaDataSet()
 * 
 * @author The Stajistics Project
 */
public interface FieldMetaDataSet {

    /**
     * Obtain the {@link MetaData} instance for the given <tt>fieldName</tt>.
     *
     * @param fieldName The fieldName for which to return a {@link MetaData} instance. 
     *                  Must not be <tt>null</tt>.
     * @return A {@link MetaData} instance containing meta data associated <tt>fieldName</tt>, 
     *         never <tt>null</tt>.
     */
    MetaData getMetaData(String fieldName);

    /**
     * Clear all meta data associated with all known fields.
     */
    void clear();
}
