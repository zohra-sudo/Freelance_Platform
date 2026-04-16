package com.example.freelanceplatform.beans;
import com.example.freelanceplatform.entities.Candidature;
import java.util.stream.Collectors;
import com.example.freelanceplatform.dao.CandidatureDAO;
import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.entities.Candidature;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.entities.Poste;
import com.example.freelanceplatform.service.ProjectService;
import com.example.freelanceplatform.service.UserService;
import com.example.freelanceplatform.dao.UserDAO;
import com.example.freelanceplatform.dao.PosteDAO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ejb.EJB;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ProfilBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    @Inject
    private PhotoUploadBean photoUploadBean;

    @Inject
    private AuthBean authBean;

    @Inject
    private ProjectService projectService;
    @EJB
    private CandidatureDAO candidatureDAO;
    @EJB
    private UserDAO userDAO;

    @EJB
    private PosteDAO posteDAO;

    private Long userId;
    private User user;
    private String nouvelleLangue;
    private String nouvelleCompetence;
    private boolean enTrainDeModifier = false;

    private static final String[] COLORS = {
            "#C47D2B", "#8B5CF6", "#059669", "#DC2626",
            "#2563EB", "#D97706", "#7C3AED", "#0891B2"
    };

    public void loadUser() {
        if (userId != null) {
            user = userService.findById(userId);
            if (photoUploadBean != null) {
                photoUploadBean.setTargetUserId(userId);
            }
        }
    }

    public boolean isMonProfil() {
        return authBean != null && authBean.isConnecte()
                && user != null
                && authBean.getUserConnecte().getId().equals(user.getId());
    }

    // ========== PROJETS ==========

    public List<Project> getMesProjets() {
        if (user == null) return new ArrayList<>();
        return projectService.getProjectsByUserId(user.getId());
    }
    public List<Candidature> getReceivedApplications() {
        // 1. On récupère l'utilisateur connecté (Jihane)
        User currentUser = authBean.getUserConnecte();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        // 2. On récupère ses projets
        List<Project> myProjects = projectService.getProjectsByUserId(currentUser.getId());
        List<Candidature> allApplications = new ArrayList<>();

        // 3. On rassemble toutes les candidatures de tous ses projets
        if (myProjects != null) {
            for (Project p : myProjects) {
                if (p.getCandidatures() != null) {
                    allApplications.addAll(p.getCandidatures());
                }
            }
        }

        // Debug pour toi (à voir dans la console de WildFly)
        System.out.println("Nombre de candidatures trouvées : " + allApplications.size());

        return allApplications;
    }

    public void updateApplicationStatus(Candidature candidature, String newStatus) {
        try {
            if (candidature != null) {
                // On change le statut localement
                candidature.setStatut(newStatus);

                // SÉCURITÉ : On utilise le DAO pour sauver uniquement cette candidature
                // sans toucher à l'objet Project entier (évite les erreurs de cascade)
                candidatureDAO.update(candidature);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Status updated to " + newStatus, null));
            }
        } catch (Exception e) {
            // En cas d'erreur, on affiche le problème dans la console sans bloquer l'appli
            e.printStackTrace();
        }
    }
    // Redirection vers la page d'édition pour créer un nouveau projet
    public void nouveauProjet() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("editProject.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Redirection vers la page d'édition pour modifier un projet existant
    public void editerProjet(Long projectId) {
        try {
            if (projectId != null) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("editProject.xhtml?id=" + projectId);
            } else {
                nouveauProjet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== LANGUES ==========

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

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Langue supprimée avec succès", null));
    }

    // ========== COMPÉTENCES ==========

    public void ajouterCompetence() {
        if (nouvelleCompetence == null || nouvelleCompetence.trim().isEmpty()) return;
        if (!isMonProfil()) return;

        User u = authBean.getUserConnecte();
        if (u.getCompetences() == null) u.setCompetences(new ArrayList<>());

        String comp = nouvelleCompetence.trim();
        if (!u.getCompetences().contains(comp)) {
            u.getCompetences().add(comp);
            userService.modifierProfil(u);
            user = userService.findById(userId);
            authBean.setUserConnecte(user);
        }
        nouvelleCompetence = null;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Compétence ajoutée avec succès", null));
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

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Compétence supprimée avec succès", null));
    }

    // ========== BIO ==========

    public void updateBio() {
        if (!isMonProfil()) return;

        try {
            userService.modifierProfil(user);
            authBean.setUserConnecte(user);
            enTrainDeModifier = false;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Bio mise à jour avec succès", null));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur lors de la mise à jour de la bio", null));
        }
    }

    // ========== POSTES (JOBS) ==========

    public List<Poste> getMesPostesCrees() {
        if (user == null) return new ArrayList<>();
        return posteDAO.findByClient(user.getId());
    }

    public void supprimerPoste(Long posteId) {
        if (!isMonProfil()) return;

        Poste p = posteDAO.findById(posteId);
        if (p != null) {
            posteDAO.delete(p);
            user = userService.findById(userId);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Poste supprimé avec succès", null));
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========

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

    // ========== GETTERS & SETTERS ==========

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getNouvelleLangue() { return nouvelleLangue; }
    public void setNouvelleLangue(String nouvelleLangue) { this.nouvelleLangue = nouvelleLangue; }

    public String getNouvelleCompetence() { return nouvelleCompetence; }
    public void setNouvelleCompetence(String nouvelleCompetence) { this.nouvelleCompetence = nouvelleCompetence; }

    public boolean isEnTrainDeModifier() { return enTrainDeModifier; }
    public void setEnTrainDeModifier(boolean enTrainDeModifier) { this.enTrainDeModifier = enTrainDeModifier; }
}