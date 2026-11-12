package com.usermanagement.exception;

/**
 * Exception wenn eine E-Mail-Adresse bereits registriert ist.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
