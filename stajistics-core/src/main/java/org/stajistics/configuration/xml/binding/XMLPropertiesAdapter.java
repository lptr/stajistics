package org.stajistics.configuration.xml.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

class XMLPropertiesAdapter extends XmlAdapter<XMLProperties,HashMap<String,String>> {

    @Override
    public XMLProperties marshal(final HashMap<String,String> v) throws Exception {
        List<XMLProperty> propertyList = new ArrayList<XMLProperty>(v.size());

        for (Map.Entry<String,String> entry : v.entrySet()) {
            propertyList.add(new XMLProperty(entry.getKey().toString(), entry.getValue().toString()));
        }

        return new XMLProperties(propertyList);
    }

    @Override
    public HashMap<String,String> unmarshal(final XMLProperties v) throws Exception {
        int size = (v.propertyList == null) ? 0 : v.propertyList.size();
        HashMap<String,String> map = new HashMap<String,String>(size);

        for (XMLProperty property : v.propertyList) {
            map.put(property.name, property.value);
        }

        return map;
    }
}
