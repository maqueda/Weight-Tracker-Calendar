package com.example.weighttrackercalendar.modules.weeklysummary.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeeklySummaryResponse(
        int weekNumber,
        LocalDate sundayStartDate,
        BigDecimal sundayStartWeight,
        LocalDate sundayEndDate,
        BigDecimal sundayEndWeight,
        BigDecimal deltaKg,
        String trend,
        boolean complete
) {
}

