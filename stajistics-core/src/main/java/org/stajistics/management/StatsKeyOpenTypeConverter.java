package org.stajistics.management;

import org.stajistics.DefaultStatsKeyFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyBuilder;
import org.stajistics.StatsKeyFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsKeyOpenTypeConverter {

    private static final char KEY_ATTRS_DELIMITER = '?';
    private static final char ATTR_NAME_VALUE_PAIR_DELIMITER = '&';
    private static final char ATTR_NAME_VALUE_DELIMITER = '=';

    private static final char ESCAPE = '\\';
    private static final String ATTR_PREFIX_BOOLEAN = "b_";
    private static final String ATTR_PREFIX_INTEGER = "i_";
    private static final String ATTR_PREFIX_LONG = "l_";

    private static final Pattern ATTR_NAME_VALUE_PAIR_SPLITTER = Pattern.compile("(?<!\\\\)[&]");
    private static final Pattern ATTR_NAME_VALUE_SPLITTER = Pattern.compile("(?<!\\\\)[=]");

    public Set<String> toOpenType(final Set<StatsKey> keys) {
        Set<String> result = new HashSet<String>(keys.size());

        for (StatsKey key : keys) {
            result.add(toOpenType(key));
        }

        return Collections.unmodifiableSet(result);
    }

    public String toOpenType(final StatsKey key) {
        final int nameLen = key.getName().length();
        final int attrCount = key.getAttributeCount();

        final StringBuilder buf = new StringBuilder(nameLen + (attrCount * 10));

        // Name
        escapePart(buf, key.getName());

        if (attrCount > 0) {

            buf.append(KEY_ATTRS_DELIMITER);

            Object value;
            Class<?> valueClass;

            for (Map.Entry<String, Object> entry : key.getAttributes().entrySet()) {

                // Attr name
                escapePart(buf, entry.getKey());
                buf.append(ATTR_NAME_VALUE_DELIMITER);

                // Attr value
                value = entry.getValue();
                valueClass = value.getClass();

                if (valueClass == String.class) {
                    final String strValue = value.toString();
                    if (strValue.length() > 1) {
                        final char c = strValue.charAt(0);
                        if ((c == 'b' || c == 'i' || c == 'l') && strValue.charAt(1) == '_') {
                            buf.append(ESCAPE);
                        }
                    }

                    escapePart(buf, strValue);

                } else if (valueClass == Boolean.class) {
                    Boolean b = (Boolean) value;
                    buf.append(ATTR_PREFIX_BOOLEAN);
                    buf.append(b ? 't' : 'f');

                } else if (valueClass == Integer.class) {
                    buf.append(ATTR_PREFIX_INTEGER);
                    buf.append(value.toString());

                } else if (valueClass == Long.class) {
                    buf.append(ATTR_PREFIX_LONG);
                    buf.append(value.toString());
                }

                buf.append(ATTR_NAME_VALUE_PAIR_DELIMITER);
            }

            if (buf.charAt(buf.length() - 1) == ATTR_NAME_VALUE_PAIR_DELIMITER) {
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

    public StatsKey fromOpenType(final String openTypeKey, final StatsKeyFactory factory) {

        final char[] openTypeChars = openTypeKey.toCharArray();
        final int len = openTypeChars.length;

        char c = '\0';
        char lastChar;
        int i;

        // Find the index of the end of the key name
        for (i = 0; i < len; i++) {
            lastChar = c;
            c = openTypeChars[i];
            if (c == KEY_ATTRS_DELIMITER && lastChar != ESCAPE) {
                // Found all of the name
                break;
            }
            if (lastChar == ESCAPE && c == ESCAPE) {
                c = '\0';
            }
        }

        String keyName = new String(openTypeChars, 0, i);
        StringBuilder buf = new StringBuilder(Math.min(16, keyName.length() + 4));
        unescapePart(buf, keyName);

        StatsKeyBuilder builder = factory.createKeyBuilder(buf.toString());

        // Are any attributes present?
        if (i < len - 1) {
            buf.setLength(0);
            String attributes = new String(openTypeChars, i + 1, len - i - 1);
            unescapeChar(buf, attributes, KEY_ATTRS_DELIMITER);
            attributes = buf.toString();
            
            String[] nameValuePairParts = ATTR_NAME_VALUE_PAIR_SPLITTER.split(attributes);
            for (String nameValuePair : nameValuePairParts) {
                buf.setLength(0);
                unescapeChar(buf, nameValuePair, ATTR_NAME_VALUE_PAIR_DELIMITER);
                nameValuePair = buf.toString();

                String[] nameValueParts = ATTR_NAME_VALUE_SPLITTER.split(nameValuePair);
                if (nameValueParts.length != 2) {
                    throw new MalformedOpenTypeStatsKeyException(openTypeKey);
                }

                buf.setLength(0);
                unescapeChar(buf, nameValueParts[0], ATTR_NAME_VALUE_DELIMITER);
                final String name = buf.toString();

                buf.setLength(0);
                unescapeChar(buf, nameValueParts[1], ATTR_NAME_VALUE_DELIMITER);
                String strValue = buf.toString();

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
                    buf.setLength(0);
                    unescapeAttrTypePrefix(buf, strValue);
                    builder.withAttribute(name, buf.toString());
                }
            }
        }

        return builder.newKey();
    }

    private void unescapeAttrTypePrefix(final StringBuilder buf, final String attrValue) {
        final int partLen = attrValue.length();

        // Unescape type
        if (partLen > 1 && attrValue.charAt(0) == ESCAPE) {
            final char nextChar = attrValue.charAt(1);
            if (nextChar == 'b' || nextChar == 'i' || nextChar == 'l') {
                buf.append(attrValue.substring(1));
                return;
            }
        }

        buf.append(attrValue);
    }

    private void escapePart(final StringBuilder buf, final String part) {
        final int partLen = part.length();
        char c;
        for (int i = 0; i < partLen; i++) {
            c = part.charAt(i);
            switch (c) {
                case KEY_ATTRS_DELIMITER:
                case ATTR_NAME_VALUE_PAIR_DELIMITER:
                case ATTR_NAME_VALUE_DELIMITER:
                    buf.append(ESCAPE);
                    break;
            }
            buf.append(c);
        }
    }

    private void unescapePart(final StringBuilder buf, final String part) {
        final int partLen = part.length();
        char c = '\0';
        char lastChar;
        for (int i = 0; i < partLen; i++) {
            lastChar = c;
            c = part.charAt(i);
            if (c == ESCAPE && lastChar != ESCAPE && i < partLen - 1) {
                final char nextChar = part.charAt(i + 1);
                if (nextChar == KEY_ATTRS_DELIMITER ||
                    nextChar == ATTR_NAME_VALUE_PAIR_DELIMITER ||
                    nextChar == ATTR_NAME_VALUE_DELIMITER) {
                    // We found an escape
                    continue;
                }
            }
            buf.append(c);
        }
    }

    private void unescapeChar(final StringBuilder buf, final String part, final char escapeChar) {
        final int partLen = part.length();
        char c = '\0';
        char lastChar;
        for (int i = 0; i < partLen; i++) {
            lastChar = c;
            c = part.charAt(i);
            if (c == ESCAPE && lastChar != ESCAPE && i < partLen - 1) {
                final char nextChar = part.charAt(i + 1);
                if (nextChar == escapeChar) {
                    // We found an escape
                    continue;
                }
            }
            buf.append(c);
        }
    }

    protected StatsKeyFactory createStatsKeyFactory() {
        return new DefaultStatsKeyFactory();
    }
}
