package ages.hopeful.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(HttpException ex) {
        return new ResponseEntity<>(
            new ErrorResponse(
                ex.getStatus().getReasonPhrase(),
                ex.getMessage()
            ),
            ex.getStatus()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
        BadCredentialsException ex
    ) {
        return new ResponseEntity<>(
            new ErrorResponse(
                "Unauthorized",
                "Username or password is invalid"
            ),
            HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
        AccessDeniedException ex
    ) {
        return new ResponseEntity<>(
            new ErrorResponse(
                "Forbidden",
                "Você não tem permissão para acessar esta rota"
            ),
            HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex
    ) {
        String message = "Malformed request";
        if (ex.getCause() instanceof InvalidFormatException invalidFormat) {
            Class<?> targetType = invalidFormat.getTargetType();
            String field = invalidFormat.getPath().get(0).getFieldName();
            if (targetType == java.util.UUID.class) {
                message = "The field '" + field + "' must be a valid UUID";
            }
        }
        return new ResponseEntity<>(
            new ErrorResponse("Bad Request", message),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            sb
                .append(fieldError.getField())
                .append(": ")
                .append(fieldError.getDefaultMessage())
                .append("; ");
        }
        return new ResponseEntity<>(
            new ErrorResponse("Bad Request", sb.toString()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex
    ) {
        return new ResponseEntity<>(
            new ErrorResponse("Bad Request", ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
        EntityNotFoundException ex
    ) {
        return new ResponseEntity<>(
            new ErrorResponse("Not Found", ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return new ResponseEntity<>(
            new ErrorResponse("Internal Server Error", ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
        AuthorizationDeniedException ex
    ) {
        return new ResponseEntity<>(
            new ErrorResponse("Forbidden", "Access denied"),
            HttpStatus.FORBIDDEN
        );
    }

    // Classe ErrorResponse normal, com getters e setters
    public static class ErrorResponse {

        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
