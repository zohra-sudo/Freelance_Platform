package com.example.freelanceplatform.service;

import com.example.freelanceplatform.dao.ProjectDAO;
import com.example.freelanceplatform.entities.Project;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@Stateless
public class ProjectService {

    @Inject
    private ProjectDAO projectDAO;

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    @Transactional
    public void addProject(Project project) {
        projectDAO.save(project);  // ← utilise save() au lieu de create()
    }

    public Project findById(Long id) {
        return projectDAO.findById(id);
    }

    public List<Project> getProjectsByUserId(Long userId) {
        return projectDAO.findByUserId(userId);
    }

    @Transactional
    public void updateProject(Project project) {
        projectDAO.update(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectDAO.findById(id);  // ← trouve d'abord
        if (project != null) {
            projectDAO.delete(project);  // ← passe l'entité
        }
    }
}