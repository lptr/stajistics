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
package org.stajistics.management;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.Stats;
import org.stajistics.session.data.DataSet;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsSession implements StatsSessionMBean,DynamicMBean {

    private static final String OP_CLEAR = "clear";
    private static final String OP_DESTROY = "destroy";
    private static final String OP_DUMP = "dump";

    private final org.stajistics.session.StatsSession session;

    public StatsSession(final org.stajistics.session.StatsSession session) {
        if (session == null) {
            throw new NullPointerException("session");
        }

        this.session = session;
    }

    @Override
    public Object getAttribute(final String attribute)
            throws AttributeNotFoundException,MBeanException,ReflectionException {
        DataSet dataSet = session.collectData();
        Object value = dataSet.getField(attribute);
        if (value == null) {
            throw new AttributeNotFoundException(attribute);
        }
        return value;
    }

    @Override
    public AttributeList getAttributes(final String[] attributes) {
        AttributeList attrList = new AttributeList();
        DataSet dataSet = session.collectData();
        for (String name : attributes) {
            attrList.add(new Attribute(name, dataSet.getField(name)));
        }
        return attrList;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        DataSet dataSet = session.collectData();

        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[dataSet.size()];

        int i = 0;
        for (String name : dataSet.getFieldNames()) {
            Object value = dataSet.getField(name);
            attrs[i++] = new MBeanAttributeInfo(name,
                                                value.getClass().getName(),
                                                "DataSet field " + name,
                                                true,
                                                false,
                                                false);
        }

        MBeanOperationInfo[] ops = {
            new MBeanOperationInfo(OP_CLEAR,
                                   "Clear collected data",
                                   null,
                                   "void",
                                   MBeanOperationInfo.ACTION),
            new MBeanOperationInfo(OP_DESTROY,
                                   "Destroy this session",
                                   null,
                                   "void",
                                   MBeanOperationInfo.ACTION),
            new MBeanOperationInfo(OP_DUMP,
                                   "Dump session data to log",
                                   null,
                                   "void",
                                   MBeanOperationInfo.ACTION)
        };

        return new MBeanInfo(getClass().getName(),
                             StatsSessionMBean.class.getSimpleName(),
                             attrs,
                             null,
                             ops,
                             null);
    }

    @Override
    public Object invoke(final String actionName, 
                         final Object[] params, 
                         final String[] signature)
            throws MBeanException, ReflectionException {

        if (actionName.equals(OP_CLEAR)) {
            session.clear();

        } else if (actionName.equals(OP_DESTROY)) {
            Stats.getSessionManager().remove(session);

        } else if (actionName.equals(OP_DUMP)) {
            Logger logger = LoggerFactory.getLogger(SessionManager.SESSION_DUMP_LOGGER_NAME);
            if (logger.isInfoEnabled()) {
                logger.info(session.toString());
            }
        }

        return null;
    }

    @Override
    public void setAttribute(final Attribute attribute)
            throws AttributeNotFoundException,InvalidAttributeValueException,MBeanException,ReflectionException {

    }

    @Override
    public AttributeList setAttributes(final AttributeList attributes) {
        return null;
    }


}
