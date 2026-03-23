package com.example.weighttrackercalendar.modules.monthlysummary.api;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        int month,
        String monthLabel,
        int entriesCount,
        BigDecimal averageWeightKg,
        BigDecimal minWeightKg,
        BigDecimal maxWeightKg,
        BigDecimal deltaKg
) {
}
