package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Campaigns extends Resource {
    public Campaigns(HttpTransport http) {
        super(http);
    }

    /**
     * Create a draft campaign. Params need {@code name}, {@code subject}, {@code template_id}
     * and {@code sender_id}, plus a {@code list_id} or {@code segment_id}.
     */
    public Map<String, Object> create(Params params) {
        return http.post("/campaigns", params);
    }

    public Map<String, Object> list() {
        return http.get("/campaigns");
    }

    public Map<String, Object> get(long id) {
        return http.get("/campaigns/" + id);
    }

    public Map<String, Object> send(long id) {
        return http.post("/campaigns/" + id + "/send", null);
    }

    public void delete(long id) {
        http.delete("/campaigns/" + id);
    }
}
