package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Webhooks extends Resource {
    public Webhooks(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> create(String url, List<String> events) {
        return http.post("/webhooks", Params.of("url", url, "events", events));
    }

    public Map<String, Object> create(Params params) {
        return http.post("/webhooks", params);
    }

    public Map<String, Object> list() {
        return http.get("/webhooks");
    }

    public void delete(long id) {
        http.delete("/webhooks/" + id);
    }
}
