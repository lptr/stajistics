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
package org.stajistics.util.xml;

import java.io.*;

/**
 * @author The Stajistics Project
 */
public interface XMLBindingManager<T> {

    public static final String DEFAULT_ENCODING = "UTF-8";

    String getEncoding();

    void setEncoding(String encoding);

    T unmarshal(String in);

    T unmarshal(File in);

    T unmarshal(Reader in);

    T unmarshal(InputStream in);

    String marshal(T binding);

    void marshal(T binding, OutputStream out);

    void marshal(T binding, Writer out);

    void marshal(T binding, File out);

}
