package com.example.weighttrackercalendar.modules.monthlysummary.application;

import com.example.weighttrackercalendar.modules.monthlysummary.api.MonthlySummaryResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetMonthlySummariesUseCase {

    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetMonthlySummariesUseCase(
            WeightEntryRepository weightEntryRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.weightEntryRepository = weightEntryRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public List<MonthlySummaryResponse> handle(int year) {
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = firstDay.withMonth(12).withDayOfMonth(31);

        // El backend entrega el mes ya agregado para que el frontend pueda
        // centrarse en la visualización y no en la estadística.
        Map<Integer, List<WeightEntry>> entriesByMonth = weightEntryRepository
                .findAllByUserIdAndEntryDateBetween(currentUserProvider.getCurrentUserId(), firstDay, lastDay)
                .stream()
                .collect(Collectors.groupingBy(entry -> entry.entryDate().getMonthValue()));

        return java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(month -> toResponse(month, entriesByMonth.getOrDefault(month, List.of())))
                .toList();
    }

    private MonthlySummaryResponse toResponse(int monthNumber, List<WeightEntry> entries) {
        if (entries.isEmpty()) {
            return new MonthlySummaryResponse(
                    monthNumber,
                    Month.of(monthNumber).getDisplayName(TextStyle.SHORT, new Locale("es", "ES")),
                    0,
                    null,
                    null,
                    null,
                    null
            );
        }

        BigDecimal average = entries.stream()
                .map(WeightEntry::weightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(entries.size()), 2, RoundingMode.HALF_UP);
        BigDecimal min = entries.stream().map(WeightEntry::weightKg).min(Comparator.naturalOrder()).orElse(null);
        BigDecimal max = entries.stream().map(WeightEntry::weightKg).max(Comparator.naturalOrder()).orElse(null);
        BigDecimal delta = entries.get(entries.size() - 1).weightKg().subtract(entries.get(0).weightKg());

        return new MonthlySummaryResponse(
                monthNumber,
                Month.of(monthNumber).getDisplayName(TextStyle.SHORT, new Locale("es", "ES")),
                entries.size(),
                average,
                min,
                max,
                delta
        );
    }
}
