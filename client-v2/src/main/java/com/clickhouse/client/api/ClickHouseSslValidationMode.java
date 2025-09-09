package com.clickhouse.client.api;

/**
 * SSL mode of connection.
 */
public enum ClickHouseSslValidationMode {
    /**
     * Strict validate ssl certificate.
     */
    STRICT,

    /**
     * Do not validate ssl certificate.
     */
    NONE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
