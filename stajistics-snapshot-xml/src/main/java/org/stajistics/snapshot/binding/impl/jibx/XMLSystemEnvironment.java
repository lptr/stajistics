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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.stajistics.snapshot.binding.SystemEnvironment;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class XMLSystemEnvironment implements SystemEnvironment {

    private static Logger logger = Logger.getLogger(XMLSystemEnvironment.class.getName());

    private String hostName;
    private HashMap<String,String> properties;

    public XMLSystemEnvironment() {
        try {
            hostName = InetAddress.getLocalHost()
                                  .getHostName();
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Failed to get host name");
        }

        Properties sysProps = System.getProperties();

        properties = new HashMap<String,String>(sysProps.size());
        for (Map.Entry<Object,Object> entry : sysProps.entrySet()) {
            properties.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    private XMLSystemEnvironment(final boolean dummy) {}

    public static XMLSystemEnvironment factory() {
        return new XMLSystemEnvironment(false);
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    @Override
    public Map<String,String> getProperties() {
        if (properties == null) {
            properties = new HashMap<String,String>();
        }
        return properties;
    }

    @Override
    public int hashCode() {
        return hostName.hashCode() ^
            properties.hashCode();
    }

    public boolean equals(final Object obj) {
        return (obj instanceof XMLSystemEnvironment) && equals((XMLSystemEnvironment)obj);
    }

    public boolean equals(final XMLSystemEnvironment other) {
        if (!hostName.equals(other.hostName)) {
            return false;
        }

        if ((properties == null || properties.isEmpty()) &&
            (other.properties == null || other.properties.isEmpty())) {
            return true;
        }

        return properties.equals(other.properties);
    }
}
