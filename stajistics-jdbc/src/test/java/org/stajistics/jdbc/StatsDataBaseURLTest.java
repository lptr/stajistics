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
package org.stajistics.jdbc;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsDataBaseURLTest extends TestCase {

    private static final String[][] TEST_DATA = {
        /* Format:
         * { Unit test name,
         *   original URL,
         *   delegate URL,
         * }
         */

        { "Parameter 1",
          "jdbc:stajistics:test statsDelegateDriver=dbDriver",
          "jdbc:test" },

        { "Parameter 2",
          "jdbc:stajistics:test?statsDelegateDriver=dbDriver",
          "jdbc:test" },

        { "Parameters before 1",
          "jdbc:stajistics:test thing=stuff statsDelegateDriver=dbDriver",
          "jdbc:test thing=stuff" },

        { "Parameters before 2",
          "jdbc:stajistics:test?thing=stuff&statsDelegateDriver=dbDriver",
          "jdbc:test?thing=stuff" },

        { "Parameters after 1",
          "jdbc:stajistics:test statsDelegateDriver=dbDriver thing=stuff",
          "jdbc:test thing=stuff" },

        { "Parameters after 2",
          "jdbc:stajistics:test?statsDelegateDriver=dbDriver&thing=stuff",
          "jdbc:test?thing=stuff" },

        { "Parameters around 1",
          "jdbc:stajistics:test cool=awesome statsDelegateDriver=dbDriver thing=stuff",
          "jdbc:test cool=awesome thing=stuff" },

        { "Parameters around 2",
          "jdbc:stajistics:test?cool=awesome&statsDelegateDriver=dbDriver&thing=stuff",
          "jdbc:test?cool=awesome&thing=stuff" },

        /* VENDOR URLS */

        { "Derby",
          "jdbc:stajistics:derby:dbName statsDelegateDriver=dbDriver", 
          "jdbc:derby:dbName" },

        { "IBM Informix",
          "jdbc:stajistics:bea:informix://dbHost:dbPort:informixServer=dbHost;databaseName=dbName statsDelegateDriver=dbDriver", 
          "jdbc:bea:informix://dbHost:dbPort:informixServer=dbHost;databaseName=dbName" },

        { "JDBC-ODBC",
          "jdbc:stajistics:odbc:dbDSN;UID=dbUser;PWD=dbPass statsDelegateDriver=dbDriver", 
          "jdbc:odbc:dbDSN;UID=dbUser;PWD=dbPass" },

        { "Microsoft SQL Server",
           "jdbc:stajistics:sqlserver://dbHost:dbPort;database=dbName statsDelegateDriver=dbDriver", 
           "jdbc:sqlserver://dbHost:dbPort;database=dbName" },

        { "MySQL",
          "jdbc:stajistics:mysql://dbHost:dbPort/dbName statsDelegateDriver=dbDriver", 
          "jdbc:mysql://dbHost:dbPort/dbName" },

        { "Oracle < 10g",
          "jdbc:stajistics:oracle:thin:@dbHost:dbPort:dbName statsDelegateDriver=dbDriver", 
          "jdbc:oracle:thin:@dbHost:dbPort:dbName" },

        { "Oracle >= 10g",
          "jdbc:stajistics:oracle:thin:@//dbHost:dbPort/dbName statsDelegateDriver=dbDriver", 
          "jdbc:oracle:thin:@//dbHost:dbPort/dbName" },

        { "PostgreSQL",
          "jdbc:stajistics:postgresql://dbHost/dbName statsDelegateDriver=dbDriver", 
          "jdbc:postgresql://dbHost/dbName" },

        { "Sybase SQL Server",
          "jdbc:stajistics:sybase:Tds:dbHost:dbPort/ statsDelegateDriver=dbDriver", 
          "jdbc:sybase:Tds:dbHost:dbPort/" }
    };

    private final String originalURL;
    private final String delegateURL;

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();

        for (int i = 0; i < TEST_DATA.length; i++) {
            suite.addTest(new StatsDataBaseURLTest(TEST_DATA[i][0],
                                                   TEST_DATA[i][1],
                                                   TEST_DATA[i][2]));
        }

        return suite;
    }

    public StatsDataBaseURLTest(final String testName,
                                final String originalURL,
                                final String delegateURL) {
        super(testName);

        if (originalURL == null) {
            throw new NullPointerException("originalURL");
        }
        if (delegateURL == null) {
            throw new NullPointerException("delegateURL");
        }

        this.originalURL = originalURL;
        this.delegateURL = delegateURL;
    }

    @Override
    protected void runTest() throws Throwable {

        assertTrue(StatsDataBaseURL.isSupported(originalURL));
        assertFalse(StatsDataBaseURL.isSupported(delegateURL));

        StatsDataBaseURL testURL = new StatsDataBaseURL(originalURL);

        assertEquals(originalURL, testURL.getOriginalURL());
        assertEquals("dbDriver", testURL.getDelegateDriverClassName());
        assertEquals(delegateURL, testURL.getDelegateURL());

        // Ensure a URL not starting with "jdbc:stajistics" is rejected
        try {
            new StatsDataBaseURL(delegateURL);
        } catch (StatsDataBaseURL.FormatException e) {
            // expected
        }
    }
}
