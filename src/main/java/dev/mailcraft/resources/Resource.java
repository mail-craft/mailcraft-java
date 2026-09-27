package dev.mailcraft.resources;

import dev.mailcraft.HttpTransport;

abstract class Resource {
    protected final HttpTransport http;

    Resource(HttpTransport http) {
        this.http = http;
    }
}
