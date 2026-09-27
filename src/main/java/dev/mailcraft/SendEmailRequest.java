package dev.mailcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** A transactional email to send. Build one with {@link #builder()}. */
public final class SendEmailRequest {
    private final Params params;

    private SendEmailRequest(Params params) {
        this.params = params;
    }

    public static Builder builder() {
        return new Builder();
    }

    Params toParams() {
        return params;
    }

    public static final class Builder {
        private final Params params = Params.of();
        private final List<String> to = new ArrayList<>();

        public Builder from(String from) {
            params.with("from", from);
            return this;
        }

        public Builder to(String... addresses) {
            to.addAll(Arrays.asList(addresses));
            return this;
        }

        public Builder cc(String... addresses) {
            params.with("cc", Arrays.asList(addresses));
            return this;
        }

        public Builder bcc(String... addresses) {
            params.with("bcc", Arrays.asList(addresses));
            return this;
        }

        public Builder replyTo(String replyTo) {
            params.with("reply_to", replyTo);
            return this;
        }

        public Builder subject(String subject) {
            params.with("subject", subject);
            return this;
        }

        public Builder html(String html) {
            params.with("html", html);
            return this;
        }

        public Builder text(String text) {
            params.with("text", text);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            params.with("headers", headers);
            return this;
        }

        public Builder tags(String... tags) {
            params.with("tags", Arrays.asList(tags));
            return this;
        }

        public SendEmailRequest build() {
            if (!params.containsKey("from") || to.isEmpty() || !params.containsKey("subject")) {
                throw new IllegalStateException("An email needs from, to and subject.");
            }
            if (!params.containsKey("html") && !params.containsKey("text")) {
                throw new IllegalStateException("An email needs html or text.");
            }

            Params result = Params.of();
            result.putAll(params);
            result.put("to", new ArrayList<>(to));
            return new SendEmailRequest(result);
        }
    }
}
