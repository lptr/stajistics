package org.stajistics.management;

import org.stajistics.DefaultStatsKeyFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyBuilder;
import org.stajistics.StatsKeyFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsKeyOpenTypeConverter {

    private static final String KEY_ATTRS_DELIMITER = ":";
    private static final String ATTR_NAME_VALUE_PAIR_DELIMITER = "&";
    private static final String ATTR_NAME_VALUE_DELIMITER = "=";

    private static final String ATTR_PREFIX_BOOLEAN = "b_";
    private static final String ATTR_PREFIX_INTEGER = "i_";
    private static final String ATTR_PREFIX_LONG = "l_";

    private static final Pattern KEY_ATTRS_SPLITTER = Pattern.compile("(?<!\\\\)[:]");
    private static final Pattern ATTR_NAME_VALUE_PAIR_SPLITTER = Pattern.compile("(?<!\\\\)[&]"); 
    private static final Pattern ATTR_NAME_VALUE_SPLITTER = Pattern.compile("(?<!\\\\)[=]");

    private static final Pattern KEY_ATTRS_UNESCAPE = Pattern.compile("\\\\:");
    private static final Pattern ATTR_NAME_VALUE_PAIR_UNESCAPE = Pattern.compile("\\\\&");
    private static final Pattern ATTR_NAME_VALUE_UNESCAPE = Pattern.compile("\\\\=");

    public Set<String> toOpenType(final Set<StatsKey> keys) {
        Set<String> result = new HashSet<String>(keys.size());

        for (StatsKey key : keys) {
            result.add(toOpenType(key));
        }

        return Collections.unmodifiableSet(result);
    }

    public String toOpenType(final StatsKey key) {
        StringBuilder buf = new StringBuilder(key.getName().length() + (key.getAttributeCount() * 10));

        String keyName = key.getName();

        buf.append(escapePart(keyName));

        if (key.getAttributeCount() > 0) {

            buf.append(KEY_ATTRS_DELIMITER);

            for (Map.Entry<String, Object> entry : key.getAttributes().entrySet()) {
                Object value = entry.getValue();
                Class<?> valueClass = value.getClass();

                String strValue = value.toString();

                if (valueClass == Boolean.class) {
                    Boolean b = (Boolean) value;
                    strValue = ATTR_PREFIX_BOOLEAN + (b ? 't' : 'f');
                } else if (valueClass == Integer.class) {
                    strValue = ATTR_PREFIX_INTEGER + strValue;
                } else if (valueClass == Long.class) {
                    strValue = ATTR_PREFIX_LONG + strValue;
                }

                buf.append(escapePart(entry.getKey()));
                buf.append(ATTR_NAME_VALUE_DELIMITER);
                buf.append(escapePart(strValue));
                buf.append(ATTR_NAME_VALUE_PAIR_DELIMITER);
            }

            if (buf.charAt(buf.length() - 1) == ATTR_NAME_VALUE_PAIR_DELIMITER.charAt(0)) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    public Set<StatsKey> fromOpenType(final Set<String> keys) {
        StatsKeyFactory factory = createStatsKeyFactory();
        Set<StatsKey> result = new HashSet<StatsKey>();

        for (String key : keys) {
            result.add(fromOpenType(key, factory));
        }

        return Collections.unmodifiableSet(result);
    }

    public StatsKey fromOpenType(final String openTypeKey) {
        StatsKeyFactory factory = createStatsKeyFactory();
        return fromOpenType(openTypeKey, factory);
    }

    protected StatsKey fromOpenType(final String openTypeKey, final StatsKeyFactory factory) {
        String[] parts = KEY_ATTRS_SPLITTER.split(openTypeKey, 2);
        if (parts.length > 2) {
            throw new MalformedOpenTypeStatsKeyException(openTypeKey);
        }

        StatsKeyBuilder builder = factory.createKeyBuilder(unescapePart(parts[0]));

        if (parts.length > 1) {
            String attributes = unescapeKeyAttrDelimiter(parts[1]);
            String[] nameValuePairParts = ATTR_NAME_VALUE_PAIR_SPLITTER.split(attributes);
            for (String nameValuePair : nameValuePairParts) {
                nameValuePair = unescapeAttrNameValuePairDelimiter(nameValuePair);
                String[] nameValueParts = ATTR_NAME_VALUE_SPLITTER.split(nameValuePair);
                if (nameValueParts.length != 2) {
                    throw new MalformedOpenTypeStatsKeyException(openTypeKey);
                }

                final String name = unescapeAttrNameValueDelimiter(nameValueParts[0]);
                final String strValue = unescapeAttrNameValueDelimiter(nameValueParts[1]);

                if (strValue.startsWith(ATTR_PREFIX_BOOLEAN)) {
                    Boolean value = strValue.substring(ATTR_PREFIX_BOOLEAN.length()).equals("t");
                    builder.withAttribute(name, value);

                } else if (strValue.startsWith(ATTR_PREFIX_INTEGER)) {
                    Integer value = Integer.parseInt(strValue.substring(ATTR_PREFIX_INTEGER.length()));
                    builder.withAttribute(name, value);

                } else if (strValue.startsWith(ATTR_PREFIX_LONG)) {
                    Long value = Long.parseLong(strValue.substring(ATTR_PREFIX_LONG.length()));
                    builder.withAttribute(name, value);

                } else {
                    builder.withAttribute(name, strValue);
                }
            }
        }

        return builder.newKey();
    }

    private String escapePart(String part) {
        Matcher m = KEY_ATTRS_SPLITTER.matcher(part);
        part = m.replaceAll("\\\\:");

        m = ATTR_NAME_VALUE_PAIR_SPLITTER.matcher(part);
        part = m.replaceAll("\\\\&");

        m = ATTR_NAME_VALUE_SPLITTER.matcher(part);
        part = m.replaceAll("\\\\=");

        return part;
    }

    private String unescapePart(String part) {
        part = unescapeKeyAttrDelimiter(part);
        part = unescapeAttrNameValuePairDelimiter(part);
        part = unescapeAttrNameValueDelimiter(part);
        return part;
    }

    private String unescapeKeyAttrDelimiter(String part) {
        Matcher m = KEY_ATTRS_UNESCAPE.matcher(part);
        part = m.replaceAll(KEY_ATTRS_DELIMITER);
        return part;
    }

    private String unescapeAttrNameValuePairDelimiter(String part) {
        Matcher m = ATTR_NAME_VALUE_PAIR_UNESCAPE.matcher(part);
        part = m.replaceAll(ATTR_NAME_VALUE_PAIR_DELIMITER);
        return part;
    }

    private String unescapeAttrNameValueDelimiter(String part) {
        Matcher m = ATTR_NAME_VALUE_UNESCAPE.matcher(part);
        part = m.replaceAll(ATTR_NAME_VALUE_DELIMITER);
        return part;
    }

    protected StatsKeyFactory createStatsKeyFactory() {
        return new DefaultStatsKeyFactory();
    }
}
