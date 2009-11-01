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
package org.stajistics.examples.basic;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.tracker.span.SpanTracker;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class MethodTimeDuration implements Runnable {

    public void run() {

        StatsKey key = Stats.newKey("myMethod");

        for (int i = 0; i < 20; i++) {

            SpanTracker tracker = Stats.start(key);

            myMethod();

            tracker.stop();
        }

        System.out.println(Stats.getSessionManager().getSession(key));
    }

    private void myMethod() {
        for (int i = 0; i < 1000000; i++) {
            String.valueOf(i);
        }
    }

    public static void main(String[] args) {
        new MethodTimeDuration().run();
    }

}
