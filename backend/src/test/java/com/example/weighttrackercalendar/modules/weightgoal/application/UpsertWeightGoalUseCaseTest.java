package com.example.weighttrackercalendar.modules.weightgoal.application;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.modules.weightgoal.api.GetWeightGoalResponse;
import com.example.weighttrackercalendar.modules.weightgoal.api.UpsertWeightGoalRequest;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoal;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoalRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpsertWeightGoalUseCaseTest {

    private final WeightGoalRepository weightGoalRepository = mock(WeightGoalRepository.class);
    private final WeightEntryRepository weightEntryRepository = mock(WeightEntryRepository.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final WeightGoalResponseMapper weightGoalResponseMapper = new WeightGoalResponseMapper();
    private final UpsertWeightGoalUseCase useCase = new UpsertWeightGoalUseCase(
            weightGoalRepository,
            weightEntryRepository,
            currentUserProvider,
            weightGoalResponseMapper
    );

    @Test
    void shouldCreateGoalAndReturnProgressUsingLatestWeight() {
        // El objetivo se guarda y, en la misma respuesta, se devuelve el último peso
        // para que la pantalla muestre progreso sin hacer otra llamada.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightGoalRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(weightGoalRepository.save(any(WeightGoal.class))).thenAnswer(invocation -> {
            WeightGoal goal = invocation.getArgument(0);
            return new WeightGoal(7L, goal.userId(), goal.startWeightKg(), goal.targetWeightKg(), goal.targetDate());
        });
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2000, 1, 1),
                LocalDate.now()
        )).thenReturn(List.of(
                entry(LocalDate.of(2026, 3, 1), "80.0"),
                entry(LocalDate.of(2026, 3, 20), "78.5")
        ));

        GetWeightGoalResponse response = useCase.handle(new UpsertWeightGoalRequest(
                new BigDecimal("82.0"),
                new BigDecimal("76.0"),
                LocalDate.of(2026, 6, 1)
        ));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.startWeightKg()).isEqualByComparingTo("82.0");
        assertThat(response.targetWeightKg()).isEqualByComparingTo("76.0");
        assertThat(response.latestWeightKg()).isEqualByComparingTo("78.5");
        assertThat(response.remainingKg()).isEqualByComparingTo("-2.5");
        assertThat(response.progressPercent()).isEqualByComparingTo("58.3");
    }

    @Test
    void shouldKeepExistingIdWhenUpdatingGoal() {
        // Si el usuario ya tenía un objetivo, actualizamos sus datos pero conservamos el mismo id.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightGoalRepository.findByUserId(1L)).thenReturn(Optional.of(
                new WeightGoal(9L, 1L, new BigDecimal("81.0"), new BigDecimal("77.0"), null)
        ));
        when(weightGoalRepository.save(any(WeightGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2000, 1, 1),
                LocalDate.now()
        )).thenReturn(List.of());

        GetWeightGoalResponse response = useCase.handle(new UpsertWeightGoalRequest(
                new BigDecimal("80.0"),
                new BigDecimal("75.0"),
                LocalDate.of(2026, 9, 1)
        ));

        assertThat(response.id()).isEqualTo(9L);
        assertThat(response.startWeightKg()).isEqualByComparingTo("80.0");
        assertThat(response.targetWeightKg()).isEqualByComparingTo("75.0");
        assertThat(response.latestWeightKg()).isNull();
        assertThat(response.progressPercent()).isNull();
    }

    private WeightEntry entry(LocalDate date, String weightKg) {
        return new WeightEntry(1L, 1L, date, new BigDecimal(weightKg), null);
    }
}
