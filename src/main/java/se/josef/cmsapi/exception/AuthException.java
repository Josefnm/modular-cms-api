package se.josef.cmsapi.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
