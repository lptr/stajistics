package org.stajistics.management;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.DefaultStatsKey;
import org.stajistics.SimpleStatsKey;
import org.stajistics.StatsKey;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * 
 *
 * @author The Stajistics Projcet
 */
public class StatsKeyOpenTypeConverterTest extends AbstractStajisticsTestCase {

    private StatsKeyOpenTypeConverter converter;

    @Before
    public void setUp() {
        converter = new StatsKeyOpenTypeConverter();
    }

    @Test
    public void testConvertSimpleKey() {
        StatsKey key = new SimpleStatsKey("test", null);
        String openKey = converter.toOpenType(key);
        assertEquals("test", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithStringAttribute() {
        StatsKey key = new DefaultStatsKey("test", null, Collections.<String,Object>singletonMap("attr", "value"));
        String openKey = converter.toOpenType(key);
        assertEquals("test:attr=value", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithStringAttributes() {
        Map<String,Object> attributes = new LinkedHashMap<String,Object>();
        attributes.put("attr1", "one");
        attributes.put("attr2", "two");

        StatsKey key = new DefaultStatsKey("test", null, attributes);
        String openKey = converter.toOpenType(key);
        assertEquals("test:attr1=one&attr2=two", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithBooleanAttribute() {
        StatsKey key = new DefaultStatsKey("test", null, Collections.<String,Object>singletonMap("cool", true));
        String openKey = converter.toOpenType(key);
        assertEquals("test:cool=b_t", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithIntegerAttribute() {
        StatsKey key = new DefaultStatsKey("test", null, Collections.<String,Object>singletonMap("cats", 3));
        String openKey = converter.toOpenType(key);
        assertEquals("test:cats=i_3", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithLongAttribute() {
        StatsKey key = new DefaultStatsKey("test", null, Collections.<String,Object>singletonMap("id", 42L));
        String openKey = converter.toOpenType(key);
        assertEquals("test:id=l_42", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithMixedAttributes() {
        Map<String,Object> attributes = new LinkedHashMap<String,Object>();
        attributes.put("attr1", "one");
        attributes.put("attr2", true);
        attributes.put("attr3", 44);
        attributes.put("attr4", 66L);

        StatsKey key = new DefaultStatsKey("test", null, attributes);
        String openKey = converter.toOpenType(key);
        assertEquals("test:attr1=one&attr2=b_t&attr3=i_44&attr4=l_66", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithKeyNameEscapes() {
        StatsKey key = new SimpleStatsKey("the:crazy&character=test", null);
        String openKey = converter.toOpenType(key);
        assertEquals("the\\:crazy\\&character\\=test", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithAttrNameEscapes() {
        StatsKey key = new DefaultStatsKey("test", null, Collections.<String,Object>singletonMap("the:crazy&funky=name", "value"));
        String openKey = converter.toOpenType(key);
        assertEquals("test:the\\:crazy\\&funky\\=name=value", openKey);

        assertConvertUnconvertSame(key);
    }

    @Test
    public void testConvertKeyWithAttrValueEscapes() {
        StatsKey key = new DefaultStatsKey("test", null, Collections.<String,Object>singletonMap("name", "the:crazy&funky=value"));
        String openKey = converter.toOpenType(key);
        assertEquals("test:name=the\\:crazy\\&funky\\=value", openKey);

        assertConvertUnconvertSame(key);
    }

    private void assertConvertUnconvertSame(final StatsKey key) {
        String openTypeKey = converter.toOpenType(key);
        StatsKey key2 = converter.fromOpenType(openTypeKey);
        assertEquals(key, key2);
    }
}
