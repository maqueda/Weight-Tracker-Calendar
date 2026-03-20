package com.example.weighttrackercalendar.modules.weightentry.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateWeightEntryRequest(
        @NotNull @DecimalMin("0.01") BigDecimal weightKg,
        String notes
) {
}

