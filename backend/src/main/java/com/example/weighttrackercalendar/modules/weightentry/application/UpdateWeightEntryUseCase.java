package com.example.weighttrackercalendar.modules.weightentry.application;

import com.example.weighttrackercalendar.modules.weightentry.api.UpdateWeightEntryRequest;
import com.example.weighttrackercalendar.modules.weightentry.api.WeightEntryResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryNotFoundException;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateWeightEntryUseCase {

    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WeightEntryResponseMapper weightEntryResponseMapper;

    public UpdateWeightEntryUseCase(
            WeightEntryRepository weightEntryRepository,
            CurrentUserProvider currentUserProvider,
            WeightEntryResponseMapper weightEntryResponseMapper
    ) {
        this.weightEntryRepository = weightEntryRepository;
        this.currentUserProvider = currentUserProvider;
        this.weightEntryResponseMapper = weightEntryResponseMapper;
    }

    @Transactional
    public WeightEntryResponse handle(Long entryId, UpdateWeightEntryRequest request) {
        // Solo se permite editar registros que pertenecen al usuario actual.
        Long userId = currentUserProvider.getCurrentUserId();
        WeightEntry existing = weightEntryRepository.findById(entryId)
                .filter(weightEntry -> weightEntry.userId().equals(userId))
                .orElseThrow(() -> new WeightEntryNotFoundException("No existe el registro " + entryId));

        // Se conserva la fecha original y solo cambian peso y notas.
        WeightEntry saved = weightEntryRepository.save(existing.withUpdatedWeight(request.weightKg(), request.notes()));
        return weightEntryResponseMapper.toResponse(saved);
    }
}

