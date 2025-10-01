package ru.practicum.ewm.stats.server.exception;

import org.slf4j.Logger;

public class ConflictedDataException extends RuntimeException {
    public ConflictedDataException(String message, Logger log) {
        super(message);
        log.error(message, this);
    }
}
