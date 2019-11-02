package club.charliefeng.kafkademoappengine.handler;

import club.charliefeng.kafkademoappengine.error.ApiError;
import club.charliefeng.kafkademoappengine.exception.EntityNotFoundException;
import club.charliefeng.kafkademoappengine.exception.SchemaNotCompatibleException;
import club.charliefeng.kafkademoappengine.exception.ServiceUnavailableException;
import club.charliefeng.kafkademoappengine.exception.ValidationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SchemaNotCompatibleException.class)
    protected ResponseEntity<ApiError> handleSchemaNotCompatible(SchemaNotCompatibleException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    protected ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex) {
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex.getCause());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
        return buildResponseEntity(ex.getError());
    }


}
