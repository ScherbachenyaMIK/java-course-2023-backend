package edu.java.exception;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException() {
        super("Link not found");
    }
}
