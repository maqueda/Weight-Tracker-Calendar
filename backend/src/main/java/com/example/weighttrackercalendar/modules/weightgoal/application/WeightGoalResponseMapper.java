package com.example.weighttrackercalendar.modules.weightgoal.application;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightgoal.api.GetWeightGoalResponse;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class WeightGoalResponseMapper {

    public GetWeightGoalResponse toResponse(WeightGoal goal, WeightEntry latestEntry) {
        BigDecimal latestWeight = latestEntry != null ? latestEntry.weightKg() : null;
        BigDecimal remainingKg = latestWeight != null ? goal.targetWeightKg().subtract(latestWeight) : null;
        BigDecimal progressPercent = latestWeight != null ? calculateProgress(goal, latestWeight) : null;
        return new GetWeightGoalResponse(
                goal.id(),
                goal.startWeightKg(),
                goal.targetWeightKg(),
                goal.targetDate(),
                latestWeight,
                remainingKg,
                progressPercent
        );
    }

    private BigDecimal calculateProgress(WeightGoal goal, BigDecimal latestWeight) {
        BigDecimal totalChange = goal.startWeightKg().subtract(goal.targetWeightKg()).abs();
        if (totalChange.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        BigDecimal coveredChange = goal.startWeightKg().subtract(latestWeight).abs();
        return coveredChange.multiply(BigDecimal.valueOf(100))
                .divide(totalChange, 1, RoundingMode.HALF_UP)
                .min(BigDecimal.valueOf(100));
    }
}
