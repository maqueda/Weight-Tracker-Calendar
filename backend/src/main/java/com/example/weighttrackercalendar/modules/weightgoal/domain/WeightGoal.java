package com.example.weighttrackercalendar.modules.weightgoal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeightGoal {

    private final Long id;
    private final Long userId;
    private final BigDecimal startWeightKg;
    private final BigDecimal targetWeightKg;
    private final LocalDate targetDate;

    public WeightGoal(Long id, Long userId, BigDecimal startWeightKg, BigDecimal targetWeightKg, LocalDate targetDate) {
        if (startWeightKg == null || startWeightKg.signum() <= 0) {
            throw new IllegalArgumentException("El peso inicial debe ser mayor que cero");
        }
        if (targetWeightKg == null || targetWeightKg.signum() <= 0) {
            throw new IllegalArgumentException("El peso objetivo debe ser mayor que cero");
        }
        this.id = id;
        this.userId = userId;
        this.startWeightKg = startWeightKg;
        this.targetWeightKg = targetWeightKg;
        this.targetDate = targetDate;
    }

    public Long id() { return id; }
    public Long userId() { return userId; }
    public BigDecimal startWeightKg() { return startWeightKg; }
    public BigDecimal targetWeightKg() { return targetWeightKg; }
    public LocalDate targetDate() { return targetDate; }
}
