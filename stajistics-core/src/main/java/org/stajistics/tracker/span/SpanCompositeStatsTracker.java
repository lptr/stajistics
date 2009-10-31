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
        return null;
    }

    @Override
    public SpanTracker stop() {
        return null;
    }

    @Override
    public boolean isTracking() {
        return false;
    }
}
