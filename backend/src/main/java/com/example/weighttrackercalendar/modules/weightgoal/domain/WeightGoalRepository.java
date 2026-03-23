package com.example.weighttrackercalendar.modules.weightgoal.domain;

import java.util.Optional;

public interface WeightGoalRepository {

    Optional<WeightGoal> findByUserId(Long userId);

    WeightGoal save(WeightGoal weightGoal);
}
