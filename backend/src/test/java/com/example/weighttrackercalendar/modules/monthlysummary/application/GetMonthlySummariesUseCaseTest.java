package com.example.weighttrackercalendar.modules.monthlysummary.application;

import com.example.weighttrackercalendar.modules.monthlysummary.api.MonthlySummaryResponse;
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

class GetMonthlySummariesUseCaseTest {

    private final WeightEntryRepository weightEntryRepository = mock(WeightEntryRepository.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final GetMonthlySummariesUseCase useCase = new GetMonthlySummariesUseCase(
            weightEntryRepository,
            currentUserProvider
    );

    @Test
    void shouldCalculateAverageMinMaxAndDeltaForMonthWithEntries() {
        // Enero tiene tres registros: queremos comprobar que el resumen mensual
        // devuelve los agregados más importantes de forma clara.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(
                entry(LocalDate.of(2026, 1, 3), "80.0"),
                entry(LocalDate.of(2026, 1, 10), "79.5"),
                entry(LocalDate.of(2026, 1, 25), "79.0")
        ));

        List<MonthlySummaryResponse> summaries = useCase.handle(2026);
        MonthlySummaryResponse january = summaries.getFirst();

        assertThat(january.month()).isEqualTo(1);
        assertThat(january.entriesCount()).isEqualTo(3);
        assertThat(january.averageWeightKg()).isEqualByComparingTo("79.50");
        assertThat(january.minWeightKg()).isEqualByComparingTo("79.0");
        assertThat(january.maxWeightKg()).isEqualByComparingTo("80.0");
        assertThat(january.deltaKg()).isEqualByComparingTo("-1.0");
    }

    @Test
    void shouldReturnEmptySummaryForMonthWithoutEntries() {
        // Cuando no hay registros, el frontend necesita igualmente los 12 meses,
        // pero con los campos numéricos vacíos.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of());

        List<MonthlySummaryResponse> summaries = useCase.handle(2026);
        MonthlySummaryResponse january = summaries.getFirst();

        assertThat(summaries).hasSize(12);
        assertThat(january.month()).isEqualTo(1);
        assertThat(january.entriesCount()).isZero();
        assertThat(january.averageWeightKg()).isNull();
        assertThat(january.minWeightKg()).isNull();
        assertThat(january.maxWeightKg()).isNull();
        assertThat(january.deltaKg()).isNull();
    }

    private WeightEntry entry(LocalDate date, String weightKg) {
        return new WeightEntry(1L, 1L, date, new BigDecimal(weightKg), null);
    }
}
