package ages.hopeful.common.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpException extends RuntimeException {

  private final HttpStatus status;

  public HttpException(final String message, final HttpStatus httpStatus) {
    super(message);
    this.status = httpStatus;
  }

  public HttpException(final String message) {
    super(message);
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
