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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author The Stajistics Project
 */
public class DefaultMetaDataTest extends AbstractDataContainerTestCase {

    private static final String FIELD_NAME = "test";

    private Map<String,Object> map;

    @Override
    protected DataContainer createDataContainer() {
        map = new HashMap<String,Object>();
        return new DefaultFieldMetaDataSet(map).getMetaData(FIELD_NAME);
    }

    @Override
    protected MetaData dc() {
        return (MetaData)super.dc();
    }

}
