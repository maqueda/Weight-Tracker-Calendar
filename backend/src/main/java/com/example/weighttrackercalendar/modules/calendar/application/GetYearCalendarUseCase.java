package com.example.weighttrackercalendar.modules.calendar.application;

import com.example.weighttrackercalendar.modules.calendar.api.YearCalendarResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetYearCalendarUseCase {

    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetYearCalendarUseCase(
            WeightEntryRepository weightEntryRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.weightEntryRepository = weightEntryRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public YearCalendarResponse handle(int year) {
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = firstDay.withMonth(12).withDayOfMonth(31);
        Map<LocalDate, WeightEntry> entriesByDate = weightEntryRepository
                .findAllByUserIdAndEntryDateBetween(currentUserProvider.getCurrentUserId(), firstDay, lastDay)
                .stream()
                .collect(Collectors.toMap(WeightEntry::entryDate, Function.identity()));

        List<com.example.weighttrackercalendar.modules.calendar.api.CalendarDayResponse> days = firstDay
                .datesUntil(lastDay.plusDays(1))
                .map(date -> {
                    WeightEntry entry = entriesByDate.get(date);
                    return new com.example.weighttrackercalendar.modules.calendar.api.CalendarDayResponse(
                            entry != null ? entry.id() : null,
                            date,
                            date.getMonthValue(),
                            date.getDayOfMonth(),
                            date.getDayOfWeek().name(),
                            entry != null ? entry.weightKg() : null,
                            entry != null ? entry.notes() : null,
                            entry != null,
                            date.getDayOfWeek() == DayOfWeek.SUNDAY
                    );
                })
                .toList();

        return new YearCalendarResponse(year, days);
    }
}

