package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Response for ExceptionHandler
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus statusCode;
    private String message;
}
