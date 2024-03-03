package edu.java.exception;

public class NoSuchUserRegisteredException extends RuntimeException {

    public NoSuchUserRegisteredException() {
        super("User is not registered");
    }
}
