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
package org.stajistics.integration.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stajistics.DefaultStatsFactory;
import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.session.StatsSession;

/**
 *
 * @author The Stajistics Project
 */
public class StatsFilterTest {

    private MockHttpServletRequest req;
    private MockHttpServletResponse res;
    private MockFilterChain chain;
    private MockFilterConfig config;

    private StatsFactory statsFactory;
    private StatsFilter statsFilter;

    @Before
    public void setUp() {
        res = new MockHttpServletResponse();
        chain = new MockFilterChain();
        config = new MockFilterConfig();

        statsFactory = new DefaultStatsFactory(new DefaultStatsManagerFactory().createManager(StatsConstants.DEFAULT_NAMESPACE));

        statsFilter = new StatsFilter();
    }

    @Test
    public void testHit() throws Exception {
        req = new MockHttpServletRequest("GET", "/");

        StatsKey key = statsFactory.newKey("test");

        config.addInitParameter(StatsFilter.INIT_PARAM_KEY_NAME, key.getName());
        statsFilter.init(config);
        statsFilter.doFilter(req, res, chain);

        StatsSession session = statsFactory.getManager().getSessionManager().getSession(key);

        assertEquals(1, session.getHits());
        assertEquals(1, session.getCommits());
    }

    @Test
    public void testBindParameters() throws Exception {
        req = new MockHttpServletRequest("GET", "/");
        req.addParameter("testParam", "true");

        StatsKey key = statsFactory.newKey("test");
        StatsKey paramKey = key.buildCopy()
                               .withAttribute(StatsFilter.KEY_ATTR_BINDING, StatsFilter.KEY_ATTR_BINDING_PARAM)
                               .withAttribute("testParam", "true")
                               .newKey();

        config.addInitParameter(StatsFilter.INIT_PARAM_KEY_NAME, key.getName());
        config.addInitParameter(StatsFilter.INIT_PARAM_BIND_PARAMS, "testParam");
        statsFilter.init(config);
        statsFilter.doFilter(req, res, chain);

        StatsSession session = statsFactory.getManager().getSessionManager().getSession(key);

        assertEquals(1, session.getHits());
        assertEquals(1, session.getCommits());

        StatsSession paramSession = statsFactory.getManager().getSessionManager().getSession(paramKey);

        assertEquals(1, paramSession.getHits());
        assertEquals(1, paramSession.getCommits());
    }

    @Test
    public void testBindHeaders() throws Exception {
        req = new MockHttpServletRequest("GET", "/");
        req.addHeader("testHeader", "true");

        StatsKey key = statsFactory.newKey("test");
        StatsKey headerKey = key.buildCopy()
                                .withAttribute(StatsFilter.KEY_ATTR_BINDING, StatsFilter.KEY_ATTR_BINDING_HEADER)
                                .withAttribute("testHeader", "true")
                                .newKey();

        config.addInitParameter(StatsFilter.INIT_PARAM_KEY_NAME, key.getName());
        config.addInitParameter(StatsFilter.INIT_PARAM_BIND_HEADERS, "testHeader");
        statsFilter.init(config);
        statsFilter.doFilter(req, res, chain);

        StatsSession session = statsFactory.getManager().getSessionManager().getSession(key);

        assertEquals(1, session.getHits());
        assertEquals(1, session.getCommits());

        StatsSession headerSession = statsFactory.getManager().getSessionManager().getSession(headerKey);

        assertEquals(1, headerSession.getHits());
        assertEquals(1, headerSession.getCommits());
    }

    // TODO: testExceptionIncidents

    // TODO: testResponseCodes
}
