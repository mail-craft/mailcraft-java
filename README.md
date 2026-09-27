# MailCraft (Java)

Official Java SDK for the [MailCraft](https://mailcraft.host) email API: transactional email, contacts, lists, segments, templates, campaigns, webhooks and more.

Requires Java 17+. Uses the JDK's built-in HTTP client and [Gson](https://github.com/google/gson) for JSON.

## Install

Maven:

```xml
<dependency>
  <groupId>dev.mailcraft</groupId>
  <artifactId>mailcraft-java</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

```groovy
implementation "dev.mailcraft:mailcraft-java:0.1.0"
```

## Quick start

```java
import dev.mailcraft.MailCraft;
import dev.mailcraft.SendEmailRequest;

MailCraft mailcraft = new MailCraft(System.getenv("MAILCRAFT_API_KEY"));

mailcraft.emails().send(SendEmailRequest.builder()
    .from("hello@yourdomain.com")
    .to("person@example.com")
    .subject("Welcome!")
    .html("<p>Thanks for signing up.</p>")
    .build());
```

Create an API key under **Settings → API keys** in your MailCraft dashboard.

Other resources take simple arguments or a `Params` map, and return the API's JSON as a `Map<String, Object>`:

```java
import dev.mailcraft.Params;

mailcraft.contacts().upsert(Params.of("email", "ada@example.com", "first_name", "Ada")
    .with("properties", Map.of("plan", "pro")));

mailcraft.domains().create("acme.com");
```

`Params` skips `null` values, so optional fields can be passed straight through.

## Resources

Every MailCraft SDK has the same resources and methods:

| Resource | Methods |
| --- | --- |
| `emails()` | `send`, `list`, `get`, `validate` |
| `domains()` | `create`, `list`, `get`, `verify`, `delete` |
| `senders()` | `create`, `list`, `get`, `delete` |
| `contacts()` | `upsert`, `list`, `get`, `delete`, `unsubscribe`, `addToLists`, `lists`, `removeFromList` |
| `lists()` | `create`, `list`, `get`, `delete` |
| `segments()` | `create`, `list`, `get`, `delete` |
| `properties()` | `create`, `list`, `delete` |
| `templates()` | `create`, `list`, `get`, `update`, `delete` |
| `templateFolders()` | `create`, `list`, `delete` |
| `campaigns()` | `create`, `list`, `get`, `send`, `delete` |
| `webhooks()` | `create`, `list`, `delete` |
| `suppressions()` | `add`, `list`, `delete` |
| `metrics()` | `get`, `reputation` |

See the [API reference](https://docs.mailcraft.host/api-reference) for every field.

## Errors

Any non-2xx response throws `MailCraftApiException` (unchecked):

```java
try {
    mailcraft.domains().create("acme.com");
} catch (MailCraftApiException error) {
    error.getStatus();  // e.g. 402
    error.getType();    // e.g. "plan_limit_reached" (business-rule errors)
    error.getErrors();  // field errors for 422 validation failures
}
```

## Options

```java
new MailCraft(apiKey, "https://api.mailcraft.host/v1", Duration.ofSeconds(30));
```

## Development

```bash
mvn test
```

The tests run the real client against a local HTTP server, so every request goes through the same code a user's would.

## License

MIT
