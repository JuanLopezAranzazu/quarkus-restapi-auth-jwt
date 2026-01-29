package org.juanlopezaranzazu.exception;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalExceptionHandler {

    @Context
    UriInfo uriInfo;

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                404,
                "Not Found",
                ex.getMessage(),
                getPath()
        );
        return RestResponse.status(RestResponse.Status.NOT_FOUND, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                getPath()
        );
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                401,
                "Unauthorized",
                ex.getMessage(),
                getPath()
        );
        return RestResponse.status(RestResponse.Status.UNAUTHORIZED, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> validationErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(java.time.LocalDateTime.now());
        error.setStatus(400);
        error.setError("Validation Failed");
        error.setMessage("Invalid input data");
        error.setPath(getPath());
        error.setValidationErrors(validationErrors);

        return RestResponse.status(RestResponse.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                getPath()
        );
        return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, error);
    }

    private String getPath() {
        return uriInfo != null ? uriInfo.getPath() : "unknown";
    }
}
