package org.stajistics.configuration.xml.binding;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author The Stajistics Project
 */
class XMLProperties {

    @XmlElement(name = "properties", required = false)
    List<XMLProperty> propertyList;

    XMLProperties() {}

    XMLProperties(final List<XMLProperty> propertyList) {
        this.propertyList = propertyList;
    }

}
