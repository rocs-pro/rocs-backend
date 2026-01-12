package com.nsbm.rocs.auth.repo;

import com.nsbm.rocs.entity.main.UserProfile;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@NullMarked
public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByEmail(String email);
}
