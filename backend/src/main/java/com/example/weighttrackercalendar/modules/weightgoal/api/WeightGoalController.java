package com.example.weighttrackercalendar.modules.weightgoal.api;

import com.example.weighttrackercalendar.modules.weightgoal.application.GetWeightGoalUseCase;
import com.example.weighttrackercalendar.modules.weightgoal.application.UpsertWeightGoalUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weight-goal")
public class WeightGoalController {

    private final GetWeightGoalUseCase getWeightGoalUseCase;
    private final UpsertWeightGoalUseCase upsertWeightGoalUseCase;

    public WeightGoalController(
            GetWeightGoalUseCase getWeightGoalUseCase,
            UpsertWeightGoalUseCase upsertWeightGoalUseCase
    ) {
        this.getWeightGoalUseCase = getWeightGoalUseCase;
        this.upsertWeightGoalUseCase = upsertWeightGoalUseCase;
    }

    @GetMapping
    public GetWeightGoalResponse get() {
        return getWeightGoalUseCase.handle();
    }

    @PutMapping
    public GetWeightGoalResponse upsert(@Valid @RequestBody UpsertWeightGoalRequest request) {
        return upsertWeightGoalUseCase.handle(request);
    }
}
