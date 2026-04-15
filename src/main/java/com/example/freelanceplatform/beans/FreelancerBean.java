package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class FreelancerBean implements Serializable {

    @Inject
    private UserService userService;

    private List<User> freelancers;
    private List<User> allFreelancers;
    private String searchQuery;

    // Filtres
    private List<String> selectedCategories = new ArrayList<>();
    private Integer budgetMin;
    private Integer budgetMax;
    private List<String> selectedExperience = new ArrayList<>();
    private List<String> selectedAvailability = new ArrayList<>();

    private List<String> availableCategories = List.of(
            "Web Development", "Mobile App", "UI/UX Design",
            "Graphic Design", "Video Editing", "Digital Marketing"
    );

    private List<String> availableExperience = List.of("Entry Level", "Intermediate", "Expert", "Top Rated");
    private List<String> availableAvailability = List.of("Available Now", "Part-Time", "Full-Time");

    private static final String[] COLORS = {
            "#C47D2B", "#8B5CF6", "#059669", "#DC2626",
            "#2563EB", "#D97706", "#7C3AED", "#0891B2"
    };

    @PostConstruct
    public void init() {
        // Lire les paramètres de l'URL
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String categoriesParam = params.get("categories");
        if (categoriesParam != null && !categoriesParam.isEmpty()) {
            selectedCategories = Arrays.asList(categoriesParam.split(","));
        }

        String budgetMinParam = params.get("budgetMin");
        if (budgetMinParam != null && !budgetMinParam.isEmpty()) {
            try {
                budgetMin = Integer.parseInt(budgetMinParam);
            } catch (NumberFormatException e) {}
        }

        String budgetMaxParam = params.get("budgetMax");
        if (budgetMaxParam != null && !budgetMaxParam.isEmpty()) {
            try {
                budgetMax = Integer.parseInt(budgetMaxParam);
            } catch (NumberFormatException e) {}
        }

        String experienceParam = params.get("experience");
        if (experienceParam != null && !experienceParam.isEmpty()) {
            selectedExperience = Arrays.asList(experienceParam.split(","));
        }

        String availabilityParam = params.get("availability");
        if (availabilityParam != null && !availabilityParam.isEmpty()) {
            selectedAvailability = Arrays.asList(availabilityParam.split(","));
        }

        // Charger et filtrer les données
        loadAndFilter();
    }

    private void loadAndFilter() {
        allFreelancers = userService.findAll();
        applyFilters();
    }

    public String search() {
        applyFilters();
        return null;
    }

    public void applyFilters() {
        if (allFreelancers == null) {
            allFreelancers = userService.findAll();
        }

        freelancers = new ArrayList<>(allFreelancers);

        // Filtre par recherche textuelle
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            freelancers = freelancers.stream()
                    .filter(u -> (u.getNom() != null && u.getNom().toLowerCase().contains(query)) ||
                            (u.getDomaine() != null && u.getDomaine().toLowerCase().contains(query)) ||
                            (u.getCompetences() != null && u.getCompetences().stream().anyMatch(c -> c.toLowerCase().contains(query))))
                    .collect(Collectors.toList());
        }

        // Filtre par catégories
        if (selectedCategories != null && !selectedCategories.isEmpty()) {
            freelancers = freelancers.stream()
                    .filter(u -> u.getDomaine() != null && selectedCategories.contains(u.getDomaine()))
                    .collect(Collectors.toList());
        }
    }

    public void resetFilters() {
        selectedCategories.clear();
        selectedExperience.clear();
        selectedAvailability.clear();
        budgetMin = null;
        budgetMax = null;
        applyFilters();
    }

    public String getInitials(String nom) {
        if (nom == null || nom.isEmpty()) return "?";
        String[] parts = nom.trim().split("\\s+");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase()
                    + String.valueOf(parts[1].charAt(0)).toUpperCase();
        }
        return String.valueOf(nom.charAt(0)).toUpperCase();
    }

    public String getAvatarColor(String nom) {
        if (nom == null || nom.isEmpty()) return COLORS[0];
        int index = Math.abs(nom.hashCode()) % COLORS.length;
        return COLORS[index];
    }

    // Getters & Setters
    public List<User> getFreelancers() { return freelancers; }
    public void setFreelancers(List<User> freelancers) { this.freelancers = freelancers; }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public List<String> getSelectedCategories() { return selectedCategories; }
    public void setSelectedCategories(List<String> selectedCategories) { this.selectedCategories = selectedCategories; }

    public Integer getBudgetMin() { return budgetMin; }
    public void setBudgetMin(Integer budgetMin) { this.budgetMin = budgetMin; }

    public Integer getBudgetMax() { return budgetMax; }
    public void setBudgetMax(Integer budgetMax) { this.budgetMax = budgetMax; }

    public List<String> getSelectedExperience() { return selectedExperience; }
    public void setSelectedExperience(List<String> selectedExperience) { this.selectedExperience = selectedExperience; }

    public List<String> getSelectedAvailability() { return selectedAvailability; }
    public void setSelectedAvailability(List<String> selectedAvailability) { this.selectedAvailability = selectedAvailability; }

    public List<String> getAvailableCategories() { return availableCategories; }
    public List<String> getAvailableExperience() { return availableExperience; }
    public List<String> getAvailableAvailability() { return availableAvailability; }
}