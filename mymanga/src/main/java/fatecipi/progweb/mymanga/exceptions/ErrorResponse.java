package fatecipi.progweb.mymanga.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ErrorResponse(
        int status,
        String timestamp,
        String error,
        String message,
        List<?> errors
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SS dd/MM/yyyy");

    public ErrorResponse(int status, String error, String message, List<?> errors) {
        this(status, LocalDateTime.now().format(FORMATTER), error, message, errors);
    }

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null);
    }
}
