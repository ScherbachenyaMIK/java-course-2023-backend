package edu.java.bot.util;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinkTest {

    @Test
    void getProtocol() {
        Link link = new Link("http", "example.com", "/test", 80);
        assertEquals("http", link.getProtocol());
    }

    @Test
    void getHost() {
        Link link = new Link("http", "example.com", "/test", 80);
        assertEquals("example.com", link.getHost());
    }

    @Test
    void getPath() {
        Link link = new Link("http", "example.com", "/test", 80);
        assertEquals("/test", link.getPath());
    }

    @Test
    void getPort() {
        Link link = new Link("http", "example.com", "/test", 80);
        assertEquals(80, link.getPort());
    }

    @Test
    void parseValidURL() throws URISyntaxException {
        URI url = new URI("http://example.com/test");
        Link link = Link.parse(url);
        assertEquals("http", link.getProtocol());
        assertEquals("example.com", link.getHost());
        assertEquals("/test", link.getPath());
        assertEquals(-1, link.getPort()); // Порт должен быть -1, если не указан
    }

    @Test
    void parseInvalidURLMissingScheme() {
        assertThrows(URISyntaxException.class, () -> {
            Link.parse(new URI("example.com/test"));
        });
    }

    @Test
    void parseInvalidURLMissingHost() {
        assertThrows(URISyntaxException.class, () -> {
            Link.parse(new URI("http://"));
        });
    }

    @Test
    void parseInvalidURLEmptyHost() {
        assertThrows(URISyntaxException.class, () -> {
            Link.parse(new URI("http:///test"));
        });
    }

    @Test
    void parseInvalidURLNonDomainHost() {
        assertThrows(URISyntaxException.class, () -> {
            Link.parse(new URI("http://localhost/test"));
        });
    }
}
