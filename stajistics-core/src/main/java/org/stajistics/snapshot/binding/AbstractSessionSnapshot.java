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
package org.stajistics.snapshot.binding;


/**
 * 
 * @author The Stajistics Project
 */
public abstract class AbstractSessionSnapshot implements SessionSnapshot {

    @Override
    public int hashCode() {
        return getKey().hashCode() ^
            getTrackerFactoryClassName().hashCode() ^
            getSessionFactoryClassName().hashCode() ^
            getDataRecorderClassNames().hashCode() ^
            getUnit().hashCode() ^
            getDataSet().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (obj instanceof SessionSnapshot) && equals((SessionSnapshot)obj);
    }

    public boolean equals(final SessionSnapshot other) {
        if (!getKey().equals(other.getKey())) {
            return false;
        }
        if (!getTrackerFactoryClassName().equals(other.getTrackerFactoryClassName())) {
            return false;
        }
        if (!getSessionFactoryClassName().equals(other.getSessionFactoryClassName())) {
            return false;
        }
        if (!getDataRecorderClassNames().equals(other.getDataRecorderClassNames())) {
            return false;
        }
        if (!getUnit().equals(other.getUnit())) {
            return false;
        }
        if (!getDataSet().equals(other.getDataSet())) {
            return false;
        }
        return true;
    }
}
