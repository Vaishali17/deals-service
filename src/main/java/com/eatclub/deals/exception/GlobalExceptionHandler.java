package com.eatclub.deals.exception;

import com.eatclub.deals.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles MissingServletRequestParameterException.
     * This exception is thrown when a required @RequestParam is not present in the request.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The MissingServletRequestParameterException instance.
     * @return A ResponseEntity containing an ErrorResponse with "MISSING_PARAMETER" code.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String parameterName = ex.getParameterName();
        String errorMessage = String.format("The '%s' parameter is required and cannot be empty.", parameterName);
        ErrorResponse errorResponse = new ErrorResponse("MISSING_PARAMETER", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles DateTimeParseException.
     * This exception is typically thrown by java.time parsing methods when input
     * cannot be parsed into a date/time object.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The DateTimeParseException instance.
     * @return A ResponseEntity containing an ErrorResponse with "INVALID_TIME_FORMAT" code.
     */
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(DateTimeParseException ex) {
        // You might want to strip sensitive path info from ex.getMessage() for production
        String errorMessage = "Invalid time format provided. Please ensure it's a valid time string. " + ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse("INVALID_TIME_FORMAT", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidInputException.
     * This exception is thrown when a method argument (like a @RequestParam or @PathVariable)
     * is having empty string.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The InvalidInputException instance.
     * @return A ResponseEntity containing an ErrorResponse with "INVALID_INPUT" code.
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        ErrorResponse errorResponse = new ErrorResponse("INVALID_INPUT", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     * This exception is thrown when a method argument (like a @RequestParam or @PathVariable)
     * could not be converted to the required type.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The MethodArgumentTypeMismatchException instance.
     * @return A ResponseEntity containing an ErrorResponse with "TYPE_MISMATCH" code.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("The parameter '%s' has an invalid value: '%s'. Expected type: %s.",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        ErrorResponse errorResponse = new ErrorResponse("TYPE_MISMATCH", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * A generic fallback exception handler for any unhandled exceptions.
     * This should always be included as a last resort to catch any unexpected errors.
     * It returns HTTP 500 Internal Server Error and a generic message to the client,
     * while logging the full exception on the server side for debugging.
     *
     * @param ex The caught Exception instance.
     * @return A ResponseEntity containing a generic ErrorResponse with "INTERNAL_SERVER_ERROR" code.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // IMPORTANT: In a production environment, use a proper logging framework (e.g., SLF4J + Logback/Log4j2)
        // to log the full stack trace and details of the exception. Avoid System.err.
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace(); // For development purposes, print stack trace

        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}