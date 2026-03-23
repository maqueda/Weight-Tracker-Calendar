package com.example.weighttrackercalendar.modules.weightentry.application;

import com.example.weighttrackercalendar.modules.weightentry.api.CreateWeightEntryRequest;
import com.example.weighttrackercalendar.modules.weightentry.api.WeightEntryResponse;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntry;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryAlreadyExistsException;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWeightEntryUseCase {

    private final WeightEntryRepository weightEntryRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WeightEntryResponseMapper weightEntryResponseMapper;

    public CreateWeightEntryUseCase(
            WeightEntryRepository weightEntryRepository,
            CurrentUserProvider currentUserProvider,
            WeightEntryResponseMapper weightEntryResponseMapper
    ) {
        this.weightEntryRepository = weightEntryRepository;
        this.currentUserProvider = currentUserProvider;
        this.weightEntryResponseMapper = weightEntryResponseMapper;
    }

    @Transactional
    public WeightEntryResponse handle(CreateWeightEntryRequest request) {
        // La aplicación solo permite un peso por usuario y fecha.
        Long userId = currentUserProvider.getCurrentUserId();
        weightEntryRepository.findByUserIdAndEntryDate(userId, request.entryDate())
                .ifPresent(existing -> {
                    throw new WeightEntryAlreadyExistsException("Ya existe un registro para la fecha " + request.entryDate());
                });

        // Si no existe, se crea el registro diario y se devuelve listo para la API.
        WeightEntry saved = weightEntryRepository.save(
                new WeightEntry(null, userId, request.entryDate(), request.weightKg(), request.notes())
        );
        return weightEntryResponseMapper.toResponse(saved);
    }
}

