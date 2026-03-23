package com.example.weighttrackercalendar.modules.weightgoal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataWeightGoalRepository extends JpaRepository<WeightGoalJpaEntity, Long> {

    Optional<WeightGoalJpaEntity> findByUserId(Long userId);
}
