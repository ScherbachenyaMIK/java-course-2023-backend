package edu.java.util;

import java.net.URI;

@SuppressWarnings("HideUtilityClassConstructor")
public class LinkParser {
    public static String parseLink(URI url) {
        if (url.toString().matches("^https?://api\\.github\\.com/repos/[^/]+/[^/]+$")) {
            return "Github";
        } else {
            return "StackOverflow";
        }
    }
}
