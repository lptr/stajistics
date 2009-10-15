package org.stajistics.jdbc.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class CompositeSQLAnalyzer implements SQLAnalyzer {

    private final List<SQLAnalyzer> analyzers;

    public CompositeSQLAnalyzer(final SQLAnalyzer... analyzers) {
        this(Arrays.asList(analyzers));
    }

    public CompositeSQLAnalyzer(final List<SQLAnalyzer> analyzers) {
        if (analyzers == null) {
            throw new NullPointerException("analyzers");
        }
        if (analyzers.isEmpty()) {
            throw new IllegalArgumentException("empty analyzers");
        }

        this.analyzers = Collections.unmodifiableList(new ArrayList<SQLAnalyzer>(analyzers));
    }

    @Override
    public void analyzeSQL(final String sql) {
        for (SQLAnalyzer analyzer : analyzers) {
            analyzer.analyzeSQL(sql);
        }
    }

    @Override
    public void analyzeSQL(List<String> batchSQL) {
        for (SQLAnalyzer analyzer : analyzers) {
            analyzer.analyzeSQL(batchSQL);
        }
    }
}
