package com.example.weighttrackercalendar.modules.auth.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAppUserRepository extends JpaRepository<AppUserJpaEntity, Long> {

    Optional<AppUserJpaEntity> findByUsername(String username);

    Optional<AppUserJpaEntity> findByEmail(String email);

    boolean existsByUsername(String username);
}
