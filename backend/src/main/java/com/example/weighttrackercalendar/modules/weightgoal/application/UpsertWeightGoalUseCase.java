package com.example.weighttrackercalendar.modules.weightgoal.application;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.modules.weightgoal.api.GetWeightGoalResponse;
import com.example.weighttrackercalendar.modules.weightgoal.api.UpsertWeightGoalRequest;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoal;
import com.example.weighttrackercalendar.modules.weightgoal.domain.WeightGoalRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UpsertWeightGoalUseCase {

    private final WeightGoalRepository weightGoalRepository;
    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WeightGoalResponseMapper weightGoalResponseMapper;

    public UpsertWeightGoalUseCase(
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

    @Transactional
    public GetWeightGoalResponse handle(UpsertWeightGoalRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();
        WeightGoal existing = weightGoalRepository.findByUserId(userId).orElse(null);
        WeightGoal saved = weightGoalRepository.save(new WeightGoal(
                existing != null ? existing.id() : null,
                userId,
                request.startWeightKg(),
                request.targetWeightKg(),
                request.targetDate()
        ));
        WeightEntry latestEntry = weightEntryRepository
                .findAllByUserIdAndEntryDateBetween(userId, LocalDate.of(2000, 1, 1), LocalDate.now())
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);
        return weightGoalResponseMapper.toResponse(saved, latestEntry);
    }
}
