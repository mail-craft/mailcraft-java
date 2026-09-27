package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class TemplateFolders extends Resource {
    public TemplateFolders(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> create(String name) {
        return http.post("/template-folders", Params.of("name", name));
    }

    public Map<String, Object> list() {
        return http.get("/template-folders");
    }

    public void delete(long id) {
        http.delete("/template-folders/" + id);
    }
}
