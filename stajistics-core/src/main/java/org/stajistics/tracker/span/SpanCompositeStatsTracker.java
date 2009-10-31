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
package org.stajistics.tracker.span;

import java.util.List;

import org.stajistics.tracker.AbstractCompositeStatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class SpanCompositeStatsTracker extends AbstractCompositeStatsTracker<SpanTracker> 
    implements SpanTracker {

    private static final long serialVersionUID = -5248420844132582820L;

    public SpanCompositeStatsTracker(final SpanTracker... trackers) {
        super(trackers);
    }

    public SpanCompositeStatsTracker(final List<SpanTracker> trackers) { 
        super(trackers);
    }

    @Override
    public SpanTracker start() {
        int len = trackers.length;
        for (int i = 0; i < len; i++) {
            trackers[i].start();
        }
        return this;
    }

    @Override
    public SpanTracker stop() {
        int len = trackers.length;
        for (int i = 0; i < len; i++) {
            trackers[i].stop();
        }
        return this;
    }

    @Override
    public boolean isTracking() {
        int len = trackers.length;
        for (int i = 0; i < len; i++) {
            if (trackers[i].isTracking()) {
                return true;
            }
        }
        return false;
    }
}
