package edu.java.bot.util;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;

@Getter
public class Link {
    private final String protocol;
    private final String host;
    private final String path;
    private final int port;

    public Link(String protocol, String host, String path, int port) {
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.port = port;
    }

    public static Link parse(URI url) throws URISyntaxException {
        String errorMessage = "Invalid URL";
        if (url == null || url.getScheme() == null || url.getHost() == null) {
            if (url != null) {
                throw new URISyntaxException(errorMessage, url.toString());
            } else {
                throw new URISyntaxException(errorMessage, "null");
            }
        }

        String protocol = url.getScheme();
        String host = url.getHost();
        String path = url.getPath();
        int port = url.getPort();

        // Checking for Syntax
        if (!isValidUrl(url)) {
            throw new URISyntaxException(errorMessage, url.toString());
        }

        return new Link(protocol, host, path, port);
    }

    private static boolean isValidUrl(URI url) {
        String host = url.getHost();
        String scheme = url.getScheme();

        // Checking for scheme and host not empty
        if (scheme == null || host == null) {
            return false;
        }

        // Checking for domain
        return host.isEmpty() || host.contains(".");
    }
}
