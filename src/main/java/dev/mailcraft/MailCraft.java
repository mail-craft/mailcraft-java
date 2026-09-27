package dev.mailcraft;

import dev.mailcraft.resources.Campaigns;
import dev.mailcraft.resources.Contacts;
import dev.mailcraft.resources.Domains;
import dev.mailcraft.resources.Emails;
import dev.mailcraft.resources.Lists;
import dev.mailcraft.resources.Metrics;
import dev.mailcraft.resources.Properties;
import dev.mailcraft.resources.Segments;
import dev.mailcraft.resources.Senders;
import dev.mailcraft.resources.Suppressions;
import dev.mailcraft.resources.TemplateFolders;
import dev.mailcraft.resources.Templates;
import dev.mailcraft.resources.Webhooks;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * The MailCraft API client.
 *
 * <pre>{@code
 * MailCraft mailcraft = new MailCraft(System.getenv("MAILCRAFT_API_KEY"));
 * mailcraft.emails().send(SendEmailRequest.builder()
 *     .from("hello@yourdomain.com").to("person@example.com")
 *     .subject("Welcome!").html("<p>Thanks for signing up.</p>")
 *     .build());
 * }</pre>
 */
public final class MailCraft {
    public static final String DEFAULT_BASE_URL = "https://api.mailcraft.host/v1";

    private final Emails emails;
    private final Domains domains;
    private final Senders senders;
    private final Contacts contacts;
    private final Lists lists;
    private final Segments segments;
    private final Properties properties;
    private final Templates templates;
    private final TemplateFolders templateFolders;
    private final Campaigns campaigns;
    private final Webhooks webhooks;
    private final Suppressions suppressions;
    private final Metrics metrics;

    public MailCraft(String apiKey) {
        this(apiKey, DEFAULT_BASE_URL, Duration.ofSeconds(30));
    }

    /** Use a different base URL (staging, self-hosted) or request timeout. */
    public MailCraft(String apiKey, String baseUrl, Duration timeout) {
        HttpTransport http = new HttpTransport(
                apiKey,
                baseUrl,
                timeout,
                HttpClient.newBuilder().connectTimeout(timeout).build());

        this.emails = new Emails(http, SendEmailRequest::toParams);
        this.domains = new Domains(http);
        this.senders = new Senders(http);
        this.contacts = new Contacts(http);
        this.lists = new Lists(http);
        this.segments = new Segments(http);
        this.properties = new Properties(http);
        this.templates = new Templates(http);
        this.templateFolders = new TemplateFolders(http);
        this.campaigns = new Campaigns(http);
        this.webhooks = new Webhooks(http);
        this.suppressions = new Suppressions(http);
        this.metrics = new Metrics(http);
    }

    public Emails emails() { return emails; }
    public Domains domains() { return domains; }
    public Senders senders() { return senders; }
    public Contacts contacts() { return contacts; }
    public Lists lists() { return lists; }
    public Segments segments() { return segments; }
    public Properties properties() { return properties; }
    public Templates templates() { return templates; }
    public TemplateFolders templateFolders() { return templateFolders; }
    public Campaigns campaigns() { return campaigns; }
    public Webhooks webhooks() { return webhooks; }
    public Suppressions suppressions() { return suppressions; }
    public Metrics metrics() { return metrics; }
}
