package com.example.weighttrackercalendar.modules.auth.domain;

import java.util.Optional;

public interface AppUserRepository {

    Optional<AppUser> findById(Long id);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    AppUser save(AppUser appUser);
}
