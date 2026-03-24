package com.example.weighttrackercalendar.modules.weightgoal.application;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightgoal.api.GetWeightGoalResponse;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class WeightGoalResponseMapperTest {

    private final WeightGoalResponseMapper mapper = new WeightGoalResponseMapper();

    @Test
    void shouldCalculateRemainingWeightAndProgressFromLatestEntry() {
        // Pasamos de 82 a 76 kg, y el último registro está en 79.
        // Eso significa que se han cubierto 3 de los 6 kg del plan: 50%.
        WeightGoal goal = new WeightGoal(7L, 1L, new BigDecimal("82.0"), new BigDecimal("76.0"), LocalDate.of(2026, 6, 1));
        WeightEntry latestEntry = new WeightEntry(11L, 1L, LocalDate.of(2026, 3, 20), new BigDecimal("79.0"), null);

        GetWeightGoalResponse response = mapper.toResponse(goal, latestEntry);

        assertThat(response.latestWeightKg()).isEqualByComparingTo("79.0");
        assertThat(response.remainingKg()).isEqualByComparingTo("-3.0");
        assertThat(response.progressPercent()).isEqualByComparingTo("50.0");
    }

    @Test
    void shouldReturnHundredPercentWhenStartAndTargetAreEqual() {
        // Si peso inicial y objetivo son iguales, el plan ya está completo por definición.
        WeightGoal goal = new WeightGoal(7L, 1L, new BigDecimal("80.0"), new BigDecimal("80.0"), null);
        WeightEntry latestEntry = new WeightEntry(11L, 1L, LocalDate.of(2026, 3, 20), new BigDecimal("80.0"), null);

        GetWeightGoalResponse response = mapper.toResponse(goal, latestEntry);

        assertThat(response.progressPercent()).isEqualByComparingTo("100");
        assertThat(response.remainingKg()).isEqualByComparingTo("0.0");
    }
}
