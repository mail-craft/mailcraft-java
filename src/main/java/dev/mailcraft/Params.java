package dev.mailcraft;

import java.util.LinkedHashMap;

/**
 * Request parameters, built fluently: {@code Params.of("name", "Acme").with("region", "eu-west-1")}.
 * Null values are skipped, so optional fields can be passed through unconditionally.
 */
public final class Params extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static Params of() {
        return new Params();
    }

    public static Params of(String key, Object value) {
        return new Params().with(key, value);
    }

    public static Params of(String k1, Object v1, String k2, Object v2) {
        return of(k1, v1).with(k2, v2);
    }

    public static Params of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return of(k1, v1, k2, v2).with(k3, v3);
    }

    public Params with(String key, Object value) {
        if (value != null) {
            put(key, value);
        }
        return this;
    }
}
