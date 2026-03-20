package com.example.weighttrackercalendar.modules.calendar.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalendarDayResponse(
        Long entryId,
        LocalDate date,
        Integer month,
        Integer dayOfMonth,
        String dayOfWeek,
        BigDecimal weightKg,
        String notes,
        boolean hasEntry,
        boolean sunday
) {
}

