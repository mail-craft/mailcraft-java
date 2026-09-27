package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Suppressions extends Resource {
    public Suppressions(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> add(String email) {
        return http.post("/suppressions", Params.of("email", email));
    }

    public Map<String, Object> add(String email, String reason) {
        return http.post("/suppressions", Params.of("email", email, "reason", reason));
    }

    public Map<String, Object> list() {
        return http.get("/suppressions");
    }

    public Map<String, Object> list(int limit) {
        return http.get("/suppressions", Params.of("limit", limit));
    }

    public void delete(long id) {
        http.delete("/suppressions/" + id);
    }
}
