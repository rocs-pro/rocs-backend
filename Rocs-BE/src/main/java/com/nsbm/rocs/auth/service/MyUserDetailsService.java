package com.nsbm.rocs.auth.service;

import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.enums.AccountStatus;
import com.nsbm.rocs.auth.repo.UserProfileRepo;
import lombok.Data;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Data
@Component
@NullMarked
public class MyUserDetailsService implements UserDetailsService {

    private final UserProfileRepo userProfileRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserProfile> userProfile = userProfileRepo.findByUsername(username);
        UserProfile existUser = userProfile.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));

        if (existUser.getAccountStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account status not active");
        }
        return existUser;
    }
}
