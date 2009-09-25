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
package org.stajistics.snapshot.binding.impl.jibx;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.stajistics.Stajistics;
import org.stajistics.StatsKey;
import org.stajistics.snapshot.binding.AbstractStatsSnapshot;
import org.stajistics.snapshot.binding.ApplicationEnvironment;
import org.stajistics.snapshot.binding.SessionSnapshot;
import org.stajistics.snapshot.binding.SystemEnvironment;

/**
 * 
 * @author The Stajistics Project
 */
@SuppressWarnings("unchecked")
public class XMLStatsSnapshot extends AbstractStatsSnapshot {

    private String schemaVersion = "1.0";
    private String stajisticsVersion = Stajistics.getVersion();
    private Date startTimeStamp = new Date();
    private Date endTimeStamp = new Date();

    private XMLApplicationEnvironment applicationEnvironment = new XMLApplicationEnvironment();
    private XMLSystemEnvironment systemEnvironment = new XMLSystemEnvironment();

    private HashMap sessions = new HashMap();

    @Override
    public String getSchemaVersion() {
        return schemaVersion;
    }

    void setSchemaVersion(final String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    @Override
    public String getStajisticsVersion() {
        return stajisticsVersion;
    }

    @Override
    public Date getStartTimeStamp() {
        return startTimeStamp;
    }

    @Override
    public Date getEndTimeStamp() {
        return endTimeStamp;
    }

    @Override
    public void setTimeStamp(final Date timeStamp) {
        this.startTimeStamp = timeStamp;
    }

    @Override
    public SystemEnvironment getSystemEnvironment() {
        if (systemEnvironment == null) {
            systemEnvironment = new XMLSystemEnvironment();
        }
        return systemEnvironment;
    }

    @Override
    public ApplicationEnvironment getApplicationEnvironment() {
        if (applicationEnvironment == null) {
            applicationEnvironment = new XMLApplicationEnvironment();
        }
        return applicationEnvironment;
    }

    public Map<StatsKey,SessionSnapshot> getSessionSnapshots() {
        return Collections.unmodifiableMap(sessions);
    }

    @Override
    public void setSessionSnapshots(final Map<StatsKey,SessionSnapshot> sessions) {
        if (sessions instanceof HashMap) { 
            this.sessions = (HashMap)sessions;
        } else {
            this.sessions = new HashMap(sessions);
        }
    }
}