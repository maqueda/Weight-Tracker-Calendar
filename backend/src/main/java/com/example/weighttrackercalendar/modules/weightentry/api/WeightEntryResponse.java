package com.example.weighttrackercalendar.modules.weightentry.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightEntryResponse(
        Long id,
        LocalDate entryDate,
        BigDecimal weightKg,
        String notes
) {
}

