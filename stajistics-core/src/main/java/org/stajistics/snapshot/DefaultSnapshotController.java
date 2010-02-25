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
package org.stajistics.snapshot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.StatsProperties;
import org.stajistics.data.DataSet;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.snapshot.binding.SessionSnapshot;
import org.stajistics.snapshot.binding.StatsSnapshot;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultSnapshotController implements SnapshotController {

    private static final String DEFAULT_SNAPSHOT_BINDING_FACTORY_CLASS_NAME = 
        "org.stajistics.snapshot.binding.XMLSnapshotBindingFactory";

    private final SnapshotBindingFactory bindingFactory;

    public DefaultSnapshotController() {
        this(createDefaultSnapshotBindingFactory());
    }

    public DefaultSnapshotController(final SnapshotBindingFactory bindingFactory) {
        this.bindingFactory = bindingFactory;
    }

    private static SnapshotBindingFactory createDefaultSnapshotBindingFactory() {

        final String factoryClassName = StatsProperties.getProperty(SnapshotBindingFactory.class.getName(),
                                                                    DEFAULT_SNAPSHOT_BINDING_FACTORY_CLASS_NAME);

        SnapshotBindingFactory factory = null;

        Class<?> factoryClass;
        try {
            //factoryClass = Class.forName(factoryClassName);
            //factory = (SnapshotBindingFactory)factoryClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: log this
        }

        return factory;
    }

    @Override
    public void restoreSnapshot(final StatsManager statsManager, 
                                final StatsSnapshot snapshot) {

        StatsSessionManager sessionManager = statsManager.getSessionManager();

        Map<StatsKey,SessionSnapshot> sessionSnapshotMap = snapshot.getSessionSnapshots();
        for (SessionSnapshot sessionSnapshot : sessionSnapshotMap.values()) {
            // TODO: determine if config has changed
            DataSet dataSet = sessionSnapshot.getDataSet();
            StatsSession session = sessionManager.getSession(sessionSnapshot.getKey());
            session.restore(dataSet);
        }
    }

    @Override
    public StatsSnapshot takeSnapshot(final StatsManager statsManager) {

        StatsSnapshot snapshot = bindingFactory.createSnapshot();

        StatsSessionManager sessionManager = statsManager.getSessionManager();
        StatsConfigManager configManager = statsManager.getConfigManager();

        Map<StatsKey,SessionSnapshot> sessionSnapshotMap = new HashMap<StatsKey,SessionSnapshot>();

        for (StatsSession session : sessionManager.getSessions()) {
            StatsConfig config = configManager.getConfig(session.getKey());
            SessionSnapshot sessionSnapshot = bindingFactory.createSessionSnapshot(session, config);
            sessionSnapshotMap.put(session.getKey(), sessionSnapshot);
        }

        snapshot.setSessionSnapshots(sessionSnapshotMap);

        return snapshot;
    }

}
