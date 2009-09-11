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
package org.stajistics.integration.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 
 * @author The Stajistics Project
 */
public class StatsHttpServletResponse extends HttpServletResponseWrapper {

    private int statusCode = SC_OK;

    public StatsHttpServletResponse(final HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(final int statusCode) {
        super.setStatus(statusCode);
        this.statusCode = statusCode;
    }

    public int getStatus() {
        return statusCode;
    }

    @Override
    public void reset() {
        super.reset();
        this.statusCode = SC_OK;
    }
}
