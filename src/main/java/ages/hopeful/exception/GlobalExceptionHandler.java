package ages.hopeful.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // captura qualquer HttpException (Conflict, NotFound, etc)
  @ExceptionHandler(HttpException.class)
  public ResponseEntity<Map<String, Object>> handleHttpException(HttpException ex) {
    return ResponseEntity
        .status(ex.getStatus())
        .body(Map.of("error", ex.getMessage()));
  }

  // captura erros de validação do @Valid
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
        .toList();

    return ResponseEntity.badRequest().body(Map.of("errors", errors));
  }
}
