package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Templates extends Resource {
    public Templates(HttpTransport http) {
        super(http);
    }

    /** Create a template; params need {@code name} and {@code subject}, plus {@code html_body} and more. */
    public Map<String, Object> create(Params params) {
        return http.post("/templates", params);
    }

    public Map<String, Object> list() {
        return http.get("/templates");
    }

    public Map<String, Object> get(long id) {
        return http.get("/templates/" + id);
    }

    /** Update a template; each save becomes a new version. Pass only the fields to change. */
    public Map<String, Object> update(long id, Params params) {
        return http.patch("/templates/" + id, params);
    }

    public void delete(long id) {
        http.delete("/templates/" + id);
    }
}
