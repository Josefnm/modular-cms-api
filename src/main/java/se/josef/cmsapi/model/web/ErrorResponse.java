package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * response for ExceptionHandler
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;
}
