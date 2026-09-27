package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;
import dev.mailcraft.SendEmailRequest;

public final class Emails extends Resource {
    private final java.util.function.Function<SendEmailRequest, Map<String, ?>> toBody;

    public Emails(HttpTransport http, java.util.function.Function<SendEmailRequest, Map<String, ?>> toBody) {
        super(http);
        this.toBody = toBody;
    }

    /** Send a transactional email. */
    public Map<String, Object> send(SendEmailRequest request) {
        return http.post("/emails", toBody.apply(request));
    }

    public Map<String, Object> list() {
        return http.get("/emails");
    }

    public Map<String, Object> list(int limit) {
        return http.get("/emails", Params.of("limit", limit));
    }

    public Map<String, Object> get(String id) {
        return http.get("/emails/" + id);
    }

    /** Check an address's format, MX records and disposable domain, without sending. */
    public Map<String, Object> validate(String email) {
        return http.get("/emails/validate", Params.of("email", email));
    }
}
