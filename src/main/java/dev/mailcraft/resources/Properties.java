package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Properties extends Resource {
    public Properties(HttpTransport http) {
        super(http);
    }

    /** Create a custom contact property; {@code type} is text, number, boolean, date or list. */
    public Map<String, Object> create(String key, String label, String type) {
        return http.post("/properties", Params.of("key", key, "label", label, "type", type));
    }

    public Map<String, Object> create(Params params) {
        return http.post("/properties", params);
    }

    public Map<String, Object> list() {
        return http.get("/properties");
    }

    public void delete(long id) {
        http.delete("/properties/" + id);
    }
}
