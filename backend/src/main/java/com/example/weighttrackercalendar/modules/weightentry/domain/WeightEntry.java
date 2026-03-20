package com.example.weighttrackercalendar.modules.weightentry.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeightEntry {

    private final Long id;
    private final Long userId;
    private final LocalDate entryDate;
    private final BigDecimal weightKg;
    private final String notes;

    public WeightEntry(Long id, Long userId, LocalDate entryDate, BigDecimal weightKg, String notes) {
        if (entryDate == null) {
            throw new IllegalArgumentException("La fecha del registro es obligatoria");
        }
        if (weightKg == null || weightKg.signum() <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero");
        }
        this.id = id;
        this.userId = userId;
        this.entryDate = entryDate;
        this.weightKg = weightKg;
        this.notes = notes;
    }

    public Long id() {
        return id;
    }

    public Long userId() {
        return userId;
    }

    public LocalDate entryDate() {
        return entryDate;
    }

    public BigDecimal weightKg() {
        return weightKg;
    }

    public String notes() {
        return notes;
    }

    public WeightEntry withUpdatedWeight(BigDecimal newWeightKg, String newNotes) {
        return new WeightEntry(id, userId, entryDate, newWeightKg, newNotes);
    }
}

