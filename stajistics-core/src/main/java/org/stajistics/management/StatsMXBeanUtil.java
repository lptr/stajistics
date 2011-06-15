package org.stajistics.management;

import static org.stajistics.management.StatsMXBeanUtil.STAJISTICS_DOMAIN;
import static org.stajistics.management.StatsMXBeanUtil.TYPE_MANAGER;

import java.util.Map;
import java.util.regex.Pattern;

import javax.management.ObjectName;

import org.stajistics.StatsKey;
import org.stajistics.StatsManager;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsMXBeanUtil {

    public static final String STAJISTICS_DOMAIN = org.stajistics.Stats.class.getPackage().getName();

    public static final String TYPE_MANAGER = "manager";
    public static final String TYPE_KEYS = "keys";

    public static final String SUBTYPE_SESSION = "session";
    public static final String SUBTYPE_CONFIG = "config";

    public static final String MANAGER_NAME_STATS = "StatsManager";
    public static final String MANAGER_NAME_CONFIG = "ConfigManager";
    public static final String MANAGER_NAME_SESSION = "SessionManager";
    public static final String MANAGER_NAME_SNAPSHOT = "SnapshotManager";

    public static final Pattern NAMESPACE_EXTRACTOR_PATTERN = Pattern.compile(".*\\[(.*)\\]");

    private StatsMXBeanUtil() {}

    public static String buildManagerName(final String namespace,
                                          final String managerName) {
        StringBuilder buf = new StringBuilder(128);
        buf.append(STAJISTICS_DOMAIN);

        buf.append(":namespace=");
        buf.append(namespace);

        buf.append(",type=");
        buf.append(TYPE_MANAGER);

        buf.append(",name=");
        buf.append(managerName);

        return buf.toString();
    }

    public static String buildName(final String namespace,
                                   final StatsKey key,
                                   final String type,
                                   final String subtype,
                                   final boolean includeAttributes) {

        StringBuilder buf = new StringBuilder(128);

        buf.append(STAJISTICS_DOMAIN);

        buf.append(":namespace=");
        buf.append(namespace);

        buf.append(",type=");
        buf.append(type);

        buf.append(",name=");
        buf.append(ObjectName.quote(key.getName()));

        if (includeAttributes) {
            for (Map.Entry<String,Object> entry : key.getAttributes().entrySet()) {
                buf.append(',');
                buf.append(entry.getKey());
                buf.append('=');
                buf.append(ObjectName.quote(entry.getValue().toString()));
            }
        }

        buf.append(",subtype=");
        buf.append(subtype);

        return buf.toString();
    }
}
