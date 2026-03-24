package com.example.weighttrackercalendar.modules.weeklysummary.application;

import com.example.weighttrackercalendar.modules.weeklysummary.api.WeeklySummaryResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetWeeklySummariesUseCaseTest {

    private final WeightEntryRepository weightEntryRepository = mock(WeightEntryRepository.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final GetWeeklySummariesUseCase useCase = new GetWeeklySummariesUseCase(
            weightEntryRepository,
            currentUserProvider
    );

    @Test
    void shouldMarkWeekAsCompleteWhenBothSundaysExist() {
        // Esta semana termina el 4 de enero de 2026.
        // Para calcular la variación, el caso de uso compara el domingo anterior contra ese domingo.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2025, 12, 25),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(
                entry(LocalDate.of(2025, 12, 28), "80.0"),
                entry(LocalDate.of(2026, 1, 4), "79.1")
        ));

        List<WeeklySummaryResponse> summaries = useCase.handle(2026);
        WeeklySummaryResponse firstWeek = summaries.getFirst();

        assertThat(firstWeek.complete()).isTrue();
        assertThat(firstWeek.trend()).isEqualTo("DOWN");
        assertThat(firstWeek.deltaKg()).isEqualByComparingTo("-0.9");
        assertThat(firstWeek.sundayStartDate()).isEqualTo(LocalDate.of(2025, 12, 28));
        assertThat(firstWeek.sundayEndDate()).isEqualTo(LocalDate.of(2026, 1, 4));
    }

    @Test
    void shouldMarkWeekAsIncompleteWhenOneSundayIsMissing() {
        // Si falta uno de los dos domingos, la semana no puede compararse.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2025, 12, 25),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(
                entry(LocalDate.of(2026, 1, 4), "79.1")
        ));

        List<WeeklySummaryResponse> summaries = useCase.handle(2026);
        WeeklySummaryResponse firstWeek = summaries.getFirst();

        assertThat(firstWeek.complete()).isFalse();
        assertThat(firstWeek.trend()).isEqualTo("INCOMPLETE");
        assertThat(firstWeek.deltaKg()).isNull();
        assertThat(firstWeek.sundayStartWeight()).isNull();
        assertThat(firstWeek.sundayEndWeight()).isEqualByComparingTo("79.1");
    }

    private WeightEntry entry(LocalDate date, String weightKg) {
        return new WeightEntry(1L, 1L, date, new BigDecimal(weightKg), null);
    }
}
