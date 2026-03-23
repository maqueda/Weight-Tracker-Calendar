package com.example.weighttrackercalendar.modules.weightgoal.application;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.modules.weightgoal.api.GetWeightGoalResponse;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoal;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoalRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class GetWeightGoalUseCase {

    private final WeightGoalRepository weightGoalRepository;
    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WeightGoalResponseMapper weightGoalResponseMapper;

    public GetWeightGoalUseCase(
            WeightGoalRepository weightGoalRepository,
            WeightEntryRepository weightEntryRepository,
            CurrentUserProvider currentUserProvider,
            WeightGoalResponseMapper weightGoalResponseMapper
    ) {
        this.weightGoalRepository = weightGoalRepository;
        this.weightEntryRepository = weightEntryRepository;
        this.currentUserProvider = currentUserProvider;
        this.weightGoalResponseMapper = weightGoalResponseMapper;
    }

    public GetWeightGoalResponse handle() {
        Long userId = currentUserProvider.getCurrentUserId();
        WeightGoal goal = weightGoalRepository.findByUserId(userId)
                .orElseGet(() -> weightGoalRepository.save(
                        new WeightGoal(null, userId, BigDecimal.valueOf(80), BigDecimal.valueOf(75), LocalDate.now().plusMonths(3))
                ));
        WeightEntry latestEntry = findLatestEntry(userId);
        return weightGoalResponseMapper.toResponse(goal, latestEntry);
    }

    private WeightEntry findLatestEntry(Long userId) {
        return weightEntryRepository
                .findAllByUserIdAndEntryDateBetween(userId, LocalDate.of(2000, 1, 1), LocalDate.now())
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);
    }
}
