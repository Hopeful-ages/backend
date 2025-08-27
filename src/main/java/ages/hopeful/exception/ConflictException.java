package ages.hopeful.exception;

import org.springframework.http.HttpStatus;
public class ConflictException extends HttpException {
  public ConflictException(String message) { super(message, HttpStatus.CONFLICT); }
}
