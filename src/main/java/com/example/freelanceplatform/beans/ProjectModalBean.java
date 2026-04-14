package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.ejb.EJB;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class ProjectModalBean implements Serializable {

    @EJB
    private ProjectService projectService;

    private Project selectedProject;

    // Charge le projet sélectionné par son ID
    public void loadProject(Long projectId) {
        if (projectId != null) {
            selectedProject = projectService.findById(projectId);
        }
    }

    // Retourne les skills sous forme de liste
    public List<String> getSkillsList() {
        if (selectedProject == null || selectedProject.getSkills() == null
                || selectedProject.getSkills().trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(selectedProject.getSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public Project getSelectedProject() { return selectedProject; }
    public void setSelectedProject(Project p) { this.selectedProject = p; }
}