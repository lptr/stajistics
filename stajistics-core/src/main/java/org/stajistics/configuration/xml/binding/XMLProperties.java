package org.stajistics.configuration.xml.binding;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

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
