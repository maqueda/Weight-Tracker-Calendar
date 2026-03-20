package com.example.weighttrackercalendar.modules.weightentry.application;

import com.example.weighttrackercalendar.modules.weightentry.api.WeightEntryResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import org.springframework.stereotype.Component;

@Component
public class WeightEntryResponseMapper {

    public WeightEntryResponse toResponse(WeightEntry weightEntry) {
        return new WeightEntryResponse(
                weightEntry.id(),
                weightEntry.entryDate(),
                weightEntry.weightKg(),
                weightEntry.notes()
        );
    }
}

