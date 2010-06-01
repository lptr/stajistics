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
package org.stajistics.configuration.xml.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author The Stajistics Project
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractXMLStatsConfig {

    @XmlAttribute(name = "unit", required = false)
    private String unit;

    @XmlAttribute(name = "enabled", required = false)
    private boolean enabled = true;

    @XmlElement(name = "description", required = false)
    private String description;

    @XmlElement(name = "trackerFactory", required = false)
    private String trackerFactoryClass;

    @XmlElement(name = "sessionFactory", required = false)
    private String sessionFactoryClass;

    @XmlElement(name = "dataRecorderFactory", required = false)
    private String dataRecorderFactoryClass;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrackerFactoryClass() {
        return trackerFactoryClass;
    }

    public void setTrackerFactoryClass(String trackerFactoryClass) {
        this.trackerFactoryClass = trackerFactoryClass;
    }

    public String getSessionFactoryClass() {
        return sessionFactoryClass;
    }

    public void setSessionFactoryClass(String sessionFactoryClass) {
        this.sessionFactoryClass = sessionFactoryClass;
    }

    public String getDataRecorderFactoryClass() {
        return dataRecorderFactoryClass;
    }

    public void setDataRecorderFactoryClass(String dataRecorderFactoryClass) {
        this.dataRecorderFactoryClass = dataRecorderFactoryClass;
    }
}
