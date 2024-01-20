package file.parsing.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidIpAddressException extends RuntimeException {
    public InvalidIpAddressException (String message) {
        super(message);
    }
}

