package dev.mailcraft;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Thrown for any non-2xx response from the MailCraft API.
 *
 * <p>Covers both error shapes the API returns: {@code {"error": {"type", "message"}}} for
 * business-rule failures (plan limits, suppressed recipients, ...), and
 * {@code {"message", "errors": {"field": [...]}}} for validation failures (422).
 */
public class MailCraftApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int status;
    private final String type;
    private final transient Map<String, List<String>> errors;

    public MailCraftApiException(int status, String message, String type, Map<String, List<String>> errors) {
        super(message);
        this.status = status;
        this.type = type;
        this.errors = errors == null ? Collections.emptyMap() : Collections.unmodifiableMap(errors);
    }

    /** The HTTP status code, e.g. 402 or 422. */
    public int getStatus() {
        return status;
    }

    /** The business-rule error type, e.g. {@code plan_limit_reached}, or null for validation errors. */
    public String getType() {
        return type;
    }

    /** Field validation errors for 422 responses; empty otherwise. */
    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
