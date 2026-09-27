package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Domains extends Resource {
    public Domains(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> create(String name) {
        return http.post("/domains", Params.of("name", name));
    }

    public Map<String, Object> create(String name, String region) {
        return http.post("/domains", Params.of("name", name, "region", region));
    }

    public Map<String, Object> list() {
        return http.get("/domains");
    }

    public Map<String, Object> get(long id) {
        return http.get("/domains/" + id);
    }

    public Map<String, Object> verify(long id) {
        return http.post("/domains/" + id + "/verify", null);
    }

    public void delete(long id) {
        http.delete("/domains/" + id);
    }
}
