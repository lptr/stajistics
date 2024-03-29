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
package org.stajistics.tracker.incident;

import java.util.List;

import org.stajistics.tracker.AbstractCompositeTracker;

/**
 *
 *
 * @author The Stajistics Project
 */
public class CompositeIncidentTracker
    extends AbstractCompositeTracker<IncidentTracker>
    implements IncidentTracker {

    public CompositeIncidentTracker(final IncidentTracker... trackers) {
        super(trackers);
    }

    public CompositeIncidentTracker(final List<IncidentTracker> trackers) {
        super(trackers);
    }

    @Override
    public IncidentTracker incident() {
        int len = trackers.length;
        for (int i = 0; i < len; i++) {
            trackers[i].incident();
        }
        return this;
    }
}
