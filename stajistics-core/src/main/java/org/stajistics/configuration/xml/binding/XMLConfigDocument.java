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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author The Stajistics Project
 */
@XmlRootElement(name = "stajistics")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLConfigDocument {

    @XmlElement(name = "properties", required = false)
    @XmlJavaTypeAdapter(XMLPropertiesAdapter.class)
    private HashMap properties;

    @XmlElement(name = "rootConfig", required = false)
    private XMLRootStatsConfig rootConfig;

    @XmlElement(name = "config", required = false)
    private List<XMLStatsConfig> configs;

    public Map<String,String> getProperties() {
        if (properties == null) {
            return Collections.emptyMap();
        }
        return properties;
    }

    public void setProperties(final Map<String,String> properties) {
        this.properties = new HashMap<String,String>(properties);
    }

    public XMLRootStatsConfig getRootConfig() {
        return rootConfig;
    }

    public void setRootConfig(XMLRootStatsConfig rootConfig) {
        this.rootConfig = rootConfig;
    }

    public List<XMLStatsConfig> getConfigs() {
        if (configs == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(configs);
    }

    public void setConfigs(final List<XMLStatsConfig> configs) {
        this.configs = new ArrayList<XMLStatsConfig>(configs);
    }
}
