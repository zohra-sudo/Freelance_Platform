package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.ProjectService;
import com.example.freelanceplatform.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class ProfilBean implements Serializable {

    @Inject private UserService userService;
    @Inject private PhotoUploadBean photoUploadBean;
    @Inject private AuthBean authBean;
    @Inject private ProjectService projectService;

    private Long userId;
    private User user;
    private String nouvelleLangue;
    private Project projectEnEdition;

    private static final String[] COLORS = {
            "#C47D2B", "#8B5CF6", "#059669", "#DC2626",
            "#2563EB", "#D97706", "#7C3AED", "#0891B2"
    };

    public void loadUser() {
        if (userId != null) {
            user = userService.findById(userId);
            photoUploadBean.setTargetUserId(userId);
        }
    }

    public boolean isMonProfil() {
        return authBean.isConnecte()
                && user != null
                && authBean.getUserConnecte().getId().equals(user.getId());
    }

    public List<Project> getMesProjets() {
        if (user == null) return new ArrayList<>();
        return projectService.getProjectsByUserId(user.getId());
    }

    public void ajouterLangue() {
        if (nouvelleLangue == null || nouvelleLangue.trim().isEmpty()) return;
        if (!isMonProfil()) return;
        User u = authBean.getUserConnecte();
        if (u.getLangues() == null) u.setLangues(new ArrayList<>());
        String langue = nouvelleLangue.trim();
        if (!u.getLangues().contains(langue)) {
            u.getLangues().add(langue);
            userService.modifierProfil(u);
            user = userService.findById(userId);
            authBean.setUserConnecte(user);
        }
        nouvelleLangue = null;
        // Ajouter un message de succès
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Langue ajoutée avec succès", null));
    }

    public void supprimerLangue(String langue) {
        if (!isMonProfil()) return;
        User u = authBean.getUserConnecte();
        if (u.getLangues() != null) {
            u.getLangues().remove(langue);
            userService.modifierProfil(u);
            user = userService.findById(userId);
            authBean.setUserConnecte(user);
        }
        // Ajouter un message de succès
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Langue supprimée avec succès", null));
    }

    // Méthode pour éditer un projet (appelée depuis le bouton Edit)
    public void editerProjet(Long projectId) {
        if (projectId == null) {
            projectEnEdition = new Project();
            projectEnEdition.setAuthor(authBean.getUserConnecte());
        } else {
            projectEnEdition = projectService.findById(projectId);
            if (projectEnEdition != null && !projectEnEdition.getAuthor().getId().equals(authBean.getUserConnecte().getId())) {
                projectEnEdition = null;
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vous ne pouvez pas modifier ce projet", null));
                return;
            }
        }
        // Rediriger vers la page d'édition
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("editProject.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour créer un nouveau projet
    public void nouveauProjet() {
        projectEnEdition = new Project();
        projectEnEdition.setAuthor(authBean.getUserConnecte());
        // Rediriger vers la page d'ajout de projet
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("editProject.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour sauvegarder le projet (appelée depuis editProject.xhtml)
    public void sauvegarderProjet() {
        if (!isMonProfil() || projectEnEdition == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur: vous n'êtes pas autorisé", null));
            return;
        }

        projectEnEdition.setAuthor(authBean.getUserConnecte());

        try {
            if (projectEnEdition.getId() == null) {
                projectService.addProject(projectEnEdition);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Projet ajouté avec succès", null));
            } else {
                projectService.updateProject(projectEnEdition);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Projet modifié avec succès", null));
            }
            projectEnEdition = null;
            // Recharger le profil
            FacesContext.getCurrentInstance().getExternalContext().redirect("profil.xhtml?id=" + authBean.getUserConnecte().getId());
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur lors de la sauvegarde du projet: " + e.getMessage(), null));
        }
    }

    // Méthode pour annuler l'édition et revenir au profil
    public void annulerEdition() {
        projectEnEdition = null;
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("profil.xhtml?id=" + authBean.getUserConnecte().getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInitials() {
        if (user == null || user.getNom() == null) return "?";
        String[] parts = user.getNom().trim().split("\\s+");
        if (parts.length >= 2)
            return (String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0))).toUpperCase();
        return String.valueOf(user.getNom().charAt(0)).toUpperCase();
    }

    public String getAvatarColor() {
        if (user == null || user.getNom() == null) return COLORS[0];
        return COLORS[Math.abs(user.getNom().hashCode()) % COLORS.length];
    }

    // Getters et Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getNouvelleLangue() { return nouvelleLangue; }
    public void setNouvelleLangue(String v) { this.nouvelleLangue = v; }
    public Project getProjectEnEdition() { return projectEnEdition; }
    public void setProjectEnEdition(Project p) { this.projectEnEdition = p; }
}