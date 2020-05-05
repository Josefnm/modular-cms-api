package se.josef.cmsapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.josef.cmsapi.model.web.ErrorResponse;


/**
 * returns specified error codes and messages as responses when throwing specified exceptions
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    //TODO add more specific exception response codes
    @ExceptionHandler(value = {
            AuthException.class, UserException.class, ContentException.class, ProjectException.class})
    protected ResponseEntity<Object> clientConflictHandler(RuntimeException ex, WebRequest request) {
        var message = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());

        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> serverConflictHandler(RuntimeException ex, WebRequest request) {
        var message = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = SecurityException.class)
    protected ResponseEntity<Object> securityConflictHandler(RuntimeException ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());

        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
}
