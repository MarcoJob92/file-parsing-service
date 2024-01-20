package file.parsing.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IpAddressException extends RuntimeException {
    public IpAddressException (String message) {
        super(message);
    }
}
