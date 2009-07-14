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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.stajistics.snapshot.binding.SystemEnvironment;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class SystemEnvironmentImpl implements SystemEnvironment {

    private String systemName = ""; // TODO

    private HashMap<String,String> properties;

    public SystemEnvironmentImpl() {
        Properties sysProps = System.getProperties();

        properties = new HashMap<String,String>(sysProps.size());
        for (Map.Entry<Object,Object> entry : sysProps.entrySet()) {
            properties.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    private SystemEnvironmentImpl(boolean dummy) {
        //TODO: need to not overwrite unmarshalled properties with current system properties
    }

    public static SystemEnvironmentImpl factory() {
        return new SystemEnvironmentImpl(false);
    }

    @Override
    public String getSystemName() {
        return systemName;
    }

    @Override
    public void setSystemName(final String systemName) {
        this.systemName = systemName;
    }

    @Override
    public Map<String,String> getProperties() {
        return properties;
    }
}
