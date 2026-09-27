package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;
import dev.mailcraft.Params;
import java.util.List;
import java.util.Map;

public final class Metrics extends Resource {
    public Metrics(HttpTransport http) {
        super(http);
    }

    public Map<String, Object> get() {
        return http.get("/metrics");
    }

    /** Daily sending metrics between two {@code YYYY-MM-DD} dates. */
    public Map<String, Object> get(String startDate, String endDate) {
        return http.get("/metrics", Params.of("start_date", startDate, "end_date", endDate));
    }

    public Map<String, Object> reputation() {
        return http.get("/reputation");
    }
}
