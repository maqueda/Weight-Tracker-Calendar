package com.example.weighttrackercalendar.modules.weightgoal.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpsertWeightGoalRequest(
        @NotNull @DecimalMin("0.01") BigDecimal startWeightKg,
        @NotNull @DecimalMin("0.01") BigDecimal targetWeightKg,
        LocalDate targetDate
) {
}
