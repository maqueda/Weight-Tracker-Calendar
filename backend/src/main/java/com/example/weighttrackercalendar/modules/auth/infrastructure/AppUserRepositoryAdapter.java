package com.example.weighttrackercalendar.modules.auth.infrastructure;

import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AppUserRepositoryAdapter implements AppUserRepository {

    private final SpringDataAppUserRepository repository;

    public AppUserRepositoryAdapter(SpringDataAppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public AppUser save(AppUser appUser) {
        AppUserJpaEntity entity = new AppUserJpaEntity();
        entity.setId(appUser.id());
        entity.setUsername(appUser.username());
        entity.setDisplayName(appUser.displayName());
        entity.setEmail(appUser.email());
        entity.setTimezone(appUser.timezone());
        entity.setPasswordHash(appUser.passwordHash());
        entity.setActive(appUser.active());
        entity.setCreatedAt(appUser.createdAt());
        entity.setUpdatedAt(appUser.updatedAt());
        return toDomain(repository.save(entity));
    }

    private AppUser toDomain(AppUserJpaEntity entity) {
        return new AppUser(
                entity.getId(),
                entity.getUsername(),
                entity.getDisplayName(),
                entity.getEmail(),
                entity.getTimezone(),
                entity.getPasswordHash(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
