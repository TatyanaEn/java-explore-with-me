package ru.practicum.ewm.service.util;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateConstant {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN)
            .withZone(ZoneOffset.UTC);

    private DateConstant() {
    }
}