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
package org.stajistics;

import java.io.Serializable;


/**
 * A factory for creating {@link StatsKey}s and {@link StatsKeyBuilder}s.
 *
 * @author The Stajistics Project
 */
public interface StatsKeyFactory extends Serializable {

   /**
    * Create a new {@link StatsKey} from the given <tt>name</tt>.
    *
    * @param name The name of the key to create.
    * @return A new {@link StatsKey}, never <tt>null</tt>.
    */
   StatsKey createKey(String name);

   /**
    * Create a new {@link StatsKeyBuilder} which can create a new {@link StatsKey}
    * for the given <tt>name</tt>.
    *
    * @param name The name of the key that the builder will create.
    * @return A {@link StatsKeyBuilder} which can be used to define key attributes, never <tt>null</tt>.
    */
   StatsKeyBuilder createKeyBuilder(String name);

   /**
    * Create a new {@link StatsKeyBuilder} which can create a new {@link StatsKey}.
    * The builder is initialized with the name and attributes of the given {@link StatsKey}
    * <tt>template</tt>.
    *
    * @param template The key with which to initialize the {@link StatsKeyBuilder}.
    * @return A {@link StatsKeyBuilder} which can be used to define key attributes, never <tt>null</tt>.
    */
   StatsKeyBuilder createKeyBuilder(StatsKey template);
    
}
