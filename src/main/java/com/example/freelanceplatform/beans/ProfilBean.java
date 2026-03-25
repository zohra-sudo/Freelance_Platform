package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.User;

import com.example.freelanceplatform.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class ProfilBean implements Serializable {

    @Inject
    private UserService userService;
    @Inject
    private PhotoUploadBean photoUploadBean;

    private Long userId;
    private User user;

    private static final String[] COLORS = {
            "#C47D2B", "#8B5CF6", "#059669", "#DC2626",
            "#2563EB", "#D97706", "#7C3AED", "#0891B2"
    };

    // Appelé automatiquement par f:viewAction après f:viewParam
    public void loadUser() {
        if (userId != null) {
            user = userService.findById(userId);
            photoUploadBean.setTargetUserId(userId);
        }
    }

    // Génère les initiales (ex: "John Doe" → "JD")
    public String getInitials() {
        if (user == null || user.getNom() == null) return "?";
        String[] parts = user.getNom().trim().split("\\s+");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase()
                    + String.valueOf(parts[1].charAt(0)).toUpperCase();
        }
        return String.valueOf(user.getNom().charAt(0)).toUpperCase();
    }

    // Génère une couleur pour l'avatar
    public String getAvatarColor() {
        if (user == null || user.getNom() == null) return COLORS[0];
        int index = Math.abs(user.getNom().hashCode()) % COLORS.length;
        return COLORS[index];
    }

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

