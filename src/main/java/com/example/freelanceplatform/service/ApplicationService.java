package com.example.freelanceplatform.service;

import com.example.freelanceplatform.entities.Application;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ApplicationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Application application) {
        entityManager.persist(application);
    }
}