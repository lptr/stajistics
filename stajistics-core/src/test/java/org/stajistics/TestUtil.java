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

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Ignore;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@Ignore
public abstract class TestUtil {

    private TestUtil() {}

    public static void buildStatsKeyExpectations(final Mockery mockery,
                                                 final StatsKey mockKey,
                                                 final String keyName) {
        buildStatsKeyExpectations(mockery, mockKey, keyName, null, null);
    }

    public static void buildStatsKeyExpectations(final Mockery mockery,
                                                 final StatsKey mockKey,
                                                 final String keyName,
                                                 final String attrName,
                                                 final String attrValue) {
        mockery.checking(new Expectations() {{
            ignoring(mockKey).getName(); will(returnValue(keyName));
            ignoring(mockKey).getAttribute(with((String)null)); will(returnValue(null));

            if (attrName == null) {
                ignoring(mockKey).getAttribute((String)with(anything())); will(returnValue(null));
                ignoring(mockKey).getAttributeCount(); will(returnValue(0));
                ignoring(mockKey).getAttributes(); will(returnValue(Collections.emptyMap()));
            } else {
                ignoring(mockKey).getAttribute(with(attrName)); will(returnValue(attrValue));
                ignoring(mockKey).getAttributeCount(); will(returnValue(1));
                ignoring(mockKey).getAttributes(); will(returnValue(Collections.singletonMap(attrName, attrValue)));
            }
        }});
    }
                                                     

}
