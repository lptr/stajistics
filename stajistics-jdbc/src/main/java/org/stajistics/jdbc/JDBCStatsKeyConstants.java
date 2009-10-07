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
package org.stajistics.jdbc;

import java.beans.Statement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.stajistics.Stats;
import org.stajistics.StatsKey;

/**
 * 
 * @author The Stajistics Project
 *
 */
public interface JDBCStatsKeyConstants {

    StatsKey PARENT = Stats.newKey(Connection.class
                                             .getPackage()
                                             .getName());

    StatsKey CONNECTION = Stats.newKey(Connection.class.getName());
    StatsKey CALLABLE_STATEMENT = Stats.newKey(CallableStatement.class.getName());
    StatsKey PREPARED_STATEMENT = Stats.newKey(PreparedStatement.class.getName());
    StatsKey RESULT_SET = Stats.newKey(ResultSet.class.getName());
    StatsKey STATEMENT = Stats.newKey(Statement.class.getName());

}
