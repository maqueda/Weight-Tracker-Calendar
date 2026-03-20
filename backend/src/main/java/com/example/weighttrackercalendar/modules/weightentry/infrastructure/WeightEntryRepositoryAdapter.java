package com.example.weighttrackercalendar.modules.weightentry.infrastructure;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class WeightEntryRepositoryAdapter implements WeightEntryRepository {

    private final SpringDataWeightEntryRepository springDataWeightEntryRepository;
    private final WeightEntryMapper weightEntryMapper;

    public WeightEntryRepositoryAdapter(
            SpringDataWeightEntryRepository springDataWeightEntryRepository,
            WeightEntryMapper weightEntryMapper
    ) {
        this.springDataWeightEntryRepository = springDataWeightEntryRepository;
        this.weightEntryMapper = weightEntryMapper;
    }

    @Override
    public WeightEntry save(WeightEntry weightEntry) {
        WeightEntryJpaEntity entity = weightEntry.id() != null
                ? springDataWeightEntryRepository.findById(weightEntry.id()).orElseGet(WeightEntryJpaEntity::new)
                : new WeightEntryJpaEntity();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(weightEntry.id());
        entity.setUserId(weightEntry.userId());
        entity.setEntryDate(weightEntry.entryDate());
        entity.setWeightKg(weightEntry.weightKg());
        entity.setNotes(weightEntry.notes());
        entity.setCreatedAt(entity.getCreatedAt() == null ? now : entity.getCreatedAt());
        entity.setUpdatedAt(now);

        WeightEntryJpaEntity saved = springDataWeightEntryRepository.save(entity);
        return weightEntryMapper.toDomain(saved);
    }

    @Override
    public Optional<WeightEntry> findByUserIdAndEntryDate(Long userId, LocalDate entryDate) {
        return springDataWeightEntryRepository.findByUserIdAndEntryDate(userId, entryDate)
                .map(weightEntryMapper::toDomain);
    }

    @Override
    public Optional<WeightEntry> findById(Long id) {
        return springDataWeightEntryRepository.findById(id)
                .map(weightEntryMapper::toDomain);
    }

    @Override
    public List<WeightEntry> findAllByUserIdAndEntryDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return springDataWeightEntryRepository
                .findAllByUserIdAndEntryDateBetweenOrderByEntryDateAsc(userId, startDate, endDate)
                .stream()
                .map(weightEntryMapper::toDomain)
                .toList();
    }
}

