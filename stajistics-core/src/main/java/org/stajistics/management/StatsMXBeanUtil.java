package org.stajistics.management;

import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.stajistics.StatsKey;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsMXBeanUtil {

    public static final String STAJISTICS_DOMAIN = org.stajistics.StatsFactory.class.getPackage().getName();

    public static final String OBJECT_NAME_ATTR_NAMESPACE = "namespace";
    public static final String OBJECT_NAME_ATTR_NAME = "name";
    public static final String OBJECT_NAME_ATTR_TYPE = "type";
    public static final String OBJECT_NAME_ATTR_SUB_TYPE = "subtype";

    public static final String TYPE_MANAGER = "manager";
    public static final String TYPE_KEYS = "keys";

    public static final String SUBTYPE_SESSION = "session";
    public static final String SUBTYPE_CONFIG = "config";

    public static final String MANAGER_NAME_STATS = "StatsManager";
    public static final String MANAGER_NAME_CONFIG = "ConfigManager";
    public static final String MANAGER_NAME_SESSION = "SessionManager";
    public static final String MANAGER_NAME_TASK_SERVICE = "TaskService";
    public static final String MANAGER_NAME_SNAPSHOT = "SnapshotManager";

    private StatsMXBeanUtil() {}

    /**
     * 
     * @param namespace
     * @return
     */
    public static String getStatsManagerObjectNameString(final String namespace, final boolean quote) {
        return buildManagerName(namespace, MANAGER_NAME_STATS, quote);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public static ObjectName getStatsManagerObjectName(final String namespace) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getStatsManagerObjectNameString(namespace, true));
        return objectName;
    }

    /**
     * 
     * @param namespace
     * @return
     */
    public static String getSessionManagerObjectNameString(final String namespace, final boolean quote) {
        return buildManagerName(namespace, MANAGER_NAME_SESSION, quote);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public static ObjectName getSessionManagerObjectName(final String namespace) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getSessionManagerObjectNameString(namespace, true));
        return objectName;
    }

    /**
     * 
     * @param namespace
     * @return
     */
    public static String getConfigManagerObjectNameString(final String namespace, final boolean quote) {
        return buildManagerName(namespace, MANAGER_NAME_CONFIG, quote);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public static ObjectName getConfigManagerObjectName(final String namespace) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getConfigManagerObjectNameString(namespace, true));
        return objectName;
    }

    /**
     * 
     * @param namespace
     * @param quote
     * @return
     */
    public static String getTaskServiceObjectNameString(final String namespace, final boolean quote) {
        return buildManagerName(namespace, MANAGER_NAME_TASK_SERVICE, quote);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public static ObjectName getTaskServiceObjectName(final String namespace) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getTaskServiceObjectNameString(namespace, true));
        return objectName;
    }

    /**
     * 
     * @param namespace
     * @param key
     * @return
     */
    public static String getSessionObjectNameString(final String namespace, final StatsKey key, final boolean quote) {
        return buildKeyName(namespace, key, TYPE_KEYS, SUBTYPE_SESSION, quote);
    }

    /**
     * 
     * @param namespace
     * @param key
     * @return
     * @throws MalformedObjectNameException
     */
    public static ObjectName getSessionObjectName(final String namespace, final StatsKey key) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getSessionObjectNameString(namespace, key, true));
        return objectName;
    }

    /**
     * 
     * @param namespace
     * @param key
     * @return
     */
    public static String getConfigObjectNameString(final String namespace, final StatsKey key, final boolean quote) {
        return buildKeyName(namespace, key, TYPE_KEYS, SUBTYPE_CONFIG, quote);
    }

    /**
     * 
     * @param namespace
     * @param key
     * @return
     * @throws MalformedObjectNameException
     */
    public static ObjectName getConfigObjectName(final String namespace, final StatsKey key) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getConfigObjectNameString(namespace, key, true));
        return objectName;
    }

    private static void appendAttr(final StringBuilder buf, 
                                   final String key, 
                                   final String value,
                                   final boolean quote) {
        boolean first = key.equals(OBJECT_NAME_ATTR_NAMESPACE);
        if (first) {
            buf.append(':');
        } else {
            buf.append(',');
        }

        if (quote && valueNeedsQuotes(key)) {
            buf.append(ObjectName.quote(key));
        } else {
            buf.append(key);
        }
        buf.append('=');

        if (quote && valueNeedsQuotes(value)) {
            buf.append(ObjectName.quote(value));
        } else {
            buf.append(value);
        }
    }

    public static boolean valueNeedsQuotes(final String value) {
        final int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\n':
                case '\\':
                case '"':
                case '*':
                case '?':
                case ',':
                case '=':
                case ':':
                    return true;
            }
        }

        return false;
    }

    protected static String buildManagerName(final String namespace,
                                             final String managerName,
                                             final boolean quote) {
        StringBuilder buf = new StringBuilder(128);
        buf.append(STAJISTICS_DOMAIN);

        appendAttr(buf, OBJECT_NAME_ATTR_NAMESPACE, namespace, quote);
        appendAttr(buf, OBJECT_NAME_ATTR_TYPE, TYPE_MANAGER, quote);
        appendAttr(buf, OBJECT_NAME_ATTR_NAME, managerName, quote);

        return buf.toString();
    }

    protected static String buildKeyName(final String namespace,
                                         final StatsKey key,
                                         final String type,
                                         final String subtype,
                                         final boolean quote) {

        StringBuilder buf = new StringBuilder(128);
        buf.append(STAJISTICS_DOMAIN);

        appendAttr(buf, OBJECT_NAME_ATTR_NAMESPACE, namespace, quote);
        appendAttr(buf, OBJECT_NAME_ATTR_TYPE, type, quote);
        appendAttr(buf, OBJECT_NAME_ATTR_NAME, key.getName(), quote);

        boolean includeAttributes = subtype.equals(SUBTYPE_SESSION); // Session beans are StatsKey-unique
        if (includeAttributes) {
            for (Map.Entry<String,Object> entry : key.getAttributes().entrySet()) {
                appendAttr(buf, entry.getKey(), entry.getValue().toString(), quote);
            }
        }

        appendAttr(buf, OBJECT_NAME_ATTR_SUB_TYPE, subtype, quote);

        return buf.toString();
    }
}
