package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Segments extends Resource {
    public Segments(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> create(String name, Map<String, ?> filters) {
        return http.post("/segments", Params.of("name", name, "filters", filters));
    }

    public Map<String, Object> create(Params params) {
        return http.post("/segments", params);
    }

    public Map<String, Object> list() {
        return http.get("/segments");
    }

    public Map<String, Object> get(long id) {
        return http.get("/segments/" + id);
    }

    public void delete(long id) {
        http.delete("/segments/" + id);
    }
}
