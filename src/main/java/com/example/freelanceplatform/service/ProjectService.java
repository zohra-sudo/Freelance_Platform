package com.example.freelanceplatform.service;

import com.example.freelanceplatform.entities.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class ProjectService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Project> getAllProjects() {
        return entityManager.createQuery(
                "SELECT p FROM Project p ORDER BY p.id DESC",
                Project.class
        ).getResultList();
    }

    @Transactional
    public void addProject(Project project) {
        entityManager.persist(project);
    }

    public Project findById(Long id) {
        return entityManager.find(Project.class, id);
    }
}