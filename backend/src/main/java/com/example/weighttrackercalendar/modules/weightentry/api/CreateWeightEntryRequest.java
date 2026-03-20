package com.example.weighttrackercalendar.modules.weightentry.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateWeightEntryRequest(
        @NotNull LocalDate entryDate,
        @NotNull @DecimalMin("0.01") BigDecimal weightKg,
        String notes
) {
}

