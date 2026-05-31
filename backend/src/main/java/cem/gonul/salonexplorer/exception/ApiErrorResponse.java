package cem.gonul.salonexplorer.exception;

import java.util.Map;

public record ApiErrorResponse(
        String message,
        Map<String, String> errors
) {
}
