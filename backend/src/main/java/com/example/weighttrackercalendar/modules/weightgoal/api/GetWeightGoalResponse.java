package com.example.weighttrackercalendar.modules.weightgoal.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetWeightGoalResponse(
        Long id,
        BigDecimal startWeightKg,
        BigDecimal targetWeightKg,
        LocalDate targetDate,
        BigDecimal latestWeightKg,
        BigDecimal remainingKg,
        BigDecimal progressPercent
) {
}
