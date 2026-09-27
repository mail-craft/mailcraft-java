package dev.mailcraft;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/** The HTTP layer shared by every resource. Internal: use the resources on {@link MailCraft}. */
public final class HttpTransport {
    static final String VERSION = "1.0.0";

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();
    private static final Type ERRORS_TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();

    private final String apiKey;
    private final String baseUrl;
    private final Duration timeout;
    private final HttpClient http;

    HttpTransport(String apiKey, String baseUrl, Duration timeout, HttpClient http) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("MailCraft: an API key is required. Find yours under Settings > API Keys.");
        }
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.timeout = timeout;
        this.http = http;
    }

    public Map<String, Object> get(String path) {
        return get(path, null);
    }

    public Map<String, Object> get(String path, Map<String, ?> query) {
        return send("GET", path + queryString(query), null);
    }

    public Map<String, Object> post(String path, Map<String, ?> body) {
        return send("POST", path, body);
    }

    public Map<String, Object> patch(String path, Map<String, ?> body) {
        return send("PATCH", path, body);
    }

    public void delete(String path) {
        send("DELETE", path, null);
    }

    private Map<String, Object> send(String method, String path, Map<String, ?> body) {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(timeout)
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .header("User-Agent", "mailcraft-java/" + VERSION);

        if (body != null) {
            request.header("Content-Type", "application/json");
            request.method(method, HttpRequest.BodyPublishers.ofString(GSON.toJson(body)));
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response;
        try {
            response = http.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException("MailCraft: request failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MailCraft: request interrupted", e);
        }

        if (response.statusCode() >= 400) {
            throw toException(response);
        }

        if (response.statusCode() == 204 || response.body() == null || response.body().isBlank()) {
            return null;
        }

        return GSON.fromJson(response.body(), MAP_TYPE);
    }

    private static MailCraftApiException toException(HttpResponse<String> response) {
        int status = response.statusCode();

        try {
            JsonElement parsed = JsonParser.parseString(response.body());

            if (parsed.isJsonObject()) {
                JsonObject body = parsed.getAsJsonObject();

                if (body.has("error") && body.get("error").isJsonObject()) {
                    JsonObject error = body.getAsJsonObject("error");
                    return new MailCraftApiException(status, stringOr(error, "message", "Request failed"), stringOr(error, "type", null), null);
                }

                if (body.has("message")) {
                    Map<String, List<String>> errors = body.has("errors") ? GSON.fromJson(body.get("errors"), ERRORS_TYPE) : null;
                    return new MailCraftApiException(status, body.get("message").getAsString(), null, errors);
                }
            }
        } catch (RuntimeException ignored) {
            // Not JSON: fall through to the status-only error below.
        }

        return new MailCraftApiException(status, "Request failed with status " + status, null, null);
    }

    private static String stringOr(JsonObject object, String key, String fallback) {
        return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : fallback;
    }

    private static String queryString(Map<String, ?> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }

        StringJoiner joined = new StringJoiner("&", "?", "");
        query.forEach((key, value) -> {
            if (value != null) {
                joined.add(encode(key) + "=" + encode(String.valueOf(value)));
            }
        });

        return joined.length() > 1 ? joined.toString() : "";
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
