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
 *
 */
public abstract class AbstractStatsSnapshot implements StatsSnapshot {

    @Override
    public int hashCode() {
        return getSchemaVersion().hashCode() ^
               getStajisticsVersion().hashCode() ^
               getStartTimeStamp().hashCode() ^
               getEndTimeStamp().hashCode() ^
               getApplicationEnvironment().hashCode() ^
               getSystemEnvironment().hashCode() ^
               getSessionSnapshots().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (obj instanceof StatsSnapshot) && equals((StatsSnapshot)obj);
    }

    public boolean equals(final StatsSnapshot other) {
        if (!getSchemaVersion().equals(other.getSchemaVersion())) {
            return false;
        }
        if (!getStajisticsVersion().equals(other.getStajisticsVersion())) {
            return false;
        }
        if (!getStartTimeStamp().equals(other.getStartTimeStamp())) {
            return false;
        }
        if (!getEndTimeStamp().equals(other.getEndTimeStamp())) {
            return false;
        }
        if (!getApplicationEnvironment().equals(other.getApplicationEnvironment())) {
            return false;
        }
        if (!getSystemEnvironment().equals(other.getSystemEnvironment())) {
            return false;
        }
        if (!getSessionSnapshots().equals(other.getSessionSnapshots())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(StatsSnapshot.class.getSimpleName());
        buf.append("[applicationEnvironment=");
        buf.append(getApplicationEnvironment());
        buf.append(",endTimeStamp=");
        buf.append(getEndTimeStamp());
        buf.append(",schemaVersion=");
        buf.append(getSchemaVersion());
        buf.append(",sessions=");
        buf.append(getSessionSnapshots());
        buf.append(",stajisticsVersion=");
        buf.append(getStajisticsVersion());
        buf.append(",startTimeStamp=");
        buf.append(getStartTimeStamp());
        buf.append(",systemEnvironment=");
        buf.append(getSystemEnvironment());
        buf.append(']');
        return buf.toString();
    }


}
