package com.example.weighttrackercalendar.modules.weightentry.api;

import com.example.weighttrackercalendar.modules.weightentry.application.CreateWeightEntryUseCase;
import com.example.weighttrackercalendar.modules.weightentry.application.UpdateWeightEntryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weight-entries")
public class WeightEntryController {

    private final CreateWeightEntryUseCase createWeightEntryUseCase;
    private final UpdateWeightEntryUseCase updateWeightEntryUseCase;

    public WeightEntryController(
            CreateWeightEntryUseCase createWeightEntryUseCase,
            UpdateWeightEntryUseCase updateWeightEntryUseCase
    ) {
        this.createWeightEntryUseCase = createWeightEntryUseCase;
        this.updateWeightEntryUseCase = updateWeightEntryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeightEntryResponse create(@Valid @RequestBody CreateWeightEntryRequest request) {
        return createWeightEntryUseCase.handle(request);
    }

    @PutMapping("/{entryId}")
    public WeightEntryResponse update(
            @PathVariable Long entryId,
            @Valid @RequestBody UpdateWeightEntryRequest request
    ) {
        return updateWeightEntryUseCase.handle(entryId, request);
    }
}

