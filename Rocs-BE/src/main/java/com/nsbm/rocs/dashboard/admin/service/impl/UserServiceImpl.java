package com.nsbm.rocs.dashboard.admin.service.impl;

import com.nsbm.rocs.dashboard.admin.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Return total number of user profiles.
     */
    @Override
    public Long getAllUserCount() {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM UserProfile u", Long.class)
                .getSingleResult();
        return count != null ? count : 0L;
    }
}
