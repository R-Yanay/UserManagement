package com.usermanagement.exception;

/**
 * Exception wenn E-Mail oder Passwort beim Login falsch sind.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
