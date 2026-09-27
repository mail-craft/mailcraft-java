package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Contacts extends Resource {
    public Contacts(HttpTransport http) {
        super(http);
    }

    /**
     * Create a contact, or update it if one exists for this email. Params: {@code email}
     * (required), {@code first_name}, {@code last_name}, {@code status}, {@code properties}.
     */
    public Map<String, Object> upsert(Params params) {
        return http.post("/contacts", params);
    }

    public Map<String, Object> list() {
        return http.get("/contacts");
    }

    public Map<String, Object> list(int limit) {
        return http.get("/contacts", Params.of("limit", limit));
    }

    public Map<String, Object> get(String id) {
        return http.get("/contacts/" + id);
    }

    public void delete(String id) {
        http.delete("/contacts/" + id);
    }

    public Map<String, Object> unsubscribe(String id) {
        return http.post("/contacts/" + id + "/unsubscribe", null);
    }

    public void addToLists(String id, List<Long> listIds) {
        http.post("/contacts/" + id + "/lists", Params.of("list_ids", listIds));
    }

    public Map<String, Object> lists(String id) {
        return http.get("/contacts/" + id + "/lists");
    }

    public void removeFromList(String id, long listId) {
        http.delete("/contacts/" + id + "/lists/" + listId);
    }
}
