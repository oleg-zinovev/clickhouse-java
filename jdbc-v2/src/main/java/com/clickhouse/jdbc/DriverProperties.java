package com.clickhouse.jdbc;

import java.util.Collections;
import java.util.List;

/**
 * JDBC driver specific properties. Does not include anything from ClientConfigProperties.
 * Processing logic should be the follows
 * 1. If property is among DriverProperties then Driver handles it specially and will not pass to a client
 * 2. If property is not among DriverProperties then it is passed to a client
 */
public enum DriverProperties {

    IGNORE_UNSUPPORTED_VALUES("jdbc_ignore_unsupported_values", ""),
    SCHEMA_TERM("jdbc_schema_term", ""),
    /**
     * Indicates if driver should create a secure connection over SSL/TLS
     */
    SECURE_CONNECTION("ssl", "false"),

    /**
     * Query settings to be passed along with query operation.
     * {@see com.clickhouse.client.api.query.QuerySettings}
     */
    DEFAULT_QUERY_SETTINGS("default_query_settings", null),

    /**
     * Enables row binary writer for simple insert statements when
     * PreparedStatement is used. Has limitation and can be used with a simple form of insert like;
     * {@code INSERT INTO t VALUES (?, ?, ?...)}
     */
    BETA_ROW_BINARY_WRITER("beta.row_binary_for_simple_insert", "false"),

    /**
     *  Enables closing result set before
     */
    RESULTSET_AUTO_CLOSE("jdbc_resultset_auto_close", "true"),

    /**
     * Cluster name.
     */
    CLUSTER_NAME("cluster_name", null)
    ;


    private final String key;

    private final String defaultValue;

    private final List<String> choices;

    DriverProperties(String key, String defaultValue) {
        this(key, defaultValue, Collections.emptyList());
    }

    DriverProperties(String key, String defaultValue, List<String> choices) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.choices = choices;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public List<String> getChoices() {
        return choices;
    }
}
