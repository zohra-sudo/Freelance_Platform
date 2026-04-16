package com.example.freelanceplatform.service;

import com.example.freelanceplatform.entities.Application;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ApplicationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Application application) {
        entityManager.persist(application);
    }

    public boolean hasApplied(Long projectId, Long freelancerId) {
        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(a) FROM Application a WHERE a.project.id = :projectId AND a.freelancer.id = :freelancerId",
                            Long.class)
                    .setParameter("projectId", projectId)
                    .setParameter("freelancerId", freelancerId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Application> findByProjectId(Long projectId) {
        return entityManager.createQuery(
                        "SELECT a FROM Application a WHERE a.project.id = :projectId ORDER BY a.createdAt DESC",
                        Application.class)
                .setParameter("projectId", projectId)
                .getResultList();
    }

    public List<Application> findByFreelancerId(Long freelancerId) {
        return entityManager.createQuery(
                        "SELECT a FROM Application a WHERE a.freelancer.id = :freelancerId ORDER BY a.createdAt DESC",
                        Application.class)
                .setParameter("freelancerId", freelancerId)
                .getResultList();
    }
}