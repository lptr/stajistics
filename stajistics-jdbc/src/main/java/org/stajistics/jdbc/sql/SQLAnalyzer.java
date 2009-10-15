package org.stajistics.jdbc.sql;

import java.util.List;

/**
 * 
 * @author The Stajistics Project
 *
 */
public interface SQLAnalyzer {

    void analyzeSQL(String sql);

    void analyzeSQL(List<String> batchSQL);

    public static final class NoOp implements SQLAnalyzer {

        private static final NoOp instance = new NoOp();

        private NoOp() {}

        public static NoOp instance() {
            return instance;
        }

        @Override
        public void analyzeSQL(final String sql) {}

        @Override
        public void analyzeSQL(final List<String> batchSQL) {}
    }
}
