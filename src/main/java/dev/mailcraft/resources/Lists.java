package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Lists extends Resource {
    public Lists(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> create(String name) {
        return http.post("/lists", Params.of("name", name));
    }

    /** Create a list; params may include {@code description}, {@code type} and {@code segment_id}. */
    public Map<String, Object> create(Params params) {
        return http.post("/lists", params);
    }

    public Map<String, Object> list() {
        return http.get("/lists");
    }

    public Map<String, Object> get(long id) {
        return http.get("/lists/" + id);
    }

    public void delete(long id) {
        http.delete("/lists/" + id);
    }
}
