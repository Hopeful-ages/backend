package ages.hopeful.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ages.hopeful.shared.exception.HttpException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // Captura a exceção HttpException
  @ExceptionHandler(HttpException.class)
  public ResponseEntity<?> handleHttpException(HttpException ex) {
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), ex.getStatus()), ex.getStatus());
  }

  // Captura exceções genéricas
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleException(Exception ex) {
    return new ResponseEntity<>(new ErrorResponse("Erro interno do servidor",
            HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // Captura falhas de validação e retorna mensagens específicas
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "BAD_REQUEST");

    // Cria um mapa para armazenar as mensagens de erro
    Map<String, String> errors = new HashMap<>();

    // Percorre os erros de campo e adiciona ao mapa
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    response.put("errors", errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // captura Illegal arguments exceptions, necessária para a validação do parse de 'string' → DTO
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "BAD_REQUEST");
    response.put("error", ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // Classe para responder com mensagem e status
  public static class ErrorResponse {
    private String message;
    private HttpStatus status;

    public ErrorResponse(String message, HttpStatus status) {
      this.message = message;
      this.status = status;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public HttpStatus getStatus() {
      return status;
    }

    public void setStatus(HttpStatus status) {
      this.status = status;
    }
  }
}
