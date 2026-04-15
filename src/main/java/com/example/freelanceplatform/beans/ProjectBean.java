package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class ProjectBean implements Serializable {

    @Inject
    private ProjectService projectService;

    @Inject // <--- CETTE LIGNE ÉTAIT MANQUANTE : Elle permet de lier le Bean de session
    private AuthBean authBean;

    private Project project = new Project();

    private String searchKeyword;
    private String selectedCategory;

    private Long projectId;
    private Project selectedProject;

    private boolean showAll = false;

    // --- LOGIQUE D'AFFICHAGE (STRICTEMENT IDENTIQUE) ---

    public List<Project> getProjects() {
        List<Project> projects = projectService.getAllProjects();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toLowerCase();
            projects = projects.stream()
                    .filter(p ->
                            (p.getTitle() != null && p.getTitle().toLowerCase().contains(keyword)) ||
                                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword)) ||
                                    (p.getCategory() != null && p.getCategory().toLowerCase().contains(keyword)) ||
                                    (p.getSkills() != null && p.getSkills().toLowerCase().contains(keyword))
                    )
                    .collect(Collectors.toList());
        }

        if (selectedCategory != null && !selectedCategory.trim().isEmpty()) {
            projects = projects.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(selectedCategory))
                    .collect(Collectors.toList());
        }

        if (!showAll && projects.size() > 4) {
            return projects.subList(0, 4);
        }

        return projects;
    }

    public int getTotalProjectsCount() {
        List<Project> projects = projectService.getAllProjects();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toLowerCase();
            projects = projects.stream()
                    .filter(p ->
                            (p.getTitle() != null && p.getTitle().toLowerCase().contains(keyword)) ||
                                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword)) ||
                                    (p.getCategory() != null && p.getCategory().toLowerCase().contains(keyword)) ||
                                    (p.getSkills() != null && p.getSkills().toLowerCase().contains(keyword))
                    )
                    .collect(Collectors.toList());
        }

        if (selectedCategory != null && !selectedCategory.trim().isEmpty()) {
            projects = projects.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(selectedCategory))
                    .collect(Collectors.toList());
        }

        return projects.size();
    }

    public boolean isHasMoreThanFour() {
        return getTotalProjectsCount() > 4;
    }

    // --- LOGIQUE DE CRÉATION ---

    public String createProject() {
        // Désormais authBean n'est plus null grâce au @Inject
        if (authBean != null && authBean.isConnecte()) {
            project.setAuthor(authBean.getUserConnecte());
            projectService.addProject(project);
            project = new Project();
            return "jobs?faces-redirect=true";
        }
        return "login?faces-redirect=true";
    }

    // --- LE RESTE DU CODE (IDENTIQUE) ---

    public String showMore() { this.showAll = true; return null; }
    public String showLess() { this.showAll = false; return null; }

    public List<String> skillsList(String skills) {
        if (skills == null || skills.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(skills.split("\\s*,\\s*"));
    }

    public String filterByCategory(String category) {
        this.selectedCategory = category;
        this.showAll = false;
        return null;
    }

    public String clearCategory() {
        this.selectedCategory = null;
        this.showAll = false;
        return null;
    }

    public String search() {
        this.showAll = false;
        return null;
    }

    public void loadProjectById() {
        if (projectId != null) {
            selectedProject = projectService.findById(projectId);
        }
    }

    public String goToProjectDetails(Long id) {
        return "projectDetails?faces-redirect=true&id=" + id;
    }
    // Ajoute cette méthode pour capturer le paramètre au chargement de la page
    public void filterOnLoad() {
        // Si selectedCategory a été injecté par l'URL (via f:viewParam),
        // on s'assure que l'affichage démarre proprement.
        if (selectedCategory != null && !selectedCategory.trim().isEmpty()) {
            this.showAll = false;
        }
    }
    public List<Project> getLatestProjectsForHome() {
        List<Project> all = projectService.getAllProjects();

        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }

        // --- SEULE MODIFICATION : On trie par date du plus récent au plus ancien ---
        List<Project> sortedProjects = all.stream()
                .sorted((p1, p2) -> {
                    if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt()); // Tri descendant
                })
                .collect(Collectors.toList());

        // On limite à 3 maximum
        if (sortedProjects.size() > 3) {
            return sortedProjects.subList(0, 3);
        }
        return sortedProjects;
    }
    // Getters & Setters
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
    public String getSelectedCategory() { return selectedCategory; }
    public void setSelectedCategory(String selectedCategory) { this.selectedCategory = selectedCategory; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Project getSelectedProject() { return selectedProject; }
    public void setSelectedProject(Project selectedProject) { this.selectedProject = selectedProject; }
    public boolean isShowAll() { return showAll; }
    public void setShowAll(boolean showAll) { this.showAll = showAll; }
}