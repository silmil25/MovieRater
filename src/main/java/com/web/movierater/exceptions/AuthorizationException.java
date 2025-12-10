package com.web.movierater.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        super("User is not authorized to perform this action.");
    }
}
