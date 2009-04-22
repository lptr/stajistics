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


/**
 * A builder for immutable {@link StatsKey} instances.
 *
 * @author The Stajistics Project
 */
public interface StatsKeyBuilder {

    /**
     * Append a hierarchy suffix onto the name of the key being built.
     *
     * @param nameSuffix The name suffix to append.
     * @return <tt>this</tt>.
     */
    StatsKeyBuilder withNameSuffix(String nameSuffix);

    /**
     * Add the given String attribute.
     *
     * @param name The name of the attribute to add.
     * @param value The attribute value. Must not be <tt>null</tt>.
     * @return <tt>this</tt>.
     */
    StatsKeyBuilder withAttribute(String name, String value);

    /**
     * Add the given Boolean attribute.
     *
     * @param name The name of the attribute to add.
     * @param value The attribute value. Must not be <tt>null</tt>.
     * @return <tt>this</tt>.
     */
    StatsKeyBuilder withAttribute(String name, Boolean value);

    /**
     * Add the given Integer attribute.
     *
     * @param name The name of the attribute to add.
     * @param value The attribute value. Must not be <tt>null</tt>.
     * @return <tt>this</tt>.
     */
    StatsKeyBuilder withAttribute(String name, Integer value);

    /**
     * Add the given Long attribute.
     *
     * @param name The name of the attribute to add.
     * @param value The attribute value. Must not be <tt>null</tt>.
     * @return <tt>this</tt>.
     */
    StatsKeyBuilder withAttribute(String name, Long value);

    /**
     * Remove the attribute for the given <tt>name</tt>. Does nothing if the attribute
     * does not exist.
     *
     * @param name The name of the attribute to remove.
     * @return <tt>this</tt>.
     */
    StatsKeyBuilder withoutAttribute(String name);

    /**
     * Create a new immutable {@link StatsKey} instance based on the builders currently 
     * configured state.
     *
     * @return  A {@link StatsKey} instance, never <tt>null</tt>.
     */
    StatsKey newKey();

}
