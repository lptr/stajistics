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
package org.stajistics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.ConcurrentAccessTracker;
import org.stajistics.tracker.ManualTracker;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.TimeDurationTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsTrackerFactory implements StatsTrackerFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends StatsTracker> T createStatsTracker(final StatsSession statsSession,
                                                         final Class<T> trackerClass) {
        if (statsSession == null) {
            throw new NullPointerException("statsSession");
        }

        if (trackerClass == null) {
            throw new NullPointerException("trackerClass");
        }

        if (trackerClass == TimeDurationTracker.class) {
            return (T)new TimeDurationTracker(statsSession);
        }

        if (trackerClass == ConcurrentAccessTracker.class) {
            return (T)new ConcurrentAccessTracker(statsSession);
        }

        if (trackerClass == ManualTracker.class) {
            return (T)new ManualTracker(statsSession);
        }

        try {
            Constructor<StatsTracker> ctor = (Constructor<StatsTracker>)trackerClass.getConstructor(new Class[] { StatsSession.class });

            return (T)ctor.newInstance(new Object[] { statsSession });

        } catch (SecurityException e) {
            throw new RuntimeException(e);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(trackerClass.getSimpleName() + 
                                       " must define a public constructor accepting a single " + 
                                       StatsSession.class.getSimpleName() + 
                                       " parameter", e);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(trackerClass.getSimpleName() + 
                                       " must define a public constructor accepting a single " + 
                                       StatsSession.class.getSimpleName() + 
                                       " parameter", e);

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }

            throw new RuntimeException(cause);
        }
    }
}
