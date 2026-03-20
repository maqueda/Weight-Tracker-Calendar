package com.example.weighttrackercalendar.modules.weightentry.infrastructure;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import org.springframework.stereotype.Component;

@Component
public class WeightEntryMapper {

    public WeightEntry toDomain(WeightEntryJpaEntity entity) {
        return new WeightEntry(
                entity.getId(),
                entity.getUserId(),
                entity.getEntryDate(),
                entity.getWeightKg(),
                entity.getNotes()
        );
    }

    public WeightEntryJpaEntity toEntity(WeightEntry weightEntry) {
        WeightEntryJpaEntity entity = new WeightEntryJpaEntity();
        entity.setId(weightEntry.id());
        entity.setUserId(weightEntry.userId());
        entity.setEntryDate(weightEntry.entryDate());
        entity.setWeightKg(weightEntry.weightKg());
        entity.setNotes(weightEntry.notes());
        return entity;
    }
}

