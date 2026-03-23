package com.example.weighttrackercalendar.modules.weeklysummary.application;

import com.example.weighttrackercalendar.modules.weeklysummary.api.WeeklySummaryResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetWeeklySummariesUseCase {

    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetWeeklySummariesUseCase(
            WeightEntryRepository weightEntryRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.weightEntryRepository = weightEntryRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public List<WeeklySummaryResponse> handle(int year) {
        // El resumen semanal compara domingo final contra domingo inicial.
        // Si falta alguno de los dos pesos, la semana queda incompleta.
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = firstDay.withMonth(12).withDayOfMonth(31);
        LocalDate firstRelevantDay = firstDay.minusDays(7);

        Map<LocalDate, WeightEntry> entriesByDate = weightEntryRepository
                .findAllByUserIdAndEntryDateBetween(currentUserProvider.getCurrentUserId(), firstRelevantDay, lastDay)
                .stream()
                .collect(Collectors.toMap(WeightEntry::entryDate, Function.identity()));

        WeekFields weekFields = WeekFields.SUNDAY_START;

        return firstDay
                .datesUntil(lastDay.plusDays(1))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .map(sundayEndDate -> {
                    LocalDate sundayStartDate = sundayEndDate.minusDays(7);
                    WeightEntry startEntry = entriesByDate.get(sundayStartDate);
                    WeightEntry endEntry = entriesByDate.get(sundayEndDate);
                    boolean complete = startEntry != null && endEntry != null;
                    BigDecimal deltaKg = complete ? endEntry.weightKg().subtract(startEntry.weightKg()) : null;

                    return new WeeklySummaryResponse(
                            sundayEndDate.get(weekFields.weekOfWeekBasedYear()),
                            sundayStartDate,
                            startEntry != null ? startEntry.weightKg() : null,
                            sundayEndDate,
                            endEntry != null ? endEntry.weightKg() : null,
                            deltaKg,
                            resolveTrend(deltaKg, complete),
                            complete
                    );
                })
                .toList();
    }

    private String resolveTrend(BigDecimal deltaKg, boolean complete) {
        if (!complete || deltaKg == null) {
            return "INCOMPLETE";
        }
        int comparison = deltaKg.compareTo(BigDecimal.ZERO);
        if (comparison > 0) {
            return "UP";
        }
        if (comparison < 0) {
            return "DOWN";
        }
        return "FLAT";
    }
}

