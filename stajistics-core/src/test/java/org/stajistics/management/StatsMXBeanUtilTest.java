package org.stajistics.management;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.stajistics.TestUtil.buildStatsKeyExpectations;
import static org.stajistics.management.StatsMXBeanUtil.buildKeyName;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsMXBeanUtilTest extends AbstractStajisticsTestCase {

    private static final String NAMESPACE = "ns";

    private static final String TYPE_TEST = "test";
    private static final String SUBTYPE_TEST = "test";

    private static final String NORMAL = "normal";

    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
    }

    @Test
    public void testBuildNameDomain() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL);
        String name = StatsMXBeanUtil.buildKeyName(NAMESPACE, mockKey, TYPE_TEST, SUBTYPE_TEST, true);
        assertTrue(name.startsWith(StatsMXBeanUtil.STAJISTICS_DOMAIN));
    }

    /* NORMAL */

    @Test
    public void testBuildNameNormal() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameValNormal() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, NORMAL);
        assertValidObjectName();
    }

    /* QUESTION MARK */

    @Test
    public void testBuildNameWithQuestion() {
        buildStatsKeyExpectations(mockery, mockKey, "with?question");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithQuestion() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with?question", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithQuestion() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with?question");
        assertValidObjectName();
    }

    /* ASTERISK */

    @Test
    public void testBuildNameWithAsterisk() {
        buildStatsKeyExpectations(mockery, mockKey, "with*asterisk");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithAsterisk() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with*asterisk", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithAsterisk() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with*asterisk");
        assertValidObjectName();
    }

    /* DOUBLE QUOTES */

    @Test
    public void testBuildNameWithQuote() {
        buildStatsKeyExpectations(mockery, mockKey, "with\"quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithTwoQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, "with\"two\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithThreeQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, "with\"three\"awesome\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithEscapedQuote() {
        buildStatsKeyExpectations(mockery, mockKey, "with\\\"escaped_quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithQuote() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with\"quote", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithTwoQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with\"two\"quotes", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithThreeQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with\"three\"awesome\"quotes", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithEscapedQuote() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with\\\"escaped_quote", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithQuote() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\"quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithTwoQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\"two\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithThreeQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\"three\"awesome\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithEscapedQuote() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\\\"escaped_quote");
        assertValidObjectName();
    }

    /* EQUALS */

    @Test
    public void testBuildNameWithEquals() {
        buildStatsKeyExpectations(mockery, mockKey, "with=equals");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithEquals() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with=equals", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithEquals() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with=equals");
        assertValidObjectName();
    }

    /* COMMA */

    @Test
    public void testBuildNameWithComma() {
        buildStatsKeyExpectations(mockery, mockKey, "with,comma");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithComma() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with,comma", NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithComma() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with,comma");
        assertValidObjectName();
    }

    /* ASSERTIONS */

    private void assertValidObjectName() {

        String name = buildKeyName(NAMESPACE, mockKey, TYPE_TEST, SUBTYPE_TEST, true);

        try {
            new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            fail(e.toString());
        }
    }
}
