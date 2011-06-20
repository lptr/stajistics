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
package org.stajistics;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public final class StatsKeyUtil {

    private StatsKeyUtil() {}

    public static String parentKeyName(final String keyName) {
        String parentKeyName = null;

        int i = keyName.lastIndexOf(StatsConstants.KEY_HIERARCHY_DELIMITER);
        if (i > -1) {
            parentKeyName = keyName.substring(0, i);
        }

        return parentKeyName;
    }

    public static StatsKey keyForFailure(final StatsKey key,
                                         final Throwable failure) {
        StatsKey failureKey = key.buildCopy()
                                 .withNameSuffix("exception")
                                 .withAttribute("threw",
                                                failure.getClass()
                                                       .getName())
                                 .newKey();
        return failureKey;
    }
}
