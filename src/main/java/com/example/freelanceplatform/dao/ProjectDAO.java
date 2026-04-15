package com.example.freelanceplatform.dao;

import com.example.freelanceplatform.entities.Project;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class ProjectDAO extends GenericDAO<Project> {

    public ProjectDAO() {
        super(Project.class);
    }

    public List<Project> findByUserId(Long userId) {
        return em.createQuery("SELECT p FROM Project p WHERE p.author.id = :userId", Project.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}