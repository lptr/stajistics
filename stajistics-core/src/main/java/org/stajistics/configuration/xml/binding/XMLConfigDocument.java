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

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * @author The Stajistics Project
 */
@XmlRootElement(name = "stajistics")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLConfigDocument {

    @XmlElement(name = "property", required = false)
    private List<XMLProperty> properties;

    @XmlElement(name = "rootConfig", required = false)
    private XMLRootStatsConfig rootConfig;

    @XmlElement(name = "config", required = false)
    private List<XMLStatsConfig> configs;

    public List<XMLProperty> getProperties() {
        return properties;
    }

    public Map<String,String> getPropertiesMap() {
        Map<String,String> result = new HashMap<String,String>(configs.size());
        for (XMLProperty prop : properties) {
            result.put(prop.getName(), prop.getValue());
        }
        return result;
    }

    public void setProperties(List<XMLProperty> properties) {
        this.properties = properties;
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
