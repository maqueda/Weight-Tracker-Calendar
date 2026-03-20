package com.example.weighttrackercalendar.modules.weightentry.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightEntryRepository {

    WeightEntry save(WeightEntry weightEntry);

    Optional<WeightEntry> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);

    Optional<WeightEntry> findById(Long id);

    List<WeightEntry> findAllByUserIdAndEntryDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}

