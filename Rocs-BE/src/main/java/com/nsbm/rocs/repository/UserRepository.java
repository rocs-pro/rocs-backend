package com.nsbm.rocs.repository;

import com.nsbm.rocs.entity.main.UserProfile; // Ensure User entity exists
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository {
    // Corresponds to Step 2: SELECT * FROM user_profiles WHERE username = ...
    Optional<UserProfile> findByUsername(String username);
}
