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
package org.stajistics.event.alarm.threashold;

import java.io.Serializable;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class Threashold implements Serializable {

    private final ThreasholdType type;
    private final double value;

    public Threashold(final ThreasholdType type,
                      final double value) {
        this.type = type;
        this.value = value;
    }

    public boolean isThreasholdExceeded(final double test) {
        boolean exceeded;

        if (type == ThreasholdType.GREATER_THAN) {
            exceeded = (test > value);

        } else if (type == ThreasholdType.LESS_THAN) {
            exceeded = (test < value);

        } else {
            throw new Error("wtf");
        }

        return exceeded;
    }

}