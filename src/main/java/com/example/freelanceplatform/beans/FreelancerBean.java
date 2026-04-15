package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class FreelancerBean implements Serializable {

    @Inject
    private UserService userService;

    private List<User> freelancers;
    private String searchQuery;

    // Couleurs pour les avatars
    private static final String[] COLORS = {
            "#C47D2B", "#8B5CF6", "#059669", "#DC2626",
            "#2563EB", "#D97706", "#7C3AED", "#0891B2"
    };

    @PostConstruct
    public void init() {
        // Charge tous les users depuis la BD
        freelancers = userService.findAll();
    }

    // Recherche par nom ou domaine
    public String search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            freelancers = userService.findAll();
        } else {
            freelancers = userService.searchByNomOrDomaine(searchQuery.trim());
        }
        return null; // reste sur la même page
    }

    // Génère les initiales du nom (ex: "John Doe" → "JD")
    public String getInitials(String nom) {
        if (nom == null || nom.isEmpty()) return "?";
        String[] parts = nom.trim().split("\\s+");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase()
                    + String.valueOf(parts[1].charAt(0)).toUpperCase();
        }
        return String.valueOf(nom.charAt(0)).toUpperCase();
    }

    // Génère une couleur basée sur le nom
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
}

