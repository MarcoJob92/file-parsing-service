package file.parsing.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MalformedFileException extends RuntimeException {
    public MalformedFileException (String message) {
        super(message);
    }
}
