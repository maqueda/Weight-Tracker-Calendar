package com.example.weighttrackercalendar.modules.weightgoal.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weight_goal")
public class WeightGoalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "weight_goal_seq")
    @SequenceGenerator(name = "weight_goal_seq", sequenceName = "weight_goal_seq", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal startWeightKg;

    @Column(name = "target_weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal targetWeightKg;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getStartWeightKg() { return startWeightKg; }
    public void setStartWeightKg(BigDecimal startWeightKg) { this.startWeightKg = startWeightKg; }
    public BigDecimal getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(BigDecimal targetWeightKg) { this.targetWeightKg = targetWeightKg; }
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
