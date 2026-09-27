package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Senders extends Resource {
    public Senders(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> create(long domainId, String email, String name) {
        return http.post("/senders", Params.of("domain_id", domainId, "email", email, "name", name));
    }

    /** Create a sender; params may include {@code reply_to}. */
    public Map<String, Object> create(Params params) {
        return http.post("/senders", params);
    }

    public Map<String, Object> list() {
        return http.get("/senders");
    }

    public Map<String, Object> get(long id) {
        return http.get("/senders/" + id);
    }

    public void delete(long id) {
        http.delete("/senders/" + id);
    }
}
