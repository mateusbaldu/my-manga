package fatecipi.progweb.mymanga.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailSenderException extends RuntimeException {
    public EmailSenderException(String message) {
        super(message);
    }
}
