package dev.mailcraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Runs the real client against a local HTTP server, so every request goes through user code paths. */
class MailCraftTest {
    private static final Gson GSON = new Gson();

    private HttpServer server;
    private final List<Recorded> requests = new ArrayList<>();
    private int nextStatus = 200;
    private String nextBody = "{\"data\":{}}";

    record Recorded(String method, String path, String query, String authorization, String userAgent, String body) {}

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            requests.add(new Recorded(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI().getPath(),
                    exchange.getRequestURI().getRawQuery(),
                    exchange.getRequestHeaders().getFirst("Authorization"),
                    exchange.getRequestHeaders().getFirst("User-Agent"),
                    body));

            byte[] response = nextBody.getBytes(StandardCharsets.UTF_8);
            if (nextStatus == 204) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(nextStatus, response.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(response);
                }
            }
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private MailCraft client() {
        return new MailCraft("sk_test_123", "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/", Duration.ofSeconds(5));
    }

    private void respond(int status, String body) {
        nextStatus = status;
        nextBody = body;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> json(String body) {
        return GSON.fromJson(body, Map.class);
    }

    @Test
    void itRequiresAnApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new MailCraft(""));
    }

    @Test
    void sendingAnEmailPostsJsonWithAuth() {
        respond(201, "{\"data\":{\"id\":\"em_1\"}}");

        Map<String, Object> result = client().emails().send(SendEmailRequest.builder()
                .from("hello@acme.test")
                .to("ada@example.com")
                .subject("Hi")
                .html("<p>Hi</p>")
                .build());

        Recorded request = requests.get(0);
        assertEquals("em_1", ((Map<?, ?>) result.get("data")).get("id"));
        assertEquals("POST", request.method());
        assertEquals("/v1/emails", request.path());
        assertEquals("Bearer sk_test_123", request.authorization());
        assertTrue(request.userAgent().startsWith("mailcraft-java/"));
        assertEquals(Map.of("from", "hello@acme.test", "to", List.of("ada@example.com"), "subject", "Hi", "html", "<p>Hi</p>"), json(request.body()));
    }

    @Test
    void theBuilderRejectsIncompleteEmails() {
        assertThrows(IllegalStateException.class, () -> SendEmailRequest.builder().from("a@b.test").subject("Hi").html("x").build());
        assertThrows(IllegalStateException.class, () -> SendEmailRequest.builder().from("a@b.test").to("c@d.test").subject("Hi").build());
    }

    @Test
    void queryParametersAreEncoded() {
        client().emails().validate("ada+test@example.com");
        client().contacts().list(5);

        assertEquals("/v1/emails/validate", requests.get(0).path());
        assertEquals("email=ada%2Btest%40example.com", requests.get(0).query());
        assertEquals("limit=5", requests.get(1).query());
    }

    @Test
    void resourcesCallTheRightEndpoints() {
        MailCraft mailcraft = client();

        mailcraft.domains().verify(3);
        mailcraft.senders().create(3, "hi@acme.test", "Acme");
        mailcraft.contacts().addToLists("ct_1", List.of(4L, 5L));
        mailcraft.contacts().removeFromList("ct_1", 4);
        mailcraft.lists().create(Params.of("name", "Beta", "type", "static"));
        mailcraft.segments().create("Pro", Map.of("conditions", List.of()));
        mailcraft.properties().create("plan", "Plan", "text");
        mailcraft.templates().update(7, Params.of("subject", "New"));
        mailcraft.templateFolders().create("Onboarding");
        mailcraft.campaigns().send(9);
        mailcraft.webhooks().create("https://acme.test/hook", List.of("email.delivered"));
        mailcraft.suppressions().add("x@example.com", "manual");
        mailcraft.metrics().reputation();

        List<String> calls = requests.stream().map(r -> r.method() + " " + r.path()).toList();
        assertEquals(List.of(
                "POST /v1/domains/3/verify",
                "POST /v1/senders",
                "POST /v1/contacts/ct_1/lists",
                "DELETE /v1/contacts/ct_1/lists/4",
                "POST /v1/lists",
                "POST /v1/segments",
                "POST /v1/properties",
                "PATCH /v1/templates/7",
                "POST /v1/template-folders",
                "POST /v1/campaigns/9/send",
                "POST /v1/webhooks",
                "POST /v1/suppressions",
                "GET /v1/reputation"), calls);
        assertEquals(Map.of("list_ids", List.of(4.0, 5.0)), json(requests.get(2).body()));
        assertEquals(Map.of("subject", "New"), json(requests.get(7).body()));
    }

    @Test
    void paramsSkipNullValues() {
        client().domains().create("acme.test", null);

        assertEquals(Map.of("name", "acme.test"), json(requests.get(0).body()));
    }

    @Test
    void deletesHandleNoContent() {
        respond(204, "");

        client().domains().delete(3);

        assertEquals("DELETE /v1/domains/3", requests.get(0).method() + " " + requests.get(0).path());
    }

    @Test
    void businessRuleErrorsCarryTypeAndMessage() {
        respond(402, "{\"error\":{\"type\":\"plan_limit_reached\",\"message\":\"Upgrade to add more domains.\"}}");

        MailCraftApiException error = assertThrows(MailCraftApiException.class, () -> client().domains().create("acme.test"));

        assertEquals(402, error.getStatus());
        assertEquals("plan_limit_reached", error.getType());
        assertEquals("Upgrade to add more domains.", error.getMessage());
    }

    @Test
    void validationErrorsCarryFieldErrors() {
        respond(422, "{\"message\":\"The email field is required.\",\"errors\":{\"email\":[\"The email field is required.\"]}}");

        MailCraftApiException error = assertThrows(MailCraftApiException.class, () -> client().contacts().upsert(Params.of()));

        assertEquals(422, error.getStatus());
        assertNull(error.getType());
        assertEquals(Map.of("email", List.of("The email field is required.")), error.getErrors());
    }

    @Test
    void nonJsonErrorsFallBackToTheStatus() {
        respond(503, "upstream down");

        MailCraftApiException error = assertThrows(MailCraftApiException.class, () -> client().metrics().get());

        assertEquals(503, error.getStatus());
    }
}
