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
import com.example.freelanceplatform.entities.Poste;
import com.example.freelanceplatform.dao.PosteDAO;
import java.util.ArrayList;
import java.util.List;
import jakarta.faces.view.ViewScoped;
import jakarta.ejb.EJB;
import com.example.freelanceplatform.dao.UserDAO;
@Named
@ViewScoped
public class ProfilBean implements Serializable {

    @Inject private UserService userService;
    @Inject private PhotoUploadBean photoUploadBean;
    @Inject private AuthBean authBean;
    @Inject private ProjectService projectService;
    private String nouvelleCompetence;
    @EJB
    private UserDAO UserDAO;
    @EJB
    private PosteDAO posteDAO;
    private Long userId;
    private User user;
    private String nouvelleLangue;
    private Project projectEnEdition;
    private boolean enTrainDeModifier = false;
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
    public List<Poste> getMesPostesCrees() {
        if (user == null) return new ArrayList<>();
        // Supprime le [cite: 3] ici
        return posteDAO.findByClient(user.getId());
    }

    public void supprimerPoste(Long posteId) {
        if (!isMonProfil()) return;
        // Supprime le [cite: 2] ici
        Poste p = posteDAO.findById(posteId);
        if (p != null) {
            // Supprime le [cite: 2] ici
            posteDAO.delete(p);
            user = userService.findById(userId); // Rafraîchir
        }
    }
    public void updateBio() {
        try {
            this.user = UserDAO.update(this.user);
            // On repasse en mode lecture côté Bean
            this.enTrainDeModifier = false;
            System.out.println("DEBUG: Bio mise à jour en base de données");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ajouterCompetence() {
        if (nouvelleCompetence == null || nouvelleCompetence.trim().isEmpty()) return;
        if (!isMonProfil()) return;

        User u = authBean.getUserConnecte();
        if (u.getCompetences() == null) u.setCompetences(new ArrayList<>());

        String comp = nouvelleCompetence.trim();
        if (!u.getCompetences().contains(comp)) {
            u.getCompetences().add(comp);
            userService.modifierProfil(u); // On sauvegarde
            user = userService.findById(userId); // On rafraîchit l'objet local
            authBean.setUserConnecte(user); // On met à jour la session
        }
        nouvelleCompetence = null; // On vide le champ
    }
    public void supprimerCompetence(String comp) {
        if (!isMonProfil()) return;

        User u = authBean.getUserConnecte();
        if (u.getCompetences() != null) {
            u.getCompetences().remove(comp);
            userService.modifierProfil(u);
            user = userService.findById(userId);
            authBean.setUserConnecte(user);
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

        if (user.getLangues() == null) user.setLangues(new ArrayList<>());
        String langue = nouvelleLangue.trim();

        if (!user.getLangues().contains(langue)) {
            user.getLangues().add(langue);
            // Utilisation de UserDAO pour sauvegarder physiquement en base
            this.user = UserDAO.update(this.user);
            // Mise à jour de la session pour rester synchronisé
            authBean.setUserConnecte(this.user);
        }
        nouvelleLangue = null;
    }

    public void supprimerLangue(String langue) {
        if (!isMonProfil()) return;

        if (user.getLangues() != null) {
            user.getLangues().remove(langue);
            // Utilisation de UserDAO pour sauvegarder physiquement en base
            this.user = UserDAO.update(this.user);
            // Mise à jour de la session
            authBean.setUserConnecte(this.user);
        }
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
    public boolean isEnTrainDeModifier() { return enTrainDeModifier; }
    public void setEnTrainDeModifier(boolean enTrainDeModifier) { this.enTrainDeModifier = enTrainDeModifier; }
    public String getNouvelleCompetence() { return nouvelleCompetence; }
    public void setNouvelleCompetence(String nc) { this.nouvelleCompetence = nc; }
}
