package com.example.weighttrackercalendar.modules.weightgoal.infrastructure;

import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoal;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoalRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class WeightGoalRepositoryAdapter implements WeightGoalRepository {

    private final SpringDataWeightGoalRepository springDataWeightGoalRepository;

    public WeightGoalRepositoryAdapter(SpringDataWeightGoalRepository springDataWeightGoalRepository) {
        this.springDataWeightGoalRepository = springDataWeightGoalRepository;
    }

    @Override
    public Optional<WeightGoal> findByUserId(Long userId) {
        return springDataWeightGoalRepository.findByUserId(userId)
                .map(this::toDomain);
    }

    @Override
    public WeightGoal save(WeightGoal weightGoal) {
        WeightGoalJpaEntity entity = weightGoal.id() != null
                ? springDataWeightGoalRepository.findById(weightGoal.id()).orElseGet(WeightGoalJpaEntity::new)
                : new WeightGoalJpaEntity();
        entity.setId(weightGoal.id());
        entity.setUserId(weightGoal.userId());
        entity.setStartWeightKg(weightGoal.startWeightKg());
        entity.setTargetWeightKg(weightGoal.targetWeightKg());
        entity.setTargetDate(weightGoal.targetDate());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDomain(springDataWeightGoalRepository.save(entity));
    }

    private WeightGoal toDomain(WeightGoalJpaEntity entity) {
        return new WeightGoal(
                entity.getId(),
                entity.getUserId(),
                entity.getStartWeightKg(),
                entity.getTargetWeightKg(),
                entity.getTargetDate()
        );
    }
}
