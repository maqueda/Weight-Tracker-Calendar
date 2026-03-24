package com.example.weighttrackercalendar.modules.calendar.application;

import com.example.weighttrackercalendar.modules.calendar.api.YearCalendarResponse;
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

class GetYearCalendarUseCaseTest {

    private final WeightEntryRepository weightEntryRepository = mock(WeightEntryRepository.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final GetYearCalendarUseCase useCase = new GetYearCalendarUseCase(
            weightEntryRepository,
            currentUserProvider
    );

    @Test
    void shouldReturnWholeYearIncludingDaysWithoutEntries() {
        // El frontend espera un año completo para dibujar el calendario sin huecos.
        // Por eso este caso comprueba que siempre llegan los 365 días de 2026.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(
                entry(11L, LocalDate.of(2026, 1, 4), "79.8"),
                entry(12L, LocalDate.of(2026, 5, 18), "77.6")
        ));

        YearCalendarResponse response = useCase.handle(2026);

        assertThat(response.year()).isEqualTo(2026);
        assertThat(response.days()).hasSize(365);
        assertThat(response.days().getFirst().date()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(response.days().getLast().date()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void shouldMarkSundaysAndFillEntryDataOnlyForSavedDays() {
        // Además del año completo, necesitamos distinguir domingos y días con dato real.
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(weightEntryRepository.findAllByUserIdAndEntryDateBetween(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(
                entry(11L, LocalDate.of(2026, 1, 4), "79.8")
        ));

        YearCalendarResponse response = useCase.handle(2026);

        var sunday = response.days().stream()
                .filter(day -> day.date().equals(LocalDate.of(2026, 1, 4)))
                .findFirst()
                .orElseThrow();
        var monday = response.days().stream()
                .filter(day -> day.date().equals(LocalDate.of(2026, 1, 5)))
                .findFirst()
                .orElseThrow();

        assertThat(sunday.sunday()).isTrue();
        assertThat(sunday.hasEntry()).isTrue();
        assertThat(sunday.weightKg()).isEqualByComparingTo("79.8");
        assertThat(monday.sunday()).isFalse();
        assertThat(monday.hasEntry()).isFalse();
        assertThat(monday.weightKg()).isNull();
    }

    private WeightEntry entry(Long id, LocalDate date, String weightKg) {
        return new WeightEntry(id, 1L, date, new BigDecimal(weightKg), null);
    }
}
