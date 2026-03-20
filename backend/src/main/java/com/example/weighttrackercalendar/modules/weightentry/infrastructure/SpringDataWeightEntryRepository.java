package com.example.weighttrackercalendar.modules.weightentry.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataWeightEntryRepository extends JpaRepository<WeightEntryJpaEntity, Long> {

    Optional<WeightEntryJpaEntity> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);

    List<WeightEntryJpaEntity> findAllByUserIdAndEntryDateBetweenOrderByEntryDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}

